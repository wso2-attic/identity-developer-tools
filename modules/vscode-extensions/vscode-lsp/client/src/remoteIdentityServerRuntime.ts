/* --------------------------------------------------------------------------------------------
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ------------------------------------------------------------------------------------------ */

import {EventEmitter} from "events";
import {readFileSync} from "fs";
import * as vscode from "vscode";
import {DebugProtocol} from "vscode-debugprotocol";
import * as rpc from "vscode-ws-jsonrpc";
import {Config} from "./Config";
import {DebugConstants} from "./DebugConstants";
import keytar = require("keytar");
import WebSocket = require("ws");

/**
 * Remote Breakpoint, communicated over Websocket and DAP.
 */
export interface IRemoteBreakpoint {
    id: number;
    line: number;
    verified: boolean;
}

/**
 * Runtime implementation for debuging remote Identity Server via WebSocket connections.
 * Websocket can be made to local or remote server.
 * There will be only one debug connection need to be maintained per server.
 */
export class RemoteIdentityServerRuntime extends EventEmitter {

    private webSocket: WebSocket;
    private messageConnection: rpc.MessageConnection;

    // Source file.Currently only one file. TODO: make debug available for multiple files
    private sourceFile: string;
    // the contents (= lines) of the one and only file. TODO: Have a map of source file to lines
    private sourceLines: string[];
    // This is the next line that will be 'executed' .TODO: Have a map of source file to lines
    private currentLine = 0;

    // maps from sourceFile to array of  breakpoints
    private breakPoints = new Map<string, IRemoteBreakpoint[]>();

    // since we want to send breakpoint events, we will assign an id to every event
    // so that the frontend can match events with breakpoints.
    private breakpointId = 1;

    private breakAddresses = new Set<string>();

    constructor() {

        super();
    }

    /**
     * This is a getter which gets the sourceFile.
     */
    public getSourceFile() {

        return this.sourceFile;
    }

    /**
     * Start executing the given program.
     *
     * @param program the name of the program debugging.
     * @param stopOnEntry whether to stop on the entry.
     */
    public start(program: string, stopOnEntry: boolean) {

        this.connectWebsocket();

        this.currentLine = -1;

        this.verifyBreakpoints(this.sourceFile);

        if (stopOnEntry) {
            // we step once
            this.step(false, DebugConstants.DEBUG_STOP_ON_ENTRY);
        } else {
            // we just start to run until we hit a breakpoint or an exception
            this.continue();
        }
    }

    /**
     * Method to set the break points.
     *
     * @param path Logical full path to the module.
     * @param line Start line of range to search possible breakpoint locations in.
     * @param args List of arguments. The first argument is the command to run.
     */
    public setBreakPoint(path: string, line: number, args: DebugProtocol.SetBreakpointsArguments): IRemoteBreakpoint {

        const bp = {verified: false, line, id: this.breakpointId++} as IRemoteBreakpoint;
        let bps = this.breakPoints.get(path);
        if (!bps) {
            bps = new Array<IRemoteBreakpoint>();
            this.breakPoints.set(path, bps);
        }
        bps.push(bp);

        this.verifyBreakpoints(path);

        if (this.messageConnection != null) {
            const notification = new rpc.NotificationType(DebugConstants.DEBUG_SET_BREAKPOINT);
            this.messageConnection.sendNotification(notification, args);
        }
        return bp;
    }

    /**
     * Clear all breakpoints for file.
     *
     * @param path Logical full path to the module.
     */
    public clearBreakpoints(path: string): void {

        if (this.messageConnection != null) {
            const notification = new rpc.NotificationType(DebugConstants.DEBUG_CLEAR_BREAKPOINT);
            this.messageConnection.sendNotification(notification);
        }
        this.breakPoints.delete(path);
    }

