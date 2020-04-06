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

import { EventEmitter } from 'events';
import { DebugProtocol } from 'vscode-debugprotocol';
import { readFileSync } from 'fs';
import * as rpc from 'vscode-ws-jsonrpc';
import * as vscode from "vscode";
import {Config} from "./Config";

const keytar = require('keytar');
var WebSocket = require('ws');

/**
 * Remote Breakpoint, communicated over Websocket and DAP.
 */
export interface RemoteBreakpoint {
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

	private  webSocket: WebSocket;
	private messageConnection: rpc.MessageConnection;

	// Source file.Currently only one file. TODO: make debug available for multiple files
	private _sourceFile: string;
	// the contents (= lines) of the one and only file. TODO: Have a map of source file to lines
	private _sourceLines: string[];
	// This is the next line that will be 'executed' .TODO: Have a map of source file to lines
	private _currentLine = 0;

	// maps from sourceFile to array of  breakpoints
	private _breakPoints = new Map<string, RemoteBreakpoint[]>();

	// since we want to send breakpoint events, we will assign an id to every event
	// so that the frontend can match events with breakpoints.
	private _breakpointId = 1;

	private _breakAddresses = new Set<string>();

	constructor() {

		super();
	}


	public getSourceFile(){

		return this._sourceFile;
	}

	public start(program: string, stopOnEntry: boolean) {

		this.connectWebsocket();

		this._currentLine = -1;

		this.verifyBreakpoints(this._sourceFile);

		if (stopOnEntry) {
			// we step once
			this.step(false, 'stopOnEntry');
		} else {
			// we just start to run until we hit a breakpoint or an exception
			this.continue();
		}
	}

	public setBreakPoint(path: string, line: number, args: DebugProtocol.SetBreakpointsArguments) : RemoteBreakpoint {

		const bp = <RemoteBreakpoint> { verified: false, line, id: this._breakpointId++ };
		let bps = this._breakPoints.get(path);
		if (!bps) {
			bps = new Array<RemoteBreakpoint>();
			this._breakPoints.set(path, bps);
		}
		bps.push(bp);

		this.verifyBreakpoints(path);

		if(this.messageConnection != null) {
			var notification = new rpc.NotificationType("setBreakpoint");
			this.messageConnection.sendNotification(notification, args);
		}


		return bp;
	}

	/*
	 * Clear all breakpoints for file.
	 */
	public clearBreakpoints(path: string): void {

		if(this.messageConnection != null) {
			var notification = new rpc.NotificationType("clearBreakpoints");
			this.messageConnection.sendNotification(notification);
		}
		this._breakPoints.delete(path);
	}

