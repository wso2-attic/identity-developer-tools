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

import {basename} from "path";
import {
    Breakpoint,
    BreakpointEvent,
    Handles,
    InitializedEvent,
    logger,
    Logger,
    LoggingDebugSession,
    OutputEvent,
    Scope,
    Source,
    StackFrame,
    StoppedEvent,
    TerminatedEvent,
    Thread,
} from "vscode-debugadapter";
import {DebugProtocol} from "vscode-debugprotocol";
import {DebugConstants} from "./DebugConstants";
import {PreviewManager} from "./lspModules/PreviewManager";
import {IRemoteBreakpoint, RemoteIdentityServerRuntime} from "./remoteIdentityServerRuntime";
import path = require("path");
import format = require("string-template");

const {Subject} = require("await-notify");

function timeout(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
}

/**
 * This interface describes the mock-debug specific launch attributes
 * (which are not part of the Debug Adapter Protocol).
 * The schema for these attributes lives in the package.json of the mock-debug extension.
 * The interface should always match this schema.
 */
interface ILaunchRequestArguments extends DebugProtocol.LaunchRequestArguments {
    /** An absolute path to the "program" to debug. */
    program: string;
    /** Automatically stop target after launch. If not specified, target does not stop. */
    stopOnEntry?: boolean;
    /** enable logging the Debug Adapter Protocol */
    trace?: boolean;
}

export class IdentityServerDebugSession extends LoggingDebugSession {

    // we don"t support multiple threads, so we can use a hardcoded ID for the default thread
    private static THREAD_ID = 1;

    /**
     * The remote Websocket based connection to IAM Server
     */
    private readonly iamRemoteRuntime: RemoteIdentityServerRuntime;

    private variableHandles = new Handles<string>();

    private configurationDone = new Subject();

    private cancellationTokens = new Map<number, boolean>();

    /**
     * Creates a new debug adapter that is used for one debug session.
     * We configure the default implementation of a debug adapter here.
     */
    public constructor() {
        super("mock-debug.txt");

        // this debugger uses zero-based lines and columns
        this.setDebuggerLinesStartAt1(false);
        this.setDebuggerColumnsStartAt1(false);

        this.iamRemoteRuntime = new RemoteIdentityServerRuntime();

        this.setupEventHandlers(this.iamRemoteRuntime);
    }

    /**
     * The "initialize" request is the first request called by the frontend
     * To interrogate the features the debug adapter provides.
     */
    protected initializeRequest(response: DebugProtocol.InitializeResponse,
                                args: DebugProtocol.InitializeRequestArguments): void {

        // build and return the capabilities of this debug adapter:
        response.body = response.body || {};

        // the adapter implements the configurationDoneRequest.
        response.body.supportsConfigurationDoneRequest = true;

        // Currently we do not support "evaluate" when hovering over source
        response.body.supportsEvaluateForHovers = false;

        // We do not support Step-Back
        response.body.supportsStepBack = false;

        // make VS Code to support data breakpoints
        response.body.supportsDataBreakpoints = true;

        // We do not support REPL
        response.body.supportsCompletionsRequest = false;
        response.body.completionTriggerCharacters = [".", "["];

        // make VS Code to send cancelRequests
        response.body.supportsCancelRequest = true;

        // make VS Code send the breakpointLocations request
        response.body.supportsBreakpointLocationsRequest = true;

        this.sendResponse(response);

        // since this debug adapter can accept configuration requests like "setBreakpoint" at any time,
        // we request them early by sending an "initializeRequest" to the frontend.
        // The frontend will end the configuration sequence by calling "configurationDone" request.
        this.sendEvent(new InitializedEvent());
    }

    /**
     * Called at the end of the configuration sequence.
     * Indicates that all breakpoints etc. have been sent to the DA and that the "launch" can start.
     */
    protected configurationDoneRequest(response: DebugProtocol.ConfigurationDoneResponse,
                                       args: DebugProtocol.ConfigurationDoneArguments): void {

        super.configurationDoneRequest(response, args);
        // notify the launchRequest that configuration has finished
        this.configurationDone.notify();
    }

