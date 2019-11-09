import * as fs from 'fs';
import * as path from 'path';
import * as url from 'url';
import * as xmlQuery from 'xml-query';
import * as XmlReader from 'xml-reader';
import * as vscode from 'vscode';
export class FileHandler {

	/**
	 *  readXML() used to read the XML files code from the given file path
	 */
	public readXML(filePath): String {
		return String(fs.readFileSync(filePath, 'utf8'));
	}

	/**
	 * extractFileName() to extract the file name from the gven filepath.
	 */
	public extractFileName(filePath): String {
		var parsed = url.parse(filePath);
		var fileName = path.basename(parsed.pathname).replace(/\.[^/.]+$/, "");
		return fileName;
	}

	/**
	 * extractAdaptiveScript() to extract the adpative scripts from the xml.
	 */
	public extractAdaptiveScript(filePath) {
		var xmlCode = this.readXML(filePath);
		var ast = XmlReader.parseSync(String(xmlCode));
		var adaptive = xmlQuery(ast).find('LocalAndOutBoundAuthenticationConfig')
			.find("AuthenticationScript").text();
		return adaptive;
	}

	/**
	 * getHTMLCode() to read the html code from the file.
	 */
	public getHTMLCode(htmlFilePath) {
		return fs.readFileSync(htmlFilePath, 'utf8');
	}

	/**
	 * createOrOpenAdaptiveScript() to Open available adaptiveScriptFile or 
	 * create a new adaptive script file.
	 */
	public createOrOpenAdaptiveScript(message, xmlFilePath) {

		//handle the button click in web view.
		switch (message.command) {
			case 'scriptFile':
				var serviceName = this.extractFileName(xmlFilePath);
				vscode.window.showInformationMessage("Servicename is " + serviceName);
				var adaptive = this.extractAdaptiveScript(xmlFilePath);

				// Check whether the file already exsists.
				if (fs.existsSync(path.join(vscode.workspace.rootPath, serviceName + '.authjs'))) {
					var file = vscode.Uri.parse('file:' + path.join(vscode.workspace.rootPath, serviceName + '.authjs'));
					// Open the file.
					vscode.workspace.openTextDocument(file).then(document => {
						vscode.window.showTextDocument(document, 3, false);
					});
				} else {

					// Uri of the untitled file.
					var newFile = vscode.Uri.parse('untitled:' + path.join(vscode.workspace.rootPath, serviceName + '.authjs'));
					vscode.workspace.openTextDocument(newFile).then(document => {
						const edit = new vscode.WorkspaceEdit();
						// Insert text to the untitled file.
						edit.insert(newFile, new vscode.Position(0, 0), adaptive);
						return vscode.workspace.applyEdit(edit).then(async success => {
							if (success) {
								// save the untitled file.
								await document.save();
								const newFile = vscode.Uri.parse('file:' + path.join(vscode.workspace.rootPath, serviceName + '.authjs'));
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
	}


	/**
	 * syncServiceProviderWithAdaptiveScript() to sync the Adaptive Script with XML File.
	 */
	public syncServiceProviderWithAdaptiveScript(xmlFilePath) {
		const { activeTextEditor } = vscode.window;
		const { document } = activeTextEditor;

		// Save the active adaptive script before sync.
		document.save(); 

		// Get the adative Script code. 
		const newAdaptiveScriptCode = document.getText(); 

		var xml = this.readXML(xmlFilePath);

		// Get the line count of the xml file.
		var linecount = xml.split(/\r\n|\r|\n/).length + 1;

		var adaptiveScript = this.extractAdaptiveScript(xmlFilePath);

		// Sync two documents.
		var newXml = xml.replace(adaptiveScript, newAdaptiveScriptCode);
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
	}

}