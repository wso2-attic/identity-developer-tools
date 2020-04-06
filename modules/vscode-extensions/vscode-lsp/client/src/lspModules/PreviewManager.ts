import * as vscode from 'vscode';
import * as fs from 'fs';
import * as path from 'path';
import { FileHandler } from './fileHandler';
import { Wso2OAuth } from './oAuthService';
import { ViewPanelHolder } from './ViewPanelHolder';
const keytar = require('keytar');
// Object of the FileHandler.
const fileHandler = new FileHandler();
var format = require("string-template");
const scope = "internal_application_mgt_create internal_application_mgt_delete internal_application_mgt_update internal_application_mgt_view internal_functional_lib_view";
export class PreviewManager {

	private static instance: PreviewManager;
	private _previewManagers = new Map<string, ViewPanelHolder>();

	private constructor() { }

	public static getInstance(): PreviewManager {

		if (!PreviewManager.instance) {
			PreviewManager.instance = new PreviewManager();
		}

		return PreviewManager.instance;
	}

	public getPreviewManagers(){

		return this._previewManagers;
	}


	/**
	 * generateWebViewPanel() to generate the web View panel to render web view.
	 */
	public generateWebViewPanel(xmlFilePath, context) {

		const previewManager = PreviewManager.getInstance();
		const previewManagers = previewManager.getPreviewManagers();
		var serviceName = String(fileHandler.extractFileName(xmlFilePath));
		// Read the XML file and generate the web view panel.
		fs.readFile(xmlFilePath, 'utf8', function (err: any, data: any) {
			// Get the text of the file.
			const code = String(data);

			const pathUri = vscode.Uri.file(
				path.join(context.extensionPath, 'client', 'src', 'ui', 'diagram.html')
			).with({ scheme: 'vscode-resource' });

			const pathCss = vscode.Uri.file(
				path.join(context.extensionPath, 'client', 'src', 'ui','css')
			).with({ scheme: 'vscode-resource' });

			const pathJS = vscode.Uri.file(
				path.join(context.extensionPath, 'client', 'src', 'ui', 'js')
			).with({ scheme: 'vscode-resource' });


			const panel = vscode.window.createWebviewPanel(
				'Diagram',
				serviceName,
				vscode.ViewColumn.One,
				{
					enableScripts: true,
					retainContextWhenHidden: true

				}
			);
			// Assign html code to the web view panel.
			const htmlGenerated=previewManager.getWebviewContent(code, pathUri, xmlFilePath,pathCss,pathJS);
			panel.webview.html = htmlGenerated;
			panel.webview.onDidReceiveMessage(
				message => {
						fileHandler.handleButtonClick(message, xmlFilePath);
				},
				undefined,
				context.subscriptions
			);
			let key=fileHandler.extractFileName(xmlFilePath).replace('%20', ' ');
			let viewPanelHolder= new ViewPanelHolder(panel,htmlGenerated);
			previewManagers.set(key,viewPanelHolder);
		});
	}


	/**
	 * getWebviewContent() to generate the html of web view.
	 */
	public getWebviewContent(xmlCode, path, filepath,pathCss,pathJS) {

		var htmlCode = fileHandler.getHTMLCode(path.fsPath);

		let newHtml = format(htmlCode, {
			myXML: xmlCode,
			myfilepath:filepath,
			pathCss:pathCss,
			pathJS:pathJS,
			SAML_REQUEST:"{SAML_REQUEST}",
			SAML_RESPONSE:"{SAML_RESPONSE}"
		});

		return newHtml;
	}

	/**
	 * getWebviewContent() to generate the html of oAuth web.
	 */
	public async generateOAuthPreview(context) {

		var htmlFilePath = vscode.Uri.file(
			path.join(context.extensionPath, 'client', 'src', 'ui', 'oAuth.html')
		);
		var html = fileHandler.getHTMLCode(htmlFilePath.fsPath);
		const panel = vscode.window.createWebviewPanel(
			'WSO2 Login',
			'WSO2 Login',
			vscode.ViewColumn.One,
			{
				enableScripts: true,
				retainContextWhenHidden: true

			}
		);

		var replaceURL = /wso2isurl/gi;
		var replaceTenant = /wso2isTenant/gi;
		var replaceID = /wso2ISClientID/gi;
		var replaceSecret = /wso2ISClientSecret/gi;
		var preClientId = vscode.workspace.getConfiguration().get('IAM.ServiceClientID');
		var preTenant = vscode.workspace.getConfiguration().get('IAM.Tenant');
		var preClientSecret;
		var secret = keytar.getPassword("clientSecret", "clientSecret");
		await secret.then((result) => {
			preClientSecret = result;
		});

		// Get the new code.
		var newHtml = html.replace(replaceURL, vscode.workspace.getConfiguration().get('IAM.URL'))
			.replace(replaceID, String(preClientId)).replace(replaceSecret, String(preClientSecret))
			.replace(replaceTenant,String(preTenant));
		panel.webview.html = newHtml;

		var url = vscode.workspace.getConfiguration().get('IAM.URL');
		panel.webview.onDidReceiveMessage(
			async message => {
				if (message.command == 'login') {
					// To start the server.
					new Wso2OAuth(8010, context).StartProcess();

					// Set the url to extension configuration.
					vscode.workspace.getConfiguration().update("IAM.URL", message.url);

					// Set the tenant domain to extension configuration.
					vscode.workspace.getConfiguration().update("IAM.Tenant", message.tenant);

					// Set the client id to extension configurations.
					vscode.workspace.getConfiguration().update("IAM.ServiceClientID", message.clientID);

					// Set Client Secret to system key chain.
					await keytar.setPassword("clientSecret", "clientSecret", message.clientSecret);

					// Open the login page.
					vscode.commands.executeCommand(
						"vscode.open",
						vscode.Uri.parse(
							url + "/oauth2/authorize?response_type=code&redirect_uri=http://localhost:8010/oauth&client_id=" + message.clientID + "&scope=" + scope)
					);
				} else if (message.command == 'acess') {
					// Set Acess Token to system key chain.
					keytar.setPassword("acessToken", "acessToken", message.acessToken);
					// Close the webview
					await vscode.commands.executeCommand("workbench.action.closeActiveEditor");
					// Show the sucess message
					vscode.window.showInformationMessage("Successfully Configued your Extension");
				}

			},
			undefined,
			context.subscriptions
		);

	}

}