import * as fs from 'fs';
import * as path from 'path';
import * as url from 'url';
import * as xmlQuery from 'xml-query';
import * as XmlReader from 'xml-reader';
import * as vscode from 'vscode';
const xml2js = require('xml2js');
export class FileHandler {

	/**
	 * readXML() used to read the XML files code from the given file path
	 */
	public readXML(filePath: any): String {
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
	public async handleButtonClick(message, xmlFilePath) {
		// Get the name of the servce.
		var serviceName = this.extractFileName(xmlFilePath).replace('%20', ' ');
		// Handle the button click in web view.
		switch (String(message.command)) {
			case "scriptFile":
				var adaptive = this.extractAdaptiveScript(xmlFilePath);
				this.createOrOpenAdaptiveScript(adaptive, serviceName);
			case "defaultScriptFile":
				var adaptiveScript = this.createDefaultAdaptiveScript(message.data);
				this.createOrOpenAdaptiveScript(adaptiveScript, serviceName);
		}
	}

	/**
	 * createOrOpenAdaptiveScript() to Open available adaptiveScriptFile or 
	 * create a new adaptive script file.
	 */
	public createOrOpenAdaptiveScript(adaptiveScript, serviceName) {
		// Check whether the file already exsists.
		if (fs.existsSync(path.join(vscode.workspace.rootPath, serviceName + '.authjs'))) {
			vscode.window.showInformationMessage('oyee!');
			var file = vscode.Uri.parse('file:' + path.join(vscode.workspace.rootPath, serviceName + '.authjs'));
			// Open the file.
			vscode.workspace.openTextDocument(file).then(document => {
				vscode.window.showTextDocument(document, 2, false);
			});
		} else {
			vscode.window.showInformationMessage('oyee! naa');
			// Uri of the untitled file.
			var newFile = vscode.Uri.parse('untitled:' + path.join(vscode.workspace.rootPath, serviceName + '.authjs'));
			vscode.workspace.openTextDocument(newFile).then(document => {
				const edit = new vscode.WorkspaceEdit();
				// Insert text to the untitled file.
				edit.insert(newFile, new vscode.Position(0, 0), adaptiveScript);
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

	/**
	 * createDefaultAdaptiveScript() to create adaptive script when no scripts found.
	 */
	public createDefaultAdaptiveScript(executeSteps) {
		var script = `var onLoginRequest = function (context) {\n` +
			this.bindExecuteSteps(executeSteps)
			+ `\n};`;
		console.log(script);
		return script;
	}

	/**
	 * bindExecuteSteps() to bind the executeStep method to the script.
	 */
	public bindExecuteSteps(executeSteps) {
		var executeStep = ``;
		for (let index = 1; index <= executeSteps; index++) {
			if (index < executeSteps) {
				executeStep = executeStep + `\texecuteStep(` + index + `);\n`;
			} else {
				executeStep = executeStep + `\texecuteStep(` + index + `);`;
			}
		}
		return executeStep;

	}

	/**
	 * syncServiceProviderWithAdaptiveScript() to sync the Adaptive Script with XML File.
	 */
	public syncServiceProviderWithAdaptiveScript(xmlFilePath) {

		var fileNames = [];
		var files;
		if (fs.existsSync(path.join(vscode.workspace.rootPath, 'IAM', 'Apps'))) {
			files = this.getFilesFromDir(path.join(vscode.workspace.rootPath, 'IAM', 'Apps'), [".authxml"]);

			files.forEach(file => {
				fileNames.push(path.basename(file).replace(/\.[^/.]+$/, ""));
			});

		}
		const { activeTextEditor } = vscode.window;
		const { document } = activeTextEditor;

		// Save the active adaptive script before sync.
		document.save();

		// Get the adative Script code. 
		const newAdaptiveScriptCode = document.getText();
		var serviceName = this.extractFileName(document.uri.fsPath).replace('%20', ' ');
		console.log("servicename in sync--" + serviceName);
		var xmlFile = path.join(vscode.workspace.rootPath, 'IAM', 'Apps', files[fileNames.indexOf(serviceName)]);
		console.log("servicename in xmlFile--" + xmlFile);
		console.log("servicename in xmlFilePath--" + xmlFilePath);
		// return;
		var xml = this.readXML(xmlFile);

		// Get the line count of the xml file.
		var linecount = xml.split(/\r\n|\r|\n/).length + 1;

		var adaptiveScript = this.extractAdaptiveScript(xmlFilePath);		
		var newXml;
		var parser = new xml2js.Parser({ explicitArray: false });
		var xmlBuilder = new xml2js.Builder({ cdata: true });
		parser.parseString(xml, function (err, result) {
			// Check whether AuthenticationScript node is Available.		
			if ('AuthenticationScript' in result.ServiceProvider.LocalAndOutBoundAuthenticationConfig) {
				newXml = xml.replace(adaptiveScript, newAdaptiveScriptCode);
			} else {	
				// Add the AuthenticationScript node to the xmlfile.			
				result.ServiceProvider.LocalAndOutBoundAuthenticationConfig.AuthenticationScript = { $: { enabled: "false", language: "application/javascript" }, _: "//<enable false>\n"+newAdaptiveScriptCode};
				// change the xml to the new xml.
				newXml = xmlBuilder.buildObject(result);		
			}
		});

		// Sync two documents.		
		const newFile = vscode.Uri.parse('file:' + path.join(xmlFilePath));
		vscode.workspace.openTextDocument(newFile).then(async document => {
			const edit = new vscode.WorkspaceEdit();
			// Delete the current xml code.
			await edit.delete(newFile, new vscode.Range(new vscode.Position(0, 0), new vscode.Position(linecount, 0)));
			// Add new xml code.
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

	/**
	 * createXMLFile() to create the xml file with of the service.
	 */
	public async createXMLFile(xml, serviceName) {
		var fileNames = [];
		var files;
		if (fs.existsSync(path.join(vscode.workspace.rootPath, 'IAM', 'Apps'))) {
			files = this.getFilesFromDir(path.join(vscode.workspace.rootPath, 'IAM', 'Apps'), [".authxml"]);

			files.forEach(file => {
				fileNames.push(path.basename(file).replace(/\.[^/.]+$/, ""));
			});

		}

		console.log();
		console.log(files);
		// Get the line count of the xml file.
		var linecount = xml.split(/\r\n|\r|\n/).length + 1;
		// Check whether the file already exsists.
		if (fileNames.includes(serviceName)) {
			var file = vscode.Uri.parse('file:' + path.join(vscode.workspace.rootPath, 'IAM', 'Apps', files[fileNames.indexOf(serviceName)]));
			// Open the file.
			vscode.workspace.openTextDocument(file).then(async document => {
				const edit = new vscode.WorkspaceEdit();
				await edit.delete(file, new vscode.Range(new vscode.Position(0, 0), new vscode.Position(linecount, 0)));
				await edit.insert(file, new vscode.Position(0, 0), xml);
				vscode.window.showTextDocument(document, 1, false);
			});
		} else {
			// Uri of the untitled file.
			var newFile = vscode.Uri.parse('untitled:' + path.join(vscode.workspace.rootPath, 'IAM', 'Apps', serviceName + '.authxml'));
			vscode.workspace.openTextDocument(newFile).then(async document => {
				const edit = new vscode.WorkspaceEdit();
				// Insert text to the untitled file.
				await edit.insert(newFile, new vscode.Position(0, 0), xml);
				return vscode.workspace.applyEdit(edit).then(async success => {
					if (success) {
						// save the untitled file.
						await document.save();
						await vscode.commands.executeCommand("workbench.action.closeActiveEditor");
						const newFile = vscode.Uri.parse('file:' + path.join(vscode.workspace.rootPath, 'IAM', 'Apps', serviceName + '.authxml'));
						vscode.workspace.openTextDocument(newFile).then(async document => {
							vscode.window.showTextDocument(document, 1, false);
						});
					} else {
						vscode.window.showInformationMessage('Error!');
					}
				});
			});
			return;
		}
	}

	/**
	 * getFilesFromDir() to return a list of files of the specified fileTypes in the provided dir.
	*/	
	public getFilesFromDir(dir, fileTypes) {
		var filesToReturn = [];
		function walkDir(currentPath) {
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

}