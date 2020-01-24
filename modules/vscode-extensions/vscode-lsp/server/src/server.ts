/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */

import {
	createConnection,
	TextDocuments,
	TextDocument,
	Diagnostic,
	DiagnosticSeverity,
	ProposedFeatures,
	InitializeParams,
	DidChangeConfigurationNotification,
	CompletionItem,
	TextDocumentPositionParams
} from 'vscode-languageserver';
import * as xml2js from 'xml2js';
// import {SnippetString} from 'vscode';
import * as rpc from 'vscode-ws-jsonrpc';
import * as path from 'path';
const fs = require('fs');
const WebSocket = require('ws');
// Create a connection for the server. The connection uses Node's IPC as a transport.
// Also include all preview / proposed LSP features.
let connection = createConnection(ProposedFeatures.all);

// Create a simple text document manager. The text document manager
// supports full document sync only
let documents: TextDocuments = new TextDocuments();

let hasConfigurationCapability: boolean = false;
let hasWorkspaceFolderCapability: boolean = false;
let hasDiagnosticRelatedInformationCapability: boolean = false;
declare var webSocket: any;
declare var text: String;
var rootpath: any;
var executeStepCount: any = 0;
const completionTemplates: { label: string; detail: any; insertText: any; }[] = [];
connection.onInitialize(async (params: InitializeParams) => {
	rootpath = params.rootPath;
	//generateFUnctionLibraries();
	getTemplates();

	let capabilities = params.capabilities;
	// Does the client support the `workspace/configuration` request?
	// If not, we will fall back using global settings
	hasConfigurationCapability = !!(
		capabilities.workspace && !!capabilities.workspace.configuration
	);
	hasWorkspaceFolderCapability = !!(
		capabilities.workspace && !!capabilities.workspace.workspaceFolders
	);
	hasDiagnosticRelatedInformationCapability = !!(
		capabilities.textDocument &&
		capabilities.textDocument.publishDiagnostics &&
		capabilities.textDocument.publishDiagnostics.relatedInformation
	);

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

connection.onInitialized(() => {
	if (hasConfigurationCapability) {
		// Register for all configuration changes.
		connection.client.register(DidChangeConfigurationNotification.type, undefined);
	}
});

// The example settings
interface ExampleSettings {
	maxNumberOfProblems: number;
}

// The global settings, used when the `workspace/configuration` request is not supported by the client.
// Please note that this is not the case when using this server with the client provided in this example
// but could happen with other clients.
const defaultSettings: ExampleSettings = { maxNumberOfProblems: 1000 };
let globalSettings: ExampleSettings = defaultSettings;

// Cache the settings of all open documents
let documentSettings: Map<string, Thenable<ExampleSettings>> = new Map();

connection.onDidChangeConfiguration(change => {
	if (hasConfigurationCapability) {
		// Reset all cached document settings
		documentSettings.clear();
	} else {
		globalSettings = <ExampleSettings>(
			(change.settings.languageServerExample || defaultSettings)
		);
	}

	// Revalidate all open text documents
	documents.all().forEach(validateTextDocument);
});

function getDocumentSettings(resource: string): Thenable<ExampleSettings> {
	if (!hasConfigurationCapability) {
		return Promise.resolve(globalSettings);
	}
	let result = documentSettings.get(resource);
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
documents.onDidClose(e => {
	documentSettings.delete(e.document.uri);
});

var text: String;
var file: String;
var extension: string;
// The content of a text document has changed. This event is emitted
// when the text document first opened or when its content has changed.
documents.onDidChangeContent(change => {
	validateTextDocument(change.document);
	text = change.document.getText();
	file = change.document.uri;
	var xtension = file.split('.').pop();
	if (xtension == "authjs") {
		var fileName = path.basename(String(file)).replace(/\.[^/.]+$/, "");
		setExecuteStep(String(fileName));
	}
	extension = String(xtension);
});

documents.onDidOpen(params => {
	file = params.document.uri;
	var fileName = path.basename(String(file)).replace(/\.[^/.]+$/, "");
	setExecuteStep(String(fileName));
});

async function validateTextDocument(textDocument: TextDocument): Promise<void> {

	// The validator creates diagnostics for all uppercase words length 2 and more
	let text = textDocument.getText();
	let diagnostics: Diagnostic[] = [];
	let pattern = new RegExp(`executeStep\\([${executeStepCount + 1}-9]*\\)`, "g");
	let m: RegExpExecArray | null;
	while ((m = pattern.exec(text))) {
		let diagnostic: Diagnostic = {
			severity: DiagnosticSeverity.Error,
			range: {
				start: textDocument.positionAt(m.index),
				end: textDocument.positionAt(m.index + m[0].length)
			},
			message: `Could not find matching Authentication Step for script executeStep`,
			source: 'wso2 IAM'
		};
		diagnostics.push(diagnostic);
	}
	var functionRegex = new RegExp("onLoginRequest\\([a-zA-Z_0-9_$][^)]*\\)", "g");
	var functionRegexForNewJDK = new RegExp("varonLoginRequest=function\\([a-zA-Z_0-9_$][^)]*\\)", "g");

	// To check whether onLoginRequest is there.
	if (!text.trim().replace(/(?:\r\n|\r|\n)/g, '').replace(/\s/g, '').match(functionRegex) && !text.trim().replace(/(?:\r\n|\r|\n)/g, '').replace(/\s/g, '').match(functionRegexForNewJDK)) {
		let diagnostic: any = {
			severity: DiagnosticSeverity.Error,
			range: {
				start: textDocument.positionAt(0),
				end: textDocument.positionAt(0)
			},
			message: `Missing required function: onLoginRequest(parameter)`,
			source: 'wso2 IAM'
		};
		diagnostics.push(diagnostic);
	}
	// Send the computed diagnostics to VSCode.
	connection.sendDiagnostics({ uri: textDocument.uri, diagnostics });
}

connection.onDidChangeWatchedFiles(_change => {
	// Monitored files have change in VSCode
	connection.console.log('We received an file change event');
});
var recieveData: any = "";
// This handler provides the initial list of the completion items.
let completionList: CompletionItem[] = [];
connection.onCompletion(
	async (_textDocumentPosition: TextDocumentPositionParams): Promise<CompletionItem[]> => {
		// The pass parameter contains the position of the text document in
		// which code complete got requested. For the example we ignore this
		// info and always provide the same completion items.		
		var webSocket = new WebSocket('wss://localhost:9443/lsp/lsp', { rejectUnauthorized: false });
		if (extension === "authjs") {
			var obj: any = {
				"text": text,
				"line": _textDocumentPosition.position.line + 1,
				"character": _textDocumentPosition.position.character,
				"path": file
			};
			var output = <JSON>obj;
			rpc.listen({
				webSocket,
				onConnection: (rpcConnection: rpc.MessageConnection) => {
					const notification = new rpc.NotificationType<any, void>('onCompletion');
					rpcConnection.listen();
					rpcConnection.sendNotification(notification, JSON.stringify(output));
				},
			});

			await webSocket.on('message', function incoming(data: any) {
				let obj = JSON.parse(data);
				recieveData = obj.result;
				var jsonData = JSON.parse(JSON.stringify(obj.result.re));
				completionList = [];
				for (var completionTemplate in completionTemplates) {
					completionList.push(completionTemplates[completionTemplate]);
				}
				for (var i = 0; i < jsonData.length; i++) {
					var counter = jsonData[i];
					var jsonob = {
						label: String(counter.label),
						kind: counter.kind,
						insertText: counter.insertText,
					};
					completionList.push(jsonob);
				}
			});
			return completionList;

		} else {
			completionList = [];
			return completionList;
		}

	}
);

// This handler resolves additional information for the item selected in
// the completion list.
connection.onCompletionResolve(
	(item: CompletionItem): CompletionItem => {
		if (item.data === 1) {
			item.detail = 'TypeScript details';
			item.documentation = 'TypeScript documentation';
		} else if (item.data === 2) {
			item.detail = 'JavaScript details';
			item.documentation = 'JavaScript documentation';
		}
		return item;
	}
);

// Make the text document manager listen on the connection
// for open, change and close text document events
documents.listen(connection);

// Listen on the connection
connection.listen();

// To set the stepCount.
function setExecuteStep(fileName: string): void {

	var fileNames: any[] = []; // To keep the names of the file.
	var files; // Array of available files in the directory
	files = getFilesFromDir(path.join(rootpath, 'IAM', 'Apps'), [".authxml"]);
	files.forEach(file => {
		fileNames.push(path.basename(file).replace(/\.[^/.]+$/, ""));
	});
	var xmlFile = path.join(rootpath, 'IAM', 'Apps', files[fileNames.indexOf(fileName)]);
	var xml = String(fs.readFileSync(xmlFile, 'utf8'));
	var parser = new xml2js.Parser({ explicitArray: false });
	parser.parseString(xml, function (err: any, result: any) {
		// Check whether AuthenticationScripts node is Available.
		if (Array.isArray(result.ServiceProvider.LocalAndOutBoundAuthenticationConfig.AuthenticationSteps.AuthenticationStep)) {
			executeStepCount = Object.keys(result.ServiceProvider.LocalAndOutBoundAuthenticationConfig.AuthenticationSteps.AuthenticationStep).length;
		} else {
			executeStepCount = Object.keys(result.ServiceProvider.LocalAndOutBoundAuthenticationConfig.AuthenticationSteps).length;
		}
	});
}
// To get the list of files in the Directory.
function getFilesFromDir(dir: string, fileTypes: string[]): any[] {
	var filesToReturn: any[] = [];
	function walkDir(currentPath: any) {
		var files = fs.readdirSync(currentPath);
		for (var i in files) {
			var curFile = path.join(currentPath, files[i]);
			if (fs.statSync(curFile).isFile() && fileTypes.indexOf(path.extname(curFile)) != -1) {
				filesToReturn.push(curFile.replace(dir, ''));
			} else if (fs.statSync(curFile).isDirectory()) {
				walkDir(curFile);
			}
		}
	}
	walkDir(dir);
	return filesToReturn;
}

// To get the adaptive script templates.
async function getTemplates() {
	const templates = path.join(__dirname, '..', 'src', 'adaptiveTemplates/');
	try {
		fs.readdirSync(templates).forEach((file: any) => {
			var filepath = templates + file;
			fs.readFile(filepath, 'utf8', function (err: any, data: any) {
				if (err) throw err;
				var obj = JSON.parse(String(data));
				var codeArray = obj.code;
				var description = obj.summary;
				var name = obj.name;
				var code: String = "";
				for (var i in codeArray) {
					code += codeArray[i] + "\n";
				}
				var snippentObj = {
					label: name,
					detail: description,
					insertText: code
				};
				completionTemplates.push(snippentObj);
			});
		});
	} catch (err) {
		console.log(err);
	}

}