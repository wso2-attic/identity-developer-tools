import * as vscode from 'vscode';
import * as fs from 'fs';
import * as path from 'path';
import { FileHandler } from './fileHandler';
import { Wso2OAuth } from './oAuthService';
const keytar = require('keytar');
// Object of the FileHandler.
const fileHandler = new FileHandler();
const scope = "internal_application_mgt_create internal_application_mgt_delete internal_application_mgt_update internal_application_mgt_view internal_functional_lib_view";
export class PreviewManager {

	/**
	 * generateWebViewPanel() to generate the web View panel to render web view.
	 */
	public generateWebViewPanel(xmlFilePath, context) {
		const previewManager = new PreviewManager();
		var serviceName = String(fileHandler.extractFileName(xmlFilePath));
		// Read the XML file and generate the web view panel.
		fs.readFile(xmlFilePath, 'utf8', function (err: any, data: any) {
			// Get the text of the file.
			const code = String(data);
			const pathToHtml = vscode.Uri.file(
				path.join(context.extensionPath, 'client', 'src', 'ui', 'diagram.html')
			);
			const pathUri = pathToHtml.with({ scheme: 'vscode-resource' });
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
			panel.webview.html = previewManager.getWebviewContent(code, pathUri, xmlFilePath);
			panel.webview.onDidReceiveMessage(
				message => {
					fileHandler.handleButtonClick(message, xmlFilePath);
				},
				undefined,
				context.subscriptions
			);
		});
	}


	/**
	 * getWebviewContent() to generate the html of web view.
	 */
	public getWebviewContent(xmlCode, path, filepath) {
		var htmlCode = fileHandler.getHTMLCode(path.fsPath);
		var re = /myXML/gi;
		var pa = /myfilepath/gi;
		// Replace the xml code and the filepath in html code.	
		var newHtml = htmlCode.replace(re, xmlCode).replace(pa, filepath);
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
			console.log("Pre client Secret: " + result); // result will be 'secret'
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
					new Wso2OAuth(8010).StartProcess();

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