    protected async launchRequest(response: DebugProtocol.LaunchResponse, args: ILaunchRequestArguments) {

        // make sure to "Stop" the buffered logging if "trace" is not set
        logger.setup(args.trace ? Logger.LogLevel.Verbose : Logger.LogLevel.Stop, false);

        // wait until configuration has finished (and configurationDoneRequest has been called)
        await this.configurationDone.wait(1000);

        // start the program in the runtime
        // this._runtime.start(args.program, !!args.stopOnEntry);
        this.iamRemoteRuntime.start(args.program, !!args.stopOnEntry);

        this.sendResponse(response);
    }

    protected setBreakPointsRequest(response: DebugProtocol.SetBreakpointsResponse,
                                    args: DebugProtocol.SetBreakpointsArguments): void {

        const filePath = args.source.path as string;
        const clientLines = args.lines || [];

        // clear all breakpoints for this file
        this.iamRemoteRuntime.clearBreakpoints(filePath);

        // set and verify breakpoint locations
        const actualBreakpoints = clientLines.map((l) => {
            const {verified, line, id} = this.iamRemoteRuntime.setBreakPoint(filePath,
                this.convertClientLineToDebugger(l), args);
            const bp = new Breakpoint(verified, this.convertDebuggerLineToClient(line)) as DebugProtocol.Breakpoint;
            bp.id = id;
            return bp;
        });

        // send back the actual breakpoint positions
        response.body = {
            breakpoints: actualBreakpoints,
        };
        this.sendResponse(response);
    }

    protected breakpointLocationsRequest(response: DebugProtocol.BreakpointLocationsResponse,
                                         args: DebugProtocol.BreakpointLocationsArguments,
                                         request?: DebugProtocol.Request): void {

        if (args.source.path) {
            const bps = this.iamRemoteRuntime.getBreakpoints(args.source.path,
                this.convertClientLineToDebugger(args.line));
            response.body = {
                breakpoints: bps.map((col) => {
                    return {
                        column: this.convertDebuggerColumnToClient(col),
                        line: args.line,
                    };
                }),
            };
        } else {
            response.body = {
                breakpoints: [],
            };
        }
        this.sendResponse(response);
    }

    protected threadsRequest(response: DebugProtocol.ThreadsResponse): void {

        // runtime supports no threads so just return a default thread.
        response.body = {
            threads: [
                new Thread(IdentityServerDebugSession.THREAD_ID, "thread 1"),
            ],
        };
        this.sendResponse(response);
    }

    protected stackTraceRequest(response: DebugProtocol.StackTraceResponse,
                                args: DebugProtocol.StackTraceArguments): void {

        const startFrame = typeof args.startFrame === "number" ? args.startFrame : 0;
        const maxLevels = typeof args.levels === "number" ? args.levels : 1000;
        const endFrame = startFrame + maxLevels;
        const stk = this.iamRemoteRuntime.stack(startFrame, endFrame);
        response.body = {
            stackFrames: stk.frames.map((f) => new StackFrame(f.index, f.name,
                this.createSource(f.file), this.convertDebuggerLineToClient(f.line))),
            totalFrames: stk.count,
        };
        this.sendResponse(response);
    }

    protected scopesRequest(response: DebugProtocol.ScopesResponse, args: DebugProtocol.ScopesArguments): void {

        response.body = {
            scopes: [
                new Scope("Local", this.variableHandles.create("local"), false),
                new Scope("Global", this.variableHandles.create("global"), true),
            ],
        };
        this.sendResponse(response);
    }

