import * as vscode from 'vscode';
import * as fs from 'fs';
import * as path from 'path';
import { FileHandler } from './fileHandler';

// Object of the FileHandler.
const fileHandler = new FileHandler();

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
				path.join(context.extensionPath, 'client', 'src','ui','diagram.html')
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
					fileHandler.createOrOpenAdaptiveScript(message,xmlFilePath);							
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
	public generateOAuthPreview(context){
		var htmlFilePath = vscode.Uri.file(
			path.join(context.extensionPath, 'client', 'src', 'ui','oAuth.html')
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
		panel.webview.html = html;
	}
	
}