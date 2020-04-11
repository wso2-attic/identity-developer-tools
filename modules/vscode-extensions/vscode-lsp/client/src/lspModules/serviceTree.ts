import * as vscode from "vscode";
import {DebugConstants} from "../DebugConstants";
import {Dependency} from "./Dependency";
import {PreviewManager} from "./PreviewManager";

import axios from "axios";
import keytar = require("keytar");
import {Config} from "../Config";

/**
 *  Class responsible for Getting Service Providers for Tree View.
 */
export class ServiceTree implements vscode.TreeDataProvider<Dependency> {

    public onDidChangeTreeDataHelper: vscode.EventEmitter<Dependency | undefined> = new vscode.EventEmitter<Dependency |
        undefined>();
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
        return Promise.resolve(this.getListOfItems());
    }

    /**
     * Read all service providers from identity server.
     */
    private async getListOfItems(): Promise<Dependency[]> {
        const services = [];
        const url = vscode.workspace.getConfiguration().get(DebugConstants.IAM_URL);
        const tenant = vscode.workspace.getConfiguration().get(DebugConstants.IAM_TENANT);
        let accessToken;
        // Get the access token from the system key chain.
        const secret = keytar.getPassword(DebugConstants.ACCESS_TOKEN, DebugConstants.ACCESS_TOKEN);
        await secret.then((result) => {
            accessToken = result; // Assign the value to access token.
        });
        // To bypass the self signed server error.
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        await axios({
            headers: {
                Authorization: "Bearer " + accessToken,
                accept: "*/*",
            },
            method: "get",
            url: Config.PATH_APPLICATIONS(url, tenant),
        }).then((response) => {
            // Once we get the response, extract the access token from
            // the response body
            // Show the sucess message in the vscode.
            if (response.data.count > 0) {
                for (const application of response.data.applications) {
                    services.push(new Dependency(application.name,
                        application.description,
                        {
                            arguments: [response.data.applications, application.name],
                            command: "extension.serviceProviderFromTreeView",
                            title: "",
                        },
                    ));
                }
            }
            vscode.window.showInformationMessage(DebugConstants.MESSAGE_SERVICE_FETCHED_SUCCESS);
        }).catch((err) => {
            // Show the success message in the vscode.
            PreviewManager.getInstance().generateOAuthPreview(this.context);
            vscode.window.showErrorMessage(DebugConstants.MESSAGE_ACCESS_TOKEN_EXPIRED);
        });

        return services;

    }
}
