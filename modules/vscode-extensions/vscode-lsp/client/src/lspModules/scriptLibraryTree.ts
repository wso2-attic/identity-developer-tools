import * as fs from "fs";
import * as path from "path";
import * as vscode from "vscode";
import {DebugConstants} from "../DebugConstants";
import {Dependency} from "./Dependency";
import {PreviewManager} from "./PreviewManager";

import axios from "axios";
import keytar = require("keytar");
import os = require("os");
import {Config} from "../Config";
import {ExtensionConstants} from "../ExtensionConstants";

const tempPath = os.tmpdir();

/**
 * Class responsible for Getting Script Library for Tree View.
 */
export class ScriptLibraryTree implements vscode.TreeDataProvider<Dependency> {

    public onDidChangeTreeDataHelper: vscode.EventEmitter<Dependency | undefined> =
        new vscode.EventEmitter<Dependency | undefined>();
    public readonly onDidChangeTreeData: vscode.Event<Dependency | undefined> = this.onDidChangeTreeDataHelper.event;

    private readonly context;

    constructor(context) {

        this.context = context;
    }

    public refresh(): void {

        this.onDidChangeTreeDataHelper.fire();
    }

    public getTreeItem(element: Dependency): vscode.TreeItem {

        return element;
    }

    public getChildren(): Thenable<Dependency[]> {

        return Promise.resolve(this.getListOfItems(this.context));
    }

    /**
     * Given the path to package.json, read all its dependencies and devDependencies.
     */
    private async getListOfItems(context): Promise<Dependency[]> {

        const scriptLibraries = [];
        const url = vscode.workspace.getConfiguration().get(DebugConstants.IAM_URL);
        const tenant = vscode.workspace.getConfiguration().get(DebugConstants.IAM_TENANT);
        let accessToken;
        // Get the access token from the system key chain.
        const secret = keytar.getPassword(DebugConstants.ACCESS_TOKEN, DebugConstants.ACCESS_TOKEN);
        await secret.then((result) => {
            accessToken = result; // Assign the value to access token.
        });

        // To bypass the self signed server error.
        process.env[ExtensionConstants.NODE_TLS_REJECT_UNAUTHORIZED] = "0";

        await axios({
            headers: {
                Authorization: "Bearer " + accessToken,
                accept: "*/*",

            },
            method: "get",
            url: Config.PATH_GET_ALL_SCRIPT_LIBRARY(url, tenant),
        }).then(async (response) => {
            // Once we get the response, extract the access token from
            // the response body
            // Show the sucess message in the vscode.
            vscode.window.showInformationMessage(DebugConstants.MESSAGE_SCRIPTS_IMPORT_SUCCESS);
            // Create a node_modules folder in the temp directory.
            if (!fs.existsSync(path.join(tempPath, "node_modules"))) {
                fs.mkdir(path.join(require("os").tmpdir(), "node_modules/"), (err: any) => {
                    if (err) {
                        throw err;
                    }
                });
            }

            if (response.data.count > 0) {
                for (const application of response.data.applications) {
                    const scriptLibraryName = application.name;
                    let content;

                    await axios({

                        method: "get",
                        url: Config.PATH_GET_SCRIPT_LIBRARY_BY_NAME(url, tenant, scriptLibraryName),

                        // Set the content type header, so that we get the response in JSOn
                        headers: {
                            Authorization: "Bearer " + accessToken,
                            accept: "*/*",
                        },
                    }).then(async (responseVal) => {
                        content = responseVal.data;
                    })
                        .catch((err) => {
                            PreviewManager.getInstance().generateOAuthPreview(this.context);
                            vscode.window.showErrorMessage(DebugConstants.MESSAGE_ACCESS_TOKEN_EXPIRED);
                        });

                    if (fs.existsSync(path.join(tempPath, "node_modules", scriptLibraryName))) {
                        const inputPath = path.join(tempPath, "node_modules", scriptLibraryName, "index.js");
                        fs.writeFile(inputPath, content, (err: any) => {
                            if (err) {
                                throw err;
                            }
                        });

                    } else {
                        fs.mkdirSync(path.join(tempPath, "node_modules", scriptLibraryName), {recursive: true});
                        const inputPath = path.join(tempPath, "node_modules", scriptLibraryName, "index.js");
                        fs.writeFile(inputPath, content, (err: any) => {
                            if (err) {
                                throw err;
                            }
                        });
                    }
                    scriptLibraries.push(new Dependency(scriptLibraryName,
                        application.description,
                        {
                            arguments: [scriptLibraryName],
                            command: "extension.scriptLibrariesFromTreeView",
                            title: "Edit Script",
                        },
                    ));
                }
            }
        }).catch((err) => {
            PreviewManager.getInstance().generateOAuthPreview(this.context);
            vscode.window.showErrorMessage(DebugConstants.MESSAGE_ACCESS_TOKEN_EXPIRED);
        });

        return scriptLibraries;

    }
}
