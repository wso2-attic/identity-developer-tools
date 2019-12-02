import * as vscode from 'vscode';
import * as fs from 'fs';
import * as path from 'path';
const axios = require('axios');
const keytar = require('keytar');
export class ServiceTree implements vscode.TreeDataProvider<Dependency> {

	private _onDidChangeTreeData: vscode.EventEmitter<Dependency | undefined> = new vscode.EventEmitter<Dependency | undefined>();
	readonly onDidChangeTreeData: vscode.Event<Dependency | undefined> = this._onDidChangeTreeData.event;

	constructor() {
	}

	refresh(): void {
		this._onDidChangeTreeData.fire();
	}

	getTreeItem(element: Dependency): vscode.TreeItem {
		return element;
	}

	getChildren(): Thenable<Dependency[]> {
		return Promise.resolve(this.getListOfItems());
	}

	/**
	 * Read all service providers from identity server.
	 */
	private async getListOfItems(): Promise<Dependency[]> {
		let services = [];
		var url = vscode.workspace.getConfiguration().get('IAM.URL');
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
			url: url + `/t/carbon.super/api/server/v1/applications`,

			// Set the content type header, so that we get the response in JSOn
			headers: {
				Authorization: 'Bearer ' + acessToken,
				accept: '*/*'

			}
		}).then((response) => {
			// Once we get the response, extract the access token from
			// the response body			
			// Show the sucess message in the vscode.
			vscode.window.showInformationMessage("Successfully retrieve the services");

			for (let index = 0; index < response.data.applications.length; index++) {
				services.push(new Dependency(response.data.applications[index].name,
					response.data.applications[index].description,
					{
						command: 'extension.serviceProvierFromTreeView',
						title: '',
						arguments:[response.data.applications,response.data.applications[index].name]
					}
				));
			}

		}).catch((err) => {
			
			console.log(err);

			// Show the sucess message in the vscode.
			vscode.window.showErrorMessage("Acess Token has expired.");
		});

		return services;

	}
}

export class Dependency extends vscode.TreeItem {

	constructor(
		public readonly label: string,
		public readonly task?: string,
		public readonly command?: vscode.Command
	) {
		super(label);
	}

	get tooltip(): string {
		return `${this.task}`;
	}

	get description(): string {
		return this.task;
	}

}