    /**
     * Method to get the break points.
     *
     * @param path Logical full path to the module.
     * @param line Start line of range to search possible breakpoint locations in.
     */
    public getBreakpoints(path: string, line: number): number[] {

        const l = this.sourceLines[line];
        let sawSpace = true;
        const bps: number[] = [];
        for (let i = 0; i < l.length; i++) {
            if (l[i] !== " ") {
                if (sawSpace) {
                    bps.push(i);
                    sawSpace = false;
                }
            } else {
                sawSpace = true;
            }
        }

        return bps;
    }

    /**
     * Creates the variable request and returns the promise which can be used to perform the results on the request.
     *
     * @param response Response for fetch Variables request.
     * @param args List of arguments. The first argument is the command to run.
     * @param request for fetch Variables.
     */
    public fetchVariables(response: DebugProtocol.VariablesResponse, args: DebugProtocol.VariablesArguments,
                          request?: DebugProtocol.Request): Thenable<DebugProtocol.VariablesResponse> {

        const variablesRequest = new rpc.RequestType1<DebugProtocol.VariablesArguments,
            DebugProtocol.VariablesResponse, DebugProtocol.ErrorResponse, DebugProtocol.Request>("variables");
        return this.messageConnection.sendRequest(variablesRequest, args);
    }

    /**
     * Returns a fake 'stacktrace' where every 'stack frame' is a word from the current line.
     */
    public stack(startFrame: number, endFrame: number): any {

        const words = this.sourceLines[this.currentLine].trim().split(/\s+/);
        const frames = new Array<any>();
        // every word of the current line becomes a stack frame.
        for (let i = startFrame; i < Math.min(endFrame, words.length); i++) {
            const name = words[i];	// use a word of the line as the stackframe name
            frames.push({
                file: this.sourceFile,
                index: i,
                line: this.currentLine,
                name: `${name}(${i})`,
            });
        }
        return {
            count: words.length,
            frames,
        };
    }

    /**
     * Continue execution to the end/beginning.
     */
    public continue(reverse = false) {

        const notification = new rpc.NotificationType(DebugConstants.DEBUG_CONTINUE);
        this.messageConnection.sendNotification(notification);
        this.run(reverse, undefined);
    }

    /**
     * Step to the next/previous non empty line.
     */
    public step(reverse = false, event = DebugConstants.DEBUG_STOP_ON_STEP) {

        this.run(reverse, event);
    }

    /**
     * Set data breakpoint.
     *
     * @param address The address of the first byte of data returned.
     */
    public setDataBreakpoint(address: string): boolean {
        if (address) {
            this.breakAddresses.add(address);
            return true;
        }
        return false;
    }

    /*
     * Clear all data breakpoints.
     */
    public clearAllDataBreakpoints(): void {
        this.breakAddresses.clear();
    }

    private sendEvent(event: string, ...args: any[]) {

        setImmediate((_) => {
            this.emit(event, ...args);
        });
    }

    private fireBreakpoint(ln: number) {

        // send 'stopped' event
        this.currentLine = ln;
        this.sendEvent(DebugConstants.DEBUG_STOP_ON_BREAKPOINT);
    }

    /**
     * Run through the file.
     * If stepEvent is specified only run a single step and emit the stepEvent.
     */
    private run(reverse = false, stepEvent?: string) {

        if (reverse) {
            for (let ln = this.currentLine - 1; ln >= 0; ln--) {
                if (this.fireEventsForLine(ln, stepEvent)) {
                    this.currentLine = ln;
                    return;
                }
            }
            // no more lines: stop at first line
            this.currentLine = 0;
            this.sendEvent(DebugConstants.DEBUG_STOP_ON_ENTRY);
        } else {
            for (let ln = this.currentLine + 1; ln < this.sourceLines.length; ln++) {
                if (this.fireEventsForLine(ln, stepEvent)) {
                    this.currentLine = ln;
                    return true;
                }
            }
            // no more lines: run to end
            this.sendEvent(DebugConstants.DEBUG_END);
        }
    }

