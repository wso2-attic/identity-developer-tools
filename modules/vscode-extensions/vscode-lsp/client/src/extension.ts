/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */

import * as path from 'path';
import { workspace, ExtensionContext,  WorkspaceFolder, DebugConfiguration, ProviderResult, CancellationToken } from 'vscode';
import { IdentityServerDebugSession } from './identityServerDebug';
import * as vscode from 'vscode';
import {
	LanguageClient,
	LanguageClientOptions,
	ServerOptions,
	TransportKind
} from 'vscode-languageclient';
let client: LanguageClient;
import * as fs from 'fs';
import * as Net from 'net';
import { Http2ServerRequest } from 'http2';
const xmlQuery = require('xml-query');
const XmlReader = require('xml-reader');
const ClientOAuth2 = require('client-oauth2');
const axios = require('axios');
var xmlFilePath;

/*
 * Set the following compile time flag to true if the
 * debug adapter should run inside the extension host.
 * Please note: the test suite does not (yet) work in this mode.
 */
const EMBED_DEBUG_ADAPTER = true;
export function activate(context: ExtensionContext) {
	// The server is implemented in node
	let serverModule = context.asAbsolutePath(
		path.join('server', 'out', 'server.js')
	);
	// The debug options for the server
	// --inspect=6009: runs the server in Node's Inspector mode so VS Code can attach to the server for debugging
	let debugOptions = { execArgv: ['--nolazy', '--inspect=6009'] };

	// If the extension is launched in debug mode then the debug server options are used
	// Otherwise the run options are used
	let serverOptions: ServerOptions = {
		run: { module: serverModule, transport: TransportKind.ipc },
		debug: {
			module: serverModule,
			transport: TransportKind.ipc,
			options: debugOptions
		}
	};

	// Options to control the language client
	let clientOptions: LanguageClientOptions = {
		// Register the server for plain text documents		
		documentSelector: [{ scheme: 'file', language: 'javascript' }],
		synchronize: {
			// Notify the server about file changes to '.clientrc files contained in the workspace
			fileEvents: workspace.createFileSystemWatcher('**/.clientrc')
		}
	};

	context.subscriptions.push(
		
		vscode.commands.registerCommand('extension.diagram', () => {
			const { activeTextEditor } = vscode.window;
			const { document } = activeTextEditor;
			const filepath = document.uri;
			xmlFilePath = filepath.fsPath;
			const code = document.getText(); //get the text of the file		
			const pathToHtml = vscode.Uri.file(
				path.join(context.extensionPath, 'client', 'src', 'diagram.html')
			);
			const pathUri = pathToHtml.with({ scheme: 'vscode-resource' });
			const panel = vscode.window.createWebviewPanel(
				'Diagram',
				'Show Diagram',
				vscode.ViewColumn.Two,
				{
					enableScripts: true,
					retainContextWhenHidden: true

				}
			);
			panel.webview.html = getWebviewContent(code, pathUri, filepath);
			panel.webview.onDidReceiveMessage(
				message => {
					switch (message.command) {
						case 'scriptFile':

							var xml = String(fs.readFileSync(xmlFilePath, 'utf8'));
							var ast = XmlReader.parseSync(String(xml));
							var adaptive = xmlQuery(ast).find('LocalAndOutBoundAuthenticationConfig').find("AuthenticationScript").text();
							if (fs.existsSync(path.join(vscode.workspace.rootPath, 'temp.authjs'))) {
								var file = vscode.Uri.parse('file:' + path.join(vscode.workspace.rootPath, 'temp.authjs'));
								vscode.workspace.openTextDocument(file).then(document => {
									vscode.window.showTextDocument(document, 3, false);
								});
							} else {
								var newFile = vscode.Uri.parse('untitled:' + path.join(vscode.workspace.rootPath, 'temp.authjs'));
								vscode.workspace.openTextDocument(newFile).then(document => {
									const edit = new vscode.WorkspaceEdit();
									edit.insert(newFile, new vscode.Position(0, 0), adaptive);
									return vscode.workspace.applyEdit(edit).then(async success => {
										if (success) {
											await document.save();
											const newFile = vscode.Uri.parse('file:' + path.join(vscode.workspace.rootPath, 'temp.authjs'));
											vscode.workspace.openTextDocument(newFile).then(document => {
												vscode.window.showTextDocument(document, 3, false);
											});
										} else {
											vscode.window.showInformationMessage('Error!');
										}
									});
								});
								return;
							}
					}
				},
				undefined,
				context.subscriptions
			);

		}),
		vscode.commands.registerCommand('extension.script', async () => {
			const { activeTextEditor } = vscode.window;
			const { document } = activeTextEditor;
			document.save(); // save the document before sync			


			const code = document.getText(); // get the text of the \file
			var xml = String(fs.readFileSync(xmlFilePath, 'utf8')); // get the xml from the file
			var ast = XmlReader.parseSync(String(xml));
			var linecount = xml.split(/\r\n|\r|\n/).length + 1; // get the line count of the xml file

			// Extract the adaptive script
			var adaptive = xmlQuery(ast).find('LocalAndOutBoundAuthenticationConfig').find("AuthenticationScript").text();

			// Sync two documents
			var newXml = xml.replace(adaptive, code);
			const newFile = vscode.Uri.parse('file:' + path.join(xmlFilePath));
			vscode.workspace.openTextDocument(newFile).then(async document => {
				const edit = new vscode.WorkspaceEdit();
				await edit.delete(newFile, new vscode.Range(new vscode.Position(0, 0), new vscode.Position(linecount, 0)));
				await edit.insert(newFile, new vscode.Position(0, 0), newXml);
				return vscode.workspace.applyEdit(edit).then(async success => {
					if (success) {
						await document.save();
					} else {
						vscode.window.showInformationMessage('Error!');
					}
				});
			});
		}),
		vscode.commands.registerCommand('extension.auth', async () => {
			const result = await vscode.window.showInputBox({
				placeHolder: 'Enter your acess token'
			});
			
			var text = 
			`{
	"name": "wso2 Service Provider",
	"token": "${result}"
}`;
			var newFile = vscode.Uri.parse('untitled:' + path.join(vscode.workspace.rootPath, '.config.json'));
			vscode.workspace.openTextDocument(newFile).then(document => {
				const edit = new vscode.WorkspaceEdit();
				edit.insert(newFile, new vscode.Position(0, 0), text);
				return vscode.workspace.applyEdit(edit).then(async success => {
					if (success) {					
						await document.save();						
					} else {
						vscode.window.showInformationMessage('Error!');
					}
				});
			});
			vscode.window.showInformationMessage(`Access Token Saved Successfully`);
		}),
		vscode.commands.registerCommand('extension.mock-debug.getProgramName', config => {
			return vscode.window.showInputBox({
				placeHolder: "Please enter the name of a markdown file in the workspace folder",
				value: "readme.md"
			});
		})
	);
	// register a configuration provider for 'mock' debug type
	const provider = new MockConfigurationProvider();
	context.subscriptions.push(vscode.debug.registerDebugConfigurationProvider('mock', provider));
	console.log("EMBED_DEBUG_ADAPTER "+EMBED_DEBUG_ADAPTER);
	if (EMBED_DEBUG_ADAPTER) {
		// The following use of a DebugAdapter factory shows how to run the debug adapter inside the extension host (and not as a separate process).
		const factory = new IdentityServerDebugAdapterDescriptorFactory();
		context.subscriptions.push(vscode.debug.registerDebugAdapterDescriptorFactory('mock', factory));
		context.subscriptions.push(factory);
	} else {
		// The following use of a DebugAdapter factory shows how to control what debug adapter executable is used.
		// Since the code implements the default behavior, it is absolutely not neccessary and we show it here only for educational purpose.
		context.subscriptions.push(vscode.debug.registerDebugAdapterDescriptorFactory('mock', {
			createDebugAdapterDescriptor: (session: vscode.DebugSession, executable: vscode.DebugAdapterExecutable | undefined) => {
				// param "executable" contains the executable optionally specified in the package.json (if any)

				// use the executable specified in the package.json if it exists or determine it based on some other information (e.g. the session)
				if (!executable) {
					const command = "absolute path to my DA executable";
					const args = [
						"some args",
						"another arg"
					];
					const options = {
						cwd: "working directory for executable",
						env: { "VAR": "some value" }
					};
					executable = new vscode.DebugAdapterExecutable(command, args, options);
				}

				// make VS Code launch the DA executable
				return executable;
			}
		}));
	}

	// Create the language client and start the client.
	client = new LanguageClient(
		'languageServerExample',
		'Language Server Example',
		serverOptions,
		clientOptions
	);

	// Start the client. This will also launch the server
	client.start();
}

