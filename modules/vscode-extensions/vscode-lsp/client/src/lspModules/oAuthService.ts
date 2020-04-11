/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as express from "express";
import {Server} from "http";
import * as path from "path";
import * as vscode from "vscode";
import {DebugConstants} from "../DebugConstants";
import {FileHandler} from "./fileHandler";
import {ScriptLibraryTree} from "./scriptLibraryTree";
import {ServiceTree} from "./serviceTree";

import axios from "axios";
import keytar = require("keytar");
import {Config} from "../Config";
import {ExtensionConstants} from "../ExtensionConstants";

const fileHandler = new FileHandler();

/**
 * This class is to help the extension to authenticate.
 */
export class Wso2OAuth {
    public app: express.Express;
    public server: Server;
    public context;

    constructor(public port: number, context) {
        this.app = express();
        this.app.use(express.json(), express.urlencoded({extended: false}));
        this.context = context;
    }

    /**
     * Method to start the authentication process.
     *
     * @constructor
     */
    public async StartProcess() {
        this.server = this.app.listen(this.port);
        this.app.get(Config.PATH_OAUTH, async (req, res) => {
            try {
                // Get client ID from the extension configurations.
                const clientID = vscode.workspace.getConfiguration().get(DebugConstants.IAM_SERVICE_CLIENT_ID);
                let clientSecret;

                // Get the client secret from the system key chain.
                const secret = keytar.getPassword(DebugConstants.CLIENT_SECRET, DebugConstants.CLIENT_SECRET);
                await secret.then((result) => {
                    clientSecret = result; // Assign the value to client secret
                });

                // String created to encode from base64.
                const data = String(clientID) + ":" + String(clientSecret);
                const buff = new Buffer(data);

                // Base64 Encoding
                const base64data = buff.toString(DebugConstants.STRING_ENCODING);
                const requestToken = req.query.code;

                // Get the url of the wso2 IS.
                const url = vscode.workspace.getConfiguration().get(DebugConstants.IAM_URL);

                // To bypass the self signed server error.
                process.env[ExtensionConstants.NODE_TLS_REJECT_UNAUTHORIZED] = "0";
                axios({

                    method: "post",
                    url: Config.PATH_AUTHORISE(url, requestToken, Config.VSCODE_SP_REDIRECT_URL),

                    // Set the content type header, so that we get the response in JSOn
                    headers: {
                        Authorization: "Basic " + base64data,
                        accept: "application/json",
                    },
                })
                    .then(async (response) => {
                        keytar.setPassword(DebugConstants.ACCESS_TOKEN,
                            DebugConstants.ACCESS_TOKEN, String(response.data.access_token));

                        // Close the webview.
                        await vscode.commands.executeCommand("workbench.action.closeActiveEditor");

                        // To create the service providers tree view.
                        vscode.window.createTreeView("service-providers", {
                            treeDataProvider: new ServiceTree(this.context),
                        });

                        // To create the script libraries tree view.
                        vscode.window.createTreeView("script-libraries", {
                            treeDataProvider: new ScriptLibraryTree(this.context),
                        });

                        // Show the sucess message in the vscode.
                        vscode.window.showInformationMessage(DebugConstants.MESSAGE_CONFIGURATION_SUCCESS);
                    })
                    .catch((err) => {
                        vscode.window.showErrorMessage(DebugConstants.MESSAGE_ACCESS_TOKEN_EXPIRED);
                    });
                // The response html.
                const htmlFilePath = vscode.Uri.file(
                    path.join(this.context.extensionPath, "client", "src", "ui", Config.SUCCESS_HTML_NAME),
                );
                const successHtml = fileHandler.getHTMLCode(htmlFilePath.fsPath);
                res.send(successHtml);

                // Close the server.
                this.server.close();
                vscode.commands.executeCommand("workbench.action.closeActiveEditor");

            } catch (err) {
                vscode.window.showErrorMessage(DebugConstants.MESSAGE_ACCESS_TOKEN_EXPIRED);
            }
        });
    }
}
