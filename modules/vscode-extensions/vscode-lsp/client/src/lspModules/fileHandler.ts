import * as fs from 'fs';
import * as path from 'path';
import * as url from 'url';
import * as xmlQuery from 'xml-query';
import * as XmlReader from 'xml-reader';
import * as vscode from 'vscode';
const xml2js = require('xml2js');
const temp = require('temp');	
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
		vscode.window.showInformationMessage(String(message.command));
		if(String(message.command) == "scriptFile"){
			var adaptive = this.extractAdaptiveScript(xmlFilePath);
			this.createOrOpenAdaptiveScript(adaptive, serviceName);
		}else if(String(message.command) == "defaultScriptFile"){
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

		// Automatically track and cleanup files at exit
		temp.track();		

		// Create a temp file.
		temp.mkdir('adaptiveScript', function (err, dirPath) {
			if (err) throw err;
			var inputPath = path.join(dirPath, serviceName+'.authjs');
			fs.writeFile(inputPath, adaptiveScript, function (err) {
				if (err) throw err;
				process.chdir(dirPath);
				vscode.workspace.openTextDocument(inputPath).then(document => {
					vscode.window.showTextDocument(document, 2, false);
				});				
			});
		});
		
	}

	/**
	 * createDefaultAdaptiveScript() to create adaptive script when no scripts found.
	 */
	public createDefaultAdaptiveScript(executeSteps) {
		var script = `var onLoginRequest = function (context) {\n` +
			this.bindExecuteSteps(executeSteps)
			+ `\n};`;
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
	public async syncServiceProviderWithAdaptiveScript() {
		var parser = new xml2js.Parser({ explicitArray: false });
		var xmlBuilder = new xml2js.Builder({ cdata: true });
		const { activeTextEditor } = vscode.window;
		const { document } = activeTextEditor;
		var fileNames = []; // Array of file names.
		var files;
		if (fs.existsSync(path.join(vscode.workspace.rootPath, 'IAM', 'Apps'))) {
			files = this.getFilesFromDir(path.join(vscode.workspace.rootPath, 'IAM', 'Apps'), [".authxml"]);
			files.forEach(file => {
				// Add files names to array.
				fileNames.push(path.basename(file).replace(/\.[^/.]+$/, ""));
			});
		}
		// Variable to assign the new xml.
		var newXml;
		// Get the adative Script code. 
		var newAdaptiveScriptCode = document.getText();
		// Get the service name.
		var serviceName = this.extractFileName(document.uri.fsPath).replace('%20', ' ');
		// Path of the xml file of the service.	
		var xmlFile = path.join(vscode.workspace.rootPath, 'IAM', 'Apps', files[fileNames.indexOf(serviceName)]);
		// Current code of the xml file.
		var xml = this.readXML(xmlFile);
		// Get the old adaptive script.	
		var adaptiveScript = this.extractAdaptiveScript(xmlFile);
		// Save the active adaptive script before sync.
		document.save();

		parser.parseString(xml, function (err, result) {
			// Check whether AuthenticationScript node is Available.		
			if ('AuthenticationScript' in result.ServiceProvider.LocalAndOutBoundAuthenticationConfig) {
				newXml = xml.replace(adaptiveScript, newAdaptiveScriptCode);
			} else {
				// Add the AuthenticationScript node to the xmlfile.			
				result.ServiceProvider.LocalAndOutBoundAuthenticationConfig.AuthenticationScript = { $: { enabled: "false", language: "application/javascript" }, _: "//<enable false>\n" + newAdaptiveScriptCode };
				// change the xml to the new xml.
				newXml = xmlBuilder.buildObject(result);
			}
		});
		// To write the new data to the xml file.
		fs.writeFile(xmlFile, newXml, (err) => {
			console.log(err)
			vscode.window.showInformationMessage('The file has been saved!');
		});
		await vscode.commands.executeCommand("workbench.action.closeActiveEditor");
	}

	/**
	 * createXMLFile() to create the xml file with of the service.
	 */
	public async createXMLFile(xml, serviceName) {
		var fileNames = []; // To keep the names of the file.
		var files; // Array of available files in the directory
		if (fs.existsSync(path.join(vscode.workspace.rootPath, 'IAM', 'Apps'))) {
			files = this.getFilesFromDir(path.join(vscode.workspace.rootPath, 'IAM', 'Apps'), [".authxml"]);
			files.forEach(file => {
				fileNames.push(path.basename(file).replace(/\.[^/.]+$/, ""));
			});
		}else{
			fs.mkdirSync(path.join(vscode.workspace.rootPath, 'IAM', 'Apps'), { recursive: true });			
		}
		// Check whether the file already exsists.
		if (fileNames.includes(serviceName)) {
			var file = vscode.Uri.parse('file:' + path.join(vscode.workspace.rootPath, 'IAM', 'Apps', files[fileNames.indexOf(serviceName)]));

			// Open the file.
			vscode.workspace.openTextDocument(file).then(async document => {
				vscode.window.showTextDocument(document, 1, false);
			});
		} else {
			// Uri of the untitled file.

			var newFile = vscode.Uri.parse('file:' + path.join(vscode.workspace.rootPath, 'IAM', 'Apps', serviceName + '.authxml'));
			fs.writeFile(path.join(vscode.workspace.rootPath, 'IAM', 'Apps', serviceName + '.authxml'), xml, (err) => {
				if (err) throw err;
				vscode.window.showInformationMessage('The file has been saved!');
			});
			// Open the file.
			vscode.workspace.openTextDocument(newFile).then(async document => {
				vscode.window.showTextDocument(document, 1, false);
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