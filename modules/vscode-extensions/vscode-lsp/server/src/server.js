"use strict";
/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = y[op[0] & 2 ? "return" : op[0] ? "throw" : "next"]) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [0, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
exports.__esModule = true;
var vscode_languageserver_1 = require("vscode-languageserver");
var rpc = require("vscode-ws-jsonrpc");
// Create a connection for the server. The connection uses Node's IPC as a transport.
// Also include all preview / proposed LSP features.
var connection = vscode_languageserver_1.createConnection(vscode_languageserver_1.ProposedFeatures.all);
// Create a simple text document manager. The text document manager
// supports full document sync only
var documents = new vscode_languageserver_1.TextDocuments();
var hasConfigurationCapability = false;
var hasWorkspaceFolderCapability = false;
var hasDiagnosticRelatedInformationCapability = false;
// var ws = createWebsocket();
function createWebsocket() {
    var WebSocket = require('ws');
    var webSocket = new WebSocket('ws://localhost:8080/lsp/lsp');
    rpc.listen({
        webSocket: webSocket,
        onConnection: function (rpcConnection) {
            var notification = new rpc.NotificationType('testNotification');
            rpcConnection.listen();
            rpcConnection.sendNotification(notification, 'Hello World');
        }
    });
    return webSocket;
}
connection.onInitialize(function (params) {
    var capabilities = params.capabilities;
    // Does the client support the `workspace/configuration` request?
    // If not, we will fall back using global settings
    hasConfigurationCapability = !!(capabilities.workspace && !!capabilities.workspace.configuration);
    hasWorkspaceFolderCapability = !!(capabilities.workspace && !!capabilities.workspace.workspaceFolders);
    hasDiagnosticRelatedInformationCapability = !!(capabilities.textDocument &&
        capabilities.textDocument.publishDiagnostics &&
        capabilities.textDocument.publishDiagnostics.relatedInformation);
    return {
        capabilities: {
            textDocumentSync: documents.syncKind,
            // Tell the client that the server supports code completion
            completionProvider: {
                resolveProvider: true
            }
        }
    };
});
connection.onInitialized(function () {
    if (hasConfigurationCapability) {
        // Register for all configuration changes.
        connection.client.register(vscode_languageserver_1.DidChangeConfigurationNotification.type, undefined);
    }
    // var WebSocket = require('ws');
    // var webSocket = new WebSocket('ws://localhost:8080/lsp/lsp');
    // var initialize = rpc.listen({
    // 	webSocket,
    // 	onConnection: (rpcConnection: rpc.MessageConnection) => {
    // 		const notification = new rpc.NotificationType<string, string>('onInitialize');
    // 		rpcConnection.listen();
    // 		rpcConnection.sendNotification(notification, 'hii');
    // 	},
    // });
});
// The global settings, used when the `workspace/configuration` request is not supported by the client.
// Please note that this is not the case when using this server with the client provided in this example
// but could happen with other clients.
var defaultSettings = { maxNumberOfProblems: 1000 };
var globalSettings = defaultSettings;
// Cache the settings of all open documents
var documentSettings = new Map();
connection.onDidChangeConfiguration(function (change) {
    if (hasConfigurationCapability) {
        // Reset all cached document settings
        documentSettings.clear();
    }
    else {
        globalSettings = ((change.settings.languageServerExample || defaultSettings));
    }
    // Revalidate all open text documents
    documents.all().forEach(validateTextDocument);
});
function getDocumentSettings(resource) {
    if (!hasConfigurationCapability) {
        return Promise.resolve(globalSettings);
    }
    var result = documentSettings.get(resource);
    if (!result) {
        result = connection.workspace.getConfiguration({
            scopeUri: resource,
            section: 'languageServerExample'
        });
        documentSettings.set(resource, result);
    }
    return result;
}
// Only keep settings for open documents
documents.onDidClose(function (e) {
    documentSettings["delete"](e.document.uri);
});
// The content of a text document has changed. This event is emitted
// when the text document first opened or when its content has changed.
documents.onDidChangeContent(function (change) {
    validateTextDocument(change.document);
});
function validateTextDocument(textDocument) {
    return __awaiter(this, void 0, void 0, function () {
        var settings, text, pattern, m, problems, diagnostics, diagnostic;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, getDocumentSettings(textDocument.uri)];
                case 1:
                    settings = _a.sent();
                    text = textDocument.getText();
                    pattern = /\b[A-Z]{2,}\b/g;
                    problems = 0;
                    diagnostics = [];
                    while ((m = pattern.exec(text)) && problems < settings.maxNumberOfProblems) {
                        problems++;
                        diagnostic = {
                            severity: vscode_languageserver_1.DiagnosticSeverity.Warning,
                            range: {
                                start: textDocument.positionAt(m.index),
                                end: textDocument.positionAt(m.index + m[0].length)
                            },
                            message: m[0] + " is all Uppercase.",
                            source: 'ex'
                        };
                        if (hasDiagnosticRelatedInformationCapability) {
                            diagnostic.relatedInformation = [
                                {
                                    location: {
                                        uri: textDocument.uri,
                                        range: Object.assign({}, diagnostic.range)
                                    },
                                    message: 'Spelling matters'
                                },
                                {
                                    location: {
                                        uri: textDocument.uri,
                                        range: Object.assign({}, diagnostic.range)
                                    },
                                    message: 'Particularly for names'
                                }
                            ];
                        }
                        diagnostics.push(diagnostic);
                    }
                    // Send the computed diagnostics to VSCode.
                    connection.sendDiagnostics({ uri: textDocument.uri, diagnostics: diagnostics });
                    return [2 /*return*/];
            }
        });
    });
}
connection.onDidChangeWatchedFiles(function (_change) {
    // Monitored files have change in VSCode
    connection.console.log('We received an file change event');
});
// This handler provides the initial list of the completion items.
connection.onCompletion(function (_textDocumentPosition) {
    // The pass parameter contains the position of the text document in
    // which code complete got requested. For the example we ignore this
    // info and always provide the same completion items.
    // var WebSocket = require('ws');
    // var webSocket = new WebSocket('ws://localhost:8080/lsp/lsp');
    // var initialize = "null";
    // rpc.listen({
    // 	webSocket,
    // 	onConnection: (rpcConnection: rpc.MessageConnection) => {
    // 		const notification = new rpc.NotificationType<String, void>('onInitialize');
    // 		rpcConnection.listen();
    // 		rpcConnection.sendNotification(notification, "complition");
    // 	},
    // });
    var WebSocket = require('ws');
    // var WebSocket = new WebSocket('ws://localhost:8080/lsp/lsp');
    var host = "localhost", port = 8080, server = new WebSocket("ws://localhost:8080/lsp/lsp");
    console.log("1. listening on " + host + ":" + port);
    console.log("hello " + server.onInitialized);
    server.on('open', function connection(ws) {
        console.log("2. client connected");
        var connection = createMessageConnection(createRPCSocket(ws));
        connection.onError(function (_a) {
            var err = _a[0];
            return console.error("error", err.stack);
        });
        connection.onClose(function () { return console.log("CLOSED"); });
        connection.listen();
        var notification = new rpc.NotificationType('testNotification');
        connection.onNotification(notification, function (param) {
            console.log("3. got notification", param);
            console.log("4. sending request...");
            var answer = connection.sendRequest(new rpc.RequestType0("testRequest"));
            console.log("5. got testRequest answer", answer);
            console.log("DONE");
            connection.dispose();
            setTimeout(function () { return process.exit(0); }, 400);
        });
    });
    // const socket:rpc.IWebSocket = new WebSocket('ws://localhost:8080/lsp/lsp');
    // const reader = new rpc.WebSocketMessageReader(socket);
    // const writer = new rpc.WebSocketMessageWriter(socket);
    // const logger = new rpc.ConsoleLogger();
    // const connection = rpc.createMessageConnection(reader, writer, logger);
    // const notification = new rpc.NotificationType<string, void>('onInitialize');
    // rpc.listen({
    // 	webSocket,
    // 	onConnection: (rpcConnection: rpc.MessageConnection) => {
    // 		const notification = new rpc.NotificationType<string, void>('onInitialize');
    // 		rpcConnection.listen();
    // 		rpcConnection.onNotification(notification, (param: string) => {
    // 			initialize = param; // This prints Hello World
    // 		});
    // 	},
    // });
    return [
        {
            label: "initialize",
            kind: vscode_languageserver_1.CompletionItemKind.Text,
            data: 1
        },
        {
            label: 'JavaScript',
            kind: vscode_languageserver_1.CompletionItemKind.Text,
            data: 2
        }
    ];
});
// This handler resolves additional information for the item selected in
// the completion list.
connection.onCompletionResolve(function (item) {
    if (item.data === 1) {
        item.detail = 'TypeScript details';
        item.documentation = 'TypeScript documentation';
    }
    else if (item.data === 2) {
        item.detail = 'JavaScript details';
        item.documentation = 'JavaScript documentation';
    }
    return item;
});
/*
connection.onDidOpenTextDocument((params) => {
    // A text document got opened in VSCode.
    // params.uri uniquely identifies the document. For documents store on disk this is a file URI.
    // params.text the initial full content of the document.
    connection.console.log(`${params.textDocument.uri} opened.`);
});
connection.onDidChangeTextDocument(onInitialize(params) => {
    // The content of a text documeonInitializent did change in VSCode.
    // params.uri uniquely identifionInitializees the document.
    // params.contentChanges descrionInitializebe the content changes to the document.
    connection.console.log(`${params.textDocument.uri} changed: ${JSON.stringify(params.contentChanges)}`);
});
connection.onDidCloseTextDocument((params) => {
    // A text document got closed in VSCode.
    // params.uri uniquely identifies the document.
    connection.console.log(`${params.textDocument.uri} closed.`);
});
*/
// Make the text document manager listen on the connection
// for open, change and close text document events
documents.listen(connection);
// Listen on the connection
connection.listen();
function createMessageConnection(socket) {
    var reader = new rpc.WebSocketMessageReader(socket), writer = new rpc.WebSocketMessageWriter(socket), logger = new rpc.ConsoleLogger(), connection = rpc.createMessageConnection(reader, writer, logger);
    return connection;
}
function createRPCSocket(websocket) {
    var onMessageCallbacks = [];
    var onErrorCallbacks = [];
    var onCloseCallbacks = [];
    var socket = {
        send: function (content) { websocket.send(content); },
        dispose: function () { websocket.close(); },
        onMessage: function (cb) { onMessageCallbacks.push(); },
        onError: function (cb) { onErrorCallbacks.push(); },
        onClose: function (cb) { onCloseCallbacks.push(); }
    };
    websocket.on('message', function (message) {
        return onMessageCallbacks.forEach(function (cb) { return cb(message); });
    });
    websocket.on('error', function (err) { return onErrorCallbacks.forEach(function (cb) { return cb(err); }); });
    websocket.on('close', function (number, reason) { return onCloseCallbacks.forEach(function (cb) { return cb(number, reason); }); });
    return socket;
}
