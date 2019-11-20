/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */

import * as path from 'path';
import { workspace, ExtensionContext, WorkspaceFolder, DebugConfiguration, ProviderResult, CancellationToken } from 'vscode';
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
const xmlQuery = require('xml-query');
const XmlReader = require('xml-reader');
var xmlFilePath;
import { FileHandler } from './lspModules/fileHandler';
import { PreviewManager } from './lspModules/PreviewManager';
import { Wso2OAuth } from './lspModules/oAuthService';
/*
 * Set the following compile time flag to true if the
 * debug adapter should run inside the extension host.
 * Please note: the test suite does not (yet) work in this mode.
 */

const EMBED_DEBUG_ADAPTER = true;
export function activate(context: ExtensionContext) {
	// The Object Of the FileHandler
	const fileHandler = new FileHandler();
	// The Object Of the previewManager
	const previewManager = new PreviewManager();
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

	var accessToken;
	context.subscriptions.push(		

		// Show oAuth to wso2.
		vscode.commands.registerCommand('extension.oAuth', () => {
			previewManager.generateOAuthPreview(context);
			new Wso2OAuth(8010).StartProcess();
		}),
		
		// File open event hadler
		vscode.workspace.onDidOpenTextDocument(async (file) => {		
			
			accessToken = vscode.workspace.getConfiguration().get('IAM.acessToken');
			var extensionOfOpenedFile = path.extname(file.uri.fsPath.split(".git")[0]);
			console.log(extensionOfOpenedFile);
			if (extensionOfOpenedFile == ".authxml") {
				await vscode.commands.executeCommand("workbench.action.closeActiveEditor");
				xmlFilePath = file.uri.fsPath.split(".git")[0];
				vscode.window.showInformationMessage(String(accessToken));
				previewManager.generateWebViewPanel(xmlFilePath, context);
			}
		}),

		// Show Diagram command registration.
		vscode.commands.registerCommand('extension.diagram', () => {
			const { activeTextEditor } = vscode.window;
			const { document } = activeTextEditor;
			const filepath = document.uri;
			xmlFilePath = filepath.fsPath;
			previewManager.generateWebViewPanel(xmlFilePath, context);
		}),

		// Sync command registration.
		vscode.commands.registerCommand('extension.script', async () => {
			fileHandler.syncServiceProviderWithAdaptiveScript(xmlFilePath);
		}),

		// List the service providers in command plate
		vscode.commands.registerCommand('extension.auth', async () => {
			// Path of the service providers directory.
			const serviceProvidersDirectory = vscode.workspace.rootPath + '/.conf/sp/';
			var services = [];
			try {
				// Add the service names to the services array.
				fs.readdirSync(serviceProvidersDirectory).forEach((file: any) => {
					var serviceName = String(file).replace(/\.[^/.]+$/, "");
					services.push(serviceName);
				});
			} catch (e) {
				console.log(e);
			}

			// Show thelist of services in command pallet.
			var result = await vscode.window.showQuickPick(
				services,
				{ placeHolder: 'Select Service' }
			);
			xmlFilePath = serviceProvidersDirectory + result + '.authxml';
			previewManager.generateWebViewPanel(xmlFilePath, context);

		})
	);
	// register a configuration provider for 'mock' debug type
	const provider = new MockConfigurationProvider();
	context.subscriptions.push(vscode.debug.registerDebugConfigurationProvider('mock', provider));
	console.log("EMBED_DEBUG_ADAPTER " + EMBED_DEBUG_ADAPTER);
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
