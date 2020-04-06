import * as vscode from 'vscode';
import * as fs from 'fs';
import * as path from 'path';
const axios = require('axios');
const keytar = require('keytar');
const tempPath = require('os').tmpdir();
import { FileHandler } from './fileHandler';
import {PreviewManager} from "./PreviewManager";
const fileHandler = new FileHandler();
export class ScriptLibraryTree implements vscode.TreeDataProvider<Dependency> {

	private _onDidChangeTreeData: vscode.EventEmitter<Dependency | undefined> = new vscode.EventEmitter<Dependency | undefined>();
	readonly onDidChangeTreeData: vscode.Event<Dependency | undefined> = this._onDidChangeTreeData.event;
	private context;

	constructor(context) {

		this.context=context;
	}

	refresh(): void {

		this._onDidChangeTreeData.fire();
	}

	getTreeItem(element: Dependency): vscode.TreeItem {

		return element;
	}

	getChildren(): Thenable<Dependency[]> {

		return Promise.resolve(this.getListOfItems(this.context));
	}

	/**
	 * Given the path to package.json, read all its dependencies and devDependencies.
	 */
	private async getListOfItems(context): Promise<Dependency[]> {

		let scriptLibraries = [];
		var url = vscode.workspace.getConfiguration().get('IAM.URL');
		var tenant = vscode.workspace.getConfiguration().get('IAM.Tenant');
		var acessToken;
		// Get the acess token from the system key chain.
		var secret = keytar.getPassword("acessToken", "acessToken");
		await secret.then((result) => {
			acessToken = result; // Assign the value to acess toke.					
		});

		// To bypass the self signed server error.
		process.env["NODE_TLS_REJECT_UNAUTHORIZED"] = "0";

		await axios({

			method: 'get',
			url: url + `/t/${tenant}/api/server/v1/script-libraries`,

			// Set the content type header, so that we get the response in JSOn
			headers: {
				Authorization: 'Bearer ' + acessToken,
				accept: '*/*'

			}
		}).then(async (response) => {
			// Once we get the response, extract the access token from
			// the response body			
			// Show the sucess message in the vscode.
			vscode.window.showInformationMessage("Successfully retrieved the script libraries");
			// Create a node_modules folder in the temp directory.
			if (!fs.existsSync(path.join(tempPath, 'node_modules'))) {
				fs.mkdir(path.join(require('os').tmpdir(), 'node_modules/'), (err: any) => {
					if (err) throw err;
				});
			}

			if (response.data.count>0){
				for (let index = 0; index < response.data.scriptLibraries.length; index++) {
					var scriptLibraryName= response.data.scriptLibraries[index].name;
					var content;
					await axios({
	
						method: 'get',
						url: url + `/t/${tenant}/api/server/v1/script-libraries/${scriptLibraryName}/content`,
			
						// Set the content type header, so that we get the response in JSOn
						headers: {
							Authorization: 'Bearer ' + acessToken,
							accept: '*/*'
			
						}
					}).then((response) => {
						// Once we get the response, extract the access token from
						// the response body			
						// Show the sucess message in the vscode.
						//vscode.window.showInformationMessage("Successfully retrieve the script libraries");
						// Create a node_modules folder in the temp directory.
						
						content=response.data;			
					}).catch((err) => {
			
						console.log(err);
			
						// Show the sucess message in the vscode.
						PreviewManager.getInstance().generateOAuthPreview(this.context);
						vscode.window.showErrorMessage("Access Token has expired.");
					});
					
					if (fs.existsSync(path.join(tempPath, 'node_modules', scriptLibraryName))) {
						var inputPath = path.join(tempPath, 'node_modules', scriptLibraryName, 'index.js');
						fs.writeFile(inputPath, content , function (err: any) {
							if (err) throw err;
						});
		
					} else {
						fs.mkdirSync(path.join(tempPath, 'node_modules', scriptLibraryName), { recursive: true });
						var inputPath = path.join(tempPath, 'node_modules', scriptLibraryName, 'index.js');
						fs.writeFile(inputPath, content , function (err: any) {
							if (err) throw err;
						});
					}
					scriptLibraries.push(new Dependency(scriptLibraryName,
						response.data.scriptLibraries[index].description,
						{
							command: 'extension.scriptLibrariesFromTreeView',
							title: 'Edit Script',
							arguments: [scriptLibraryName]
						}
						
					));
				}
			}

		}).catch((err) => {
			// Show the sucess message in the vscode.
			PreviewManager.getInstance().generateOAuthPreview(this.context);
			vscode.window.showErrorMessage("Access Token has expired.");
			console.log(err);
		});

		return scriptLibraries;

	}		
}

export class Dependency extends vscode.TreeItem {

	constructor(
		public readonly label: string,
		public readonly task: string,
		public readonly command: vscode.Command
	) {
		super(label);
	}

	get tooltip(): string {
		return `${this.label}`;
	}

	get description(): string {
		return this.task;
	}

}