	public getBreakpoints(path: string, line: number): number[] {

		const l = this._sourceLines[line];
		let sawSpace = true;
		const bps: number[] = [];
		for (let i = 0; i < l.length; i++) {
			if (l[i] !== ' ') {
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
	 * Creates the variable request and returns the promise which can be used to perform the results on the request
	 * @param response
	 * @param args
	 * @param request
	 */
	public fetchVariables(response: DebugProtocol.VariablesResponse, args: DebugProtocol.VariablesArguments, request?: DebugProtocol.Request) :Thenable<DebugProtocol.VariablesResponse> {

		var varaiablesRequest = new rpc.RequestType1<DebugProtocol.VariablesArguments,DebugProtocol.VariablesResponse, DebugProtocol.ErrorResponse,  DebugProtocol.Request>("variables");
		var answer = this.messageConnection.sendRequest(varaiablesRequest,args);
		return answer;
	}

	/**
	 * Returns a fake 'stacktrace' where every 'stackframe' is a word from the current line.
	 */
	public stack(startFrame: number, endFrame: number): any {

		const words = this._sourceLines[this._currentLine].trim().split(/\s+/);
		const frames = new Array<any>();
		// every word of the current line becomes a stack frame.
		for (let i = startFrame; i < Math.min(endFrame, words.length); i++) {
			const name = words[i];	// use a word of the line as the stackframe name
			frames.push({
				index: i,
				name: `${name}(${i})`,
				file: this._sourceFile,
				line: this._currentLine
			});
		}
		return {
			frames: frames,
			count: words.length
		};
	}

	/**
	 * Continue execution to the end/beginning.
	 */
	public continue(reverse = false) {

		var notification = new rpc.NotificationType("continue");
		this.messageConnection.sendNotification(notification);
		this.run(reverse, undefined);
	}

	/**
	 * Step to the next/previous non empty line.
	 */
	public step(reverse = false, event = 'stopOnStep') {

		this.run(reverse, event);
	}

	private sendEvent(event: string, ... args: any[]) {

		setImmediate(_ => {
			this.emit(event, ...args);
		});
	}

	private fireBreakpoint(ln: number) {

		// send 'stopped' event
		this._currentLine = ln;
		this.sendEvent('stopOnBreakpoint');
	}

	/**
	 * Run through the file.
	 * If stepEvent is specified only run a single step and emit the stepEvent.
	 */
	private run(reverse = false, stepEvent?: string) {

		if (reverse) {
			for (let ln = this._currentLine-1; ln >= 0; ln--) {
				if (this.fireEventsForLine(ln, stepEvent)) {
					this._currentLine = ln;
					return;
				}
			}
			// no more lines: stop at first line
			this._currentLine = 0;
			this.sendEvent('stopOnEntry');
		} else {
			for (let ln = this._currentLine+1; ln < this._sourceLines.length; ln++) {
				if (this.fireEventsForLine(ln, stepEvent)) {
					this._currentLine = ln;
					return true;
				}
			}
			// no more lines: run to end
			this.sendEvent('end');
		}
	}

	private verifyBreakpoints(path: string) : void {

		let bps = this._breakPoints.get(path);
		if (bps) {
			this.loadSource(path);
			bps.forEach(bp => {
				if (!bp.verified && bp.line < this._sourceLines.length) {
					const srcLine = this._sourceLines[bp.line].trim();

					// if a line is empty or starts with '+' we don't allow to set a breakpoint but move the breakpoint down
					if (srcLine.length === 0 || srcLine.indexOf('+') === 0) {
						bp.line++;
					}
					// if a line starts with '-' we don't allow to set a breakpoint but move the breakpoint up
					if (srcLine.indexOf('-') === 0) {
						bp.line--;
					}
					// don't set 'verified' to true if the line contains the word 'lazy'
					// in this case the breakpoint will be verified 'lazy' after hitting it once.
					if (srcLine.indexOf('lazy') < 0) {
						bp.verified = true;
						this.sendEvent('breakpointValidated', bp);
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

		const line = this._sourceLines[ln].trim();
		// if 'log(...)' found in source -> send argument to debug console
		const matches = /log\((.*)\)/.exec(line);
		if (matches && matches.length === 2) {
			this.sendEvent('output', matches[1], this._sourceFile, ln, matches.index);
		}

		// if a word in a line matches a data breakpoint, fire a 'dataBreakpoint' event
		const words = line.split(" ");
		for (let word of words) {
			if (this._breakAddresses.has(word)) {
				this.sendEvent('stopOnDataBreakpoint');
				return true;
			}
		}

		// if word 'exception' found in source -> throw exception
		if (line.indexOf('exception') >= 0) {
			this.sendEvent('stopOnException');
			return true;
		}

		// is there a breakpoint?
		const breakpoints = this._breakPoints.get(this._sourceFile);
		if (breakpoints) {
			const bps = breakpoints.filter(bp => bp.line === ln);
			if (bps.length > 0) {

				// send 'stopped' event
				this.sendEvent('stopOnBreakpoint');

				// the following shows the use of 'breakpoint' events to update properties of a breakpoint in the UI
				// if breakpoint is not yet verified, verify it now and send a 'breakpoint' update event
				if (!bps[0].verified) {
					bps[0].verified = true;
					this.sendEvent('breakpointValidated', bps[0]);
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

	/*
	 * Set data breakpoint.
	 */
	public setDataBreakpoint(address: string): boolean {
		if (address) {
			this._breakAddresses.add(address);
			return true;
		}
		return false;
	}

	/*
	 * Clear all data breakpoints.
	 */
	public clearAllDataBreakpoints(): void {
		this._breakAddresses.clear();
	}

	private loadSource(file: string) {
		if (this._sourceFile !== file) {
			this._sourceFile = file;
			this._sourceLines = readFileSync(this._sourceFile).toString().split('\n');
		}
	}

	private async connectWebsocket() {

		var acessToken;
		var secret = keytar.getPassword("acessToken", "acessToken");
		await secret.then((result) => {
			acessToken = result;
		});

		var options = {
			headers: {
				Authorization: 'Bearer ' + acessToken,
			},
			rejectUnauthorized: false
		};

		var webSocket = new WebSocket(Config.WEBSOCKET_END_POINT,options);
		this.webSocket = webSocket;
		vscode.window.showInformationMessage("Debug Session Started..");
		rpc.listen({
			webSocket,
			onConnection: (rpcConnection: rpc.MessageConnection) => {
				this.messageConnection = rpcConnection;
				let breakpointNotification = new rpc.NotificationType<string, void>('breakpoint');
				let connectedNotification = new rpc.NotificationType<string, void>('connected');
				let continueNotification = new rpc.NotificationType<string, void>('continue');

				rpcConnection.onNotification(breakpointNotification, (param: any) => {
					console.log("Got notification Breakpoint.. ");
					this.fireBreakpoint(param.line);
				});
				rpcConnection.onNotification(connectedNotification, (param: any) => {
					console.log("Got notification Connected .. "+param );
				});
				rpcConnection.onNotification(continueNotification, (param: any) => {
					console.log("Got notification Continue.. ");
				});
				rpcConnection.onNotification( (param: any) => {
					console.log("Got notification any .. "+param );
				});
				rpcConnection.onRequest((param: any) => {
					console.log("Got Request.. "+param );
				});
				rpcConnection.onError((param: any) => {
					console.log("Got Error.. "+param );
				});

				rpcConnection.listen();
			}
		});

	}
}
