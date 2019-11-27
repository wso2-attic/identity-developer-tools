import * as vscode from 'vscode';
import * as fs from 'fs';
import * as path from 'path';

export class ConfigProvider implements vscode.TreeDataProvider<Dependency> {

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
	 * Given the path to package.json, read all its dependencies and devDependencies.
	 */
	private getListOfItems(): Dependency[] {		

		return [
			new Dependency("Login With WSO2 IS", "To get Acess Token", {
				command: 'extension.oAuth',
				title: '',
			})
		];
	}

	private pathExists(p: string): boolean {
		try {
			fs.accessSync(p);
		} catch (err) {
			return false;
		}

		return true;
	}
}

export class Dependency extends vscode.TreeItem {

	constructor(
		public readonly label: string,
		public readonly task:string,
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
