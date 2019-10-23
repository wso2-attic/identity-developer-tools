/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */

import * as path from 'path';
import { workspace, ExtensionContext } from 'vscode';
import * as vscode from 'vscode';
import {
	LanguageClient,
	LanguageClientOptions,
	ServerOptions,
	TransportKind
} from 'vscode-languageclient';
let client: LanguageClient;
import * as fs from 'fs';
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
		documentSelector: [{ scheme: 'file', language: 'IAM' }],
		synchronize: {
			// Notify the server about file changes to '.clientrc files contained in the workspace
			fileEvents: workspace.createFileSystemWatcher('**/.clientrc')
		}
	};

	context.subscriptions.push(
		vscode.commands.registerCommand('extension.diagram', () => {

			const { activeTextEditor } = vscode.window;
			const { document } = activeTextEditor;
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

			console.log(pathUri.fsPath);

			// And set its HTML content
			panel.webview.html = getWebviewContent(code, pathUri);



		})
	);



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

function getWebviewContent(code, path) {
	var html = fs.readFileSync(path.fsPath, 'utf8');
	var re = /myXML/gi;
	var newHtml = html.replace(re, code);
	return newHtml;	
}