export function deactivate(): Thenable<void> | undefined {
	if (!client) {
		return undefined;
	}
	return client.stop();
}

function getWebviewContent(code, path, filepath) {
	var html = fs.readFileSync(path.fsPath, 'utf8');
	var re = /myXML/gi;
	var pa = /myfilepath/gi;
	var newHtml = html.replace(re, code).replace(pa, filepath);
	return newHtml;
}

class MockConfigurationProvider implements vscode.DebugConfigurationProvider {

	/**
	 * Massage a debug configuration just before a debug session is being launched,
	 * e.g. add all missing attributes to the debug configuration.
	 */
	resolveDebugConfiguration(folder: WorkspaceFolder | undefined, config: DebugConfiguration, token?: CancellationToken): ProviderResult<DebugConfiguration> {

		// if launch.json is missing or empty
		if (!config.type && !config.request && !config.name) {
			const editor = vscode.window.activeTextEditor;
			if (editor && editor.document.languageId === 'markdown') {
				config.type = 'mock';
				config.name = 'Launch';
				config.request = 'launch';
				config.program = '${file}';
				config.stopOnEntry = true;
			}
		}

		if (!config.program) {
			return vscode.window.showInformationMessage("Cannot find a program to debug").then(_ => {
				return undefined;	// abort launch
			});
		}

		return config;
	}
}

class IdentityServerDebugAdapterDescriptorFactory implements vscode.DebugAdapterDescriptorFactory {

	private server?: Net.Server;

	createDebugAdapterDescriptor(session: vscode.DebugSession, executable: vscode.DebugAdapterExecutable | undefined): vscode.ProviderResult<vscode.DebugAdapterDescriptor> {

		if (!this.server) {
			// start listening on a random port
			this.server = Net.createServer(socket => {
				const session = new IdentityServerDebugSession();
				session.setRunAsServer(true);
				session.start(<NodeJS.ReadableStream>socket, socket);
			}).listen(0);
		}

		// make VS Code connect to debug server
		return new vscode.DebugAdapterServer((<Net.AddressInfo>this.server.address()).port);
	}

	dispose() {
		if (this.server) {
			this.server.close();
		}
	}
}