    private verifyBreakpoints(path: string): void {

        const bps = this.breakPoints.get(path);
        if (bps) {
            this.loadSource(path);
            bps.forEach((bp) => {
                if (!bp.verified && bp.line < this.sourceLines.length) {
                    const srcLine = this.sourceLines[bp.line].trim();

                    /* If a line is empty or starts with '+' we don't allow to set a breakpoint but move the
                     breakpoint down */
                    if (srcLine.length === 0 || srcLine.indexOf("+") === 0) {
                        bp.line++;
                    }
                    // If a line starts with '-' we don't allow to set a breakpoint but move the breakpoint up
                    if (srcLine.indexOf("-") === 0) {
                        bp.line--;
                    }
                    // don't set 'verified' to true if the line contains the word 'lazy'
                    // in this case the breakpoint will be verified 'lazy' after hitting it once.
                    if (srcLine.indexOf("lazy") < 0) {
                        bp.verified = true;
                        this.sendEvent(DebugConstants.DEBUG_BREAKPOINT_VALIDATED, bp);
                    }
                }
            });
        }
    }

    /**
     * Fire events if line has a breakpoint or the word 'exception' is found.
     * Returns true is execution needs to stop.
     */
    private fireEventsForLine(ln: number, stepEvent?: string): boolean {

        const line = this.sourceLines[ln].trim();
        // if 'log(...)' found in source -> send argument to debug console
        const matches = /log\((.*)\)/.exec(line);
        if (matches && matches.length === 2) {
            this.sendEvent("output", matches[1], this.sourceFile, ln, matches.index);
        }

        // if a word in a line matches a data breakpoint, fire a 'dataBreakpoint' event
        const words = line.split(" ");
        for (const word of words) {
            if (this.breakAddresses.has(word)) {
                this.sendEvent(DebugConstants.DEBUG_STOP_ON_DATA_BREAKPOINT);
                return true;
            }
        }

        // if word 'exception' found in source -> throw exception
        if (line.indexOf(DebugConstants.DEBUG_EXCEPTION) >= 0) {
            this.sendEvent(DebugConstants.DEBUG_STOP_ON_EXCEPTION);
            return true;
        }

        // is there a breakpoint?
        const breakpoints = this.breakPoints.get(this.sourceFile);
        if (breakpoints) {
            const bps = breakpoints.filter((bp) => bp.line === ln);
            if (bps.length > 0) {

                // send 'stopped' event
                this.sendEvent(DebugConstants.DEBUG_STOP_ON_EXCEPTION);

                // the following shows the use of 'breakpoint' events to update properties of a breakpoint in the UI
                // if breakpoint is not yet verified, verify it now and send a 'breakpoint' update event
                if (!bps[0].verified) {
                    bps[0].verified = true;
                    this.sendEvent(DebugConstants.DEBUG_BREAKPOINT_VALIDATED, bps[0]);
                }
                return true;
            }
        }

        // non-empty line
        if (stepEvent && line.length > 0) {
            this.sendEvent(stepEvent);
            return true;
        }

        // nothing interesting found -> continue
        return false;
    }

    private loadSource(file: string) {
        if (this.sourceFile !== file) {
            this.sourceFile = file;
            this.sourceLines = readFileSync(this.sourceFile).toString().split("\n");
        }
    }

    private async connectWebsocket() {

        let accessToken;
        const secret = keytar.getPassword(DebugConstants.ACCESS_TOKEN, DebugConstants.ACCESS_TOKEN);
        await secret.then((result) => {
            accessToken = result;
        });

        const options = {
            headers: {
                Authorization: "Bearer " + accessToken,
            },
            rejectUnauthorized: false,
        };

        const webSocket = new WebSocket(Config.WEBSOCKET_END_POINT, options);
        this.webSocket = webSocket;
        vscode.window.showInformationMessage("Debug Session Started..");
        rpc.listen({
            onConnection: (rpcConnection: rpc.MessageConnection) => {
                this.messageConnection = rpcConnection;
                const breakpointNotification = new rpc.NotificationType<string, void>("breakpoint");
                rpcConnection.onNotification(breakpointNotification, (param: any) => {
                    this.fireBreakpoint(param.line);
                });
                rpcConnection.listen();
            }, webSocket,
        });

    }
}