    protected escapeHtml(unsafe) {

        return unsafe
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    protected async variablesRequest(response: DebugProtocol.VariablesResponse,
                                     args: DebugProtocol.VariablesArguments, request?: DebugProtocol.Request) {
        // TODO follow the Nested Mechanism
        const variables: DebugProtocol.Variable[] = [];
        if (this.iamRemoteRuntime != null) {
            // Create the remote legible request and await the response
            const answer = this.iamRemoteRuntime.fetchVariables(response, args, request);
            answer.then((remoteResponse) => {
                remoteResponse.body.variables.forEach((element) => {

                    if (element.name === DebugConstants.SAML_REQUEST
                        && !element.value.includes("NO SAML Request Added")) {
                        const viewPanelHolderDictonary = PreviewManager.getInstance().getPreviewManagers();
                        const viewPanelHolder = viewPanelHolderDictonary.get(
                            path.parse(this.iamRemoteRuntime.getSourceFile()).name);
                        const panel = viewPanelHolder.getPanel();
                        const currentHtml = viewPanelHolder.getCurrentHtml();
                        const newHtml = format(currentHtml, {
                            SAML_REQUEST: this.escapeHtml(element.value),
                            SAML_RESPONSE: DebugConstants.SAML_RESPONSE_HTML,
                        });
                        panel.webview.html = newHtml;
                        viewPanelHolder.setCurrentHtml(newHtml);
                    } else if (element.name === DebugConstants.SAML_RESPONSE) {
                        const viewPanelHolderDictonary = PreviewManager.getInstance().getPreviewManagers();
                        const viewPanelHolder = viewPanelHolderDictonary.get(path.parse(
                            this.iamRemoteRuntime.getSourceFile()).name);
                        const panel = viewPanelHolder.getPanel();
                        const currentHtml = viewPanelHolder.getCurrentHtml();
                        const newHtml = format(currentHtml, {
                            SAML_RESPONSE: this.escapeHtml(element.value),

                        });
                        panel.webview.html = newHtml;
                        viewPanelHolder.setCurrentHtml(newHtml);
                    }

                    element.type = typeof (element.value);
                    element.variablesReference = args.variablesReference;
                    element.value = JSON.stringify(element.value);
                    variables.push(element);
                });

                response.body = {
                    variables,
                };
                this.sendResponse(response);
            });
        } else {
            // No remote connetion exists. Create dummy variables.
            const id = this.variableHandles.get(args.variablesReference);

            if (id) {
                variables.push({
                    name: id + "_i",
                    type: "integer",
                    value: "123",
                    variablesReference: 0,
                });
            }

            response.body = {
                variables,
            };

            this.sendResponse(response);
        }
    }

    protected continueRequest(response: DebugProtocol.ContinueResponse, args: DebugProtocol.ContinueArguments): void {

        this.iamRemoteRuntime.continue();
        this.sendResponse(response);
    }

    protected reverseContinueRequest(response: DebugProtocol.ReverseContinueResponse,
                                     args: DebugProtocol.ReverseContinueArguments): void {

        this.iamRemoteRuntime.continue(true);
        this.sendResponse(response);
    }

    protected nextRequest(response: DebugProtocol.NextResponse, args: DebugProtocol.NextArguments): void {

        this.iamRemoteRuntime.step();
        this.sendResponse(response);
    }

    protected stepBackRequest(response: DebugProtocol.StepBackResponse, args: DebugProtocol.StepBackArguments): void {

        this.iamRemoteRuntime.step(true);
        this.sendResponse(response);
    }

    protected evaluateRequest(response: DebugProtocol.EvaluateResponse, args: DebugProtocol.EvaluateArguments): void {

        let reply: string | undefined;
        response.body = {
            result: reply ? reply : `evaluate(context: "${args.context}", "${args.expression}")`,
            variablesReference: 0,
        };
        this.sendResponse(response);
    }

    protected dataBreakpointInfoRequest(response: DebugProtocol.DataBreakpointInfoResponse,
                                        args: DebugProtocol.DataBreakpointInfoArguments): void {

        response.body = {
            accessTypes: undefined,
            canPersist: false,
            dataId: null,
            description: "cannot break on data access",
        };

        if (args.variablesReference && args.name) {
            const id = this.variableHandles.get(args.variablesReference);
            if (id.startsWith("global_")) {
                response.body.dataId = args.name;
                response.body.description = args.name;
                response.body.accessTypes = ["read"];
                response.body.canPersist = false;
            }
        }

        this.sendResponse(response);
    }

    protected setDataBreakpointsRequest(response: DebugProtocol.SetDataBreakpointsResponse,
                                        args: DebugProtocol.SetDataBreakpointsArguments): void {

        // clear all data breakpoints
        this.iamRemoteRuntime.clearAllDataBreakpoints();

        response.body = {
            breakpoints: [],
        };

        for (const dbp of args.breakpoints) {
            // assume that id is the "address" to break on
            const ok = this.iamRemoteRuntime.setDataBreakpoint(dbp.dataId);
            response.body.breakpoints.push({
                verified: ok,
            });
        }

        this.sendResponse(response);
    }

    protected completionsRequest(response: DebugProtocol.CompletionsResponse,
                                 args: DebugProtocol.CompletionsArguments): void {

        response.body = {
            targets: [
                {
                    label: "item 10",
                    sortText: "10",
                },
                {
                    label: "item 1",
                    sortText: "01",
                },
                {
                    label: "item 2",
                    sortText: "02",
                },
            ],
        };
        this.sendResponse(response);
    }

    protected cancelRequest(response: DebugProtocol.CancelResponse, args: DebugProtocol.CancelArguments) {

        if (args.requestId) {
            this.cancellationTokens.set(args.requestId, true);
        }
    }

    /**
     * Sets up the event handlers for the DAP, which we are interested in to intercept.
     *
     * @param RemoteIdentityServerRuntime The Runtime for debugging remote Identity Server via WebSocket connections.
     */
    private setupEventHandlers(remoteIdentityServerRuntime: RemoteIdentityServerRuntime): void {

        remoteIdentityServerRuntime.on("stopOnEntry", () => {
            this.sendEvent(new StoppedEvent("entry", IdentityServerDebugSession.THREAD_ID));
        });
        remoteIdentityServerRuntime.on("stopOnStep", () => {
            this.sendEvent(new StoppedEvent("step", IdentityServerDebugSession.THREAD_ID));
        });
        remoteIdentityServerRuntime.on("stopOnBreakpoint", () => {
            this.sendEvent(new StoppedEvent("breakpoint", IdentityServerDebugSession.THREAD_ID));
        });
        remoteIdentityServerRuntime.on("stopOnDataBreakpoint", () => {
            this.sendEvent(new StoppedEvent("data breakpoint", IdentityServerDebugSession.THREAD_ID));
        });
        remoteIdentityServerRuntime.on("stopOnException", () => {
            this.sendEvent(new StoppedEvent("exception", IdentityServerDebugSession.THREAD_ID));
        });
        remoteIdentityServerRuntime.on("breakpointValidated", (bp: IRemoteBreakpoint) => {
            this.sendEvent(new BreakpointEvent("changed", {
                id: bp.id,
                verified: bp.verified,
            } as DebugProtocol.Breakpoint));
        });
        remoteIdentityServerRuntime.on("output", (text, filePath, line, column) => {
            const e: DebugProtocol.OutputEvent = new OutputEvent(`${text}\n`);
            e.body.source = this.createSource(filePath);
            e.body.line = this.convertDebuggerLineToClient(line);
            e.body.column = this.convertDebuggerColumnToClient(column);
            this.sendEvent(e);
        });
        remoteIdentityServerRuntime.on("end", () => {
            this.sendEvent(new TerminatedEvent());
        });
    }

    // ---- helpers

    private createSource(filePath: string): Source {

        return new Source(basename(filePath), this.convertDebuggerPathToClient(filePath),
            undefined, undefined, "mock-adapter-data");
    }
}
