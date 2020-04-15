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

import * as fs from "fs";
import * as path from "path";
import * as temp from "temp";
import * as url from "url";
import * as vscode from "vscode";
import * as xmlQuery from "xml-query";
import * as XmlReader from "xml-reader";
import * as xml2js from "xml2js";
import {DebugConstants} from "../DebugConstants";

import axios from "axios";
import {Config} from "../Config";
import keytar = require("keytar");

/**
 * This class helps to read and write the files when needed.
 */
export class FileHandler {
    /**
     * Read the XML files code from the given file path
     */
    public readXML(filePath: any): string {

        return String(fs.readFileSync(filePath, "utf8"));
    }

    /**
     * Extract the file name from the gven filepath.
     */
    public extractFileName(filePath): string {

        const parsed = url.parse(filePath);
        return path.basename(parsed.pathname).replace(/\.[^/.]+$/, "");
    }

    /**
     * Extract the adpative scripts from the xml.
     */
    public extractAdaptiveScript(filePath) {

        const xmlCode = this.readXML(filePath);
        const ast = XmlReader.parseSync(String(xmlCode));
        return xmlQuery(ast).find(DebugConstants.LOCAL_AND_OUTBOUND_AUTHENTICATION_CONFIG)
            .find(DebugConstants.AUTHENTICATION_SCRIPTS).text();
    }

    /**
     * Read the html code from the file.
     */
    public getHTMLCode(htmlFilePath) {

        return fs.readFileSync(htmlFilePath, "utf8");
    }

    /**
     * Open available adaptiveScriptFile or create a new adaptive script file.
     */
    public async handleButtonClick(message, xmlFilePath) {

        // Get the name of the service.
        const serviceName = this.extractFileName(xmlFilePath).replace("%20", " ");
        // Handle the button click in web view.
        if (String(message.command) === DebugConstants.SCRIPT_FILE) {
            const adaptive = this.extractAdaptiveScript(xmlFilePath);
            this.createOrOpenAdaptiveScript(adaptive, serviceName);
        } else if (String(message.command) === DebugConstants.DEFAULT_SCRIPT_FILE) {
            const adaptiveScript = this.createDefaultAdaptiveScript(message.data);
            this.createOrOpenAdaptiveScript(adaptiveScript, serviceName);
        }
    }

    /**
     * Open available adaptiveScriptFile or create a new adaptive script file.
     */
    public createOrOpenAdaptiveScript(adaptiveScript, serviceName) {

        // Automatically track and cleanup files at exit
        temp.track();

        // Create a temp file.
        temp.mkdir(DebugConstants.ADAPTIVE_SCRIPT, (err, dirPath) => {
            if (err) {
                throw err;
            }
            const inputPath = path.join(dirPath, serviceName + ".authjs");
            fs.writeFile(inputPath, adaptiveScript, (error) => {
                if (error) {
                    throw error;
                }
                process.chdir(dirPath);
                vscode.workspace.openTextDocument(inputPath).then((document) => {
                    vscode.window.showTextDocument(document, 2, false);
                });
            });
        });

    }

    /**
     * Create adaptive script when no scripts found.
     */
    public createDefaultAdaptiveScript(executeSteps) {

        return `var onLoginRequest = function (context) {\n` +
            this.bindExecuteSteps(executeSteps)
            + `\n};`;
    }

    /**
     * Bind the executeStep method to the script.
     */
    public bindExecuteSteps(executeSteps) {

        let executeStep = ``;
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
     * Sync the Adaptive Script with XML File.
     */
    public async syncServiceProviderWithAdaptiveScript() {

        const parser = new xml2js.Parser({explicitArray: false});
        const xmlBuilder = new xml2js.Builder({cdata: true});
        const {activeTextEditor} = vscode.window;
        const {document} = activeTextEditor;
        const fileNames = []; // Array of file names.
        let files;
        if (fs.existsSync(path.join(vscode.workspace.rootPath, "IAM", "Apps"))) {
            files = this.getFilesFromDir(path.join(vscode.workspace.rootPath, "IAM", "Apps"), [".authxml"]);
            files.forEach((file) => {
                // Add files names to array.
                fileNames.push(path.basename(file).replace(/\.[^/.]+$/, ""));
            });
        }
        // Variable to assign the new xml.
        let newXml;
        // Get the adaptive Script code.
        const newAdaptiveScriptCode = document.getText();
        // Get the service name.
        const serviceName = this.extractFileName(document.uri.fsPath).replace("%20", " ");
        // Path of the xml file of the service.
        const xmlFile = path.join(vscode.workspace.rootPath, "IAM", "Apps", files[fileNames.indexOf(serviceName)]);
        // Current code of the xml file.
        const xml = this.readXML(xmlFile);
        // Get the old adaptive script.
        const adaptiveScript = this.extractAdaptiveScript(xmlFile);
        // Save the active adaptive script before sync.
        document.save();

        parser.parseString(xml, (err, result) => {
            // Check whether AuthenticationScript node is Available.
            if (DebugConstants.AUTHENTICATION_SCRIPTS in result.ServiceProvider.LocalAndOutBoundAuthenticationConfig) {
                newXml = xml.replace(adaptiveScript, newAdaptiveScriptCode)
                    .replace(DebugConstants.AUTHENTICATION_SCRIPT_TRUE, DebugConstants.AUTHENTICATION_SCRIPT_FALSE);
            } else {
                // Add the AuthenticationScript node to the xml file.
                result.ServiceProvider.LocalAndOutBoundAuthenticationConfig.AuthenticationScript = {
                    $: {enabled: "false", language: "application/javascript"}, _: `//<enabled false>
${newAdaptiveScriptCode}`,
                };
                // change the xml to the new xml.
                newXml = xmlBuilder.buildObject(result);
            }
        });
        // To write the new data to the xml file.
        fs.writeFile(xmlFile, newXml, (err) => {
            if (err) {
                throw err;
            }
            vscode.window.showInformationMessage(DebugConstants.MESSAGE_FILE_SAVED_SUCCESS);
        });
        // To update the service.
        this.updateService(newXml);
        await vscode.commands.executeCommand("workbench.action.closeActiveEditor");

    }

    /**
     * Create the xml file with of the service.
     */
    public async createXMLFile(xml, serviceName) {

        const fileNames = []; // To keep the names of the file.
        let files; // Array of available files in the directory
        if (fs.existsSync(path.join(vscode.workspace.rootPath, "IAM", "Apps"))) {
            files = this.getFilesFromDir(path.join(vscode.workspace.rootPath, "IAM", "Apps"), [".authxml"]);
            files.forEach((file) => {
                fileNames.push(path.basename(file).replace(/\.[^/.]+$/, ""));
            });
        } else {
            fs.mkdirSync(path.join(vscode.workspace.rootPath, "IAM", "Apps"), {recursive: true});
        }
        // Check whether the file already exsists.
        if (fileNames.includes(serviceName)) {
            const file = vscode.Uri.parse("file:" + path.join(vscode.workspace.rootPath, "IAM", "Apps",
                files[fileNames.indexOf(serviceName)]));

            // Open the file.
            vscode.workspace.openTextDocument(file).then(async (document) => {
                vscode.window.showTextDocument(document, 1, false);
            });
        } else {
            // Uri of the untitled file.
            const newFile = vscode.Uri.parse("file:" + path.join(vscode.workspace.rootPath, "IAM", "Apps",
                serviceName + ".authxml"));
            await fs.writeFile(path.join(vscode.workspace.rootPath, "IAM", "Apps", serviceName + ".authxml"),
                xml, (err) => {
                    if (err) {
                        throw err;
                    }
                });
            // Open the file.
            vscode.workspace.openTextDocument(newFile).then(async (document) => {
                vscode.window.showTextDocument(document, 1, false);
            });
            return;
        }
    }

    /**
     * Return a list of files of the specified fileTypes in the provided dir.
     */
    public getFilesFromDir(dir, fileTypes) {

        const filesToReturn = [];

        function walkDir(currentPath) {
            const files = fs.readdirSync(currentPath);
            for (const i in files) {
                const curFile = path.join(currentPath, files[i]);
                if (fs.statSync(curFile).isFile() && fileTypes.indexOf(path.extname(curFile)) !== -1) {
                    filesToReturn.push(curFile.replace(dir, ""));
                } else if (fs.statSync(curFile).isDirectory()) {
                    walkDir(curFile);
                }
            }
        }

        walkDir(dir);
        return filesToReturn;
    }

    /**
     * Update the service in the productIS.
     */
    public async updateService(file) {

        const iamUrl = vscode.workspace.getConfiguration().get(DebugConstants.IAM_URL);
        const tenant = vscode.workspace.getConfiguration().get(DebugConstants.IAM_TENANT);
        let accessToken;
        // Get the access token from the system key chain.
        const secret = keytar.getPassword(DebugConstants.ACCESS_TOKEN, DebugConstants.ACCESS_TOKEN);
        await secret.then((result) => {
            accessToken = result; // Assign the value to access token.
        });

        // To bypass the self signed server error.
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        const FormData = require("form-data");
        const bodyFormData = new FormData();
        bodyFormData.append("file", file);

        axios({
            data: bodyFormData,
            headers: {
                "Authorization": "Bearer " + accessToken,
                "Content-Type": "multipart/form-data",
            },
            method: "put",
            url: Config.PATH_APPLICATION_IMPORT(iamUrl, tenant),
        }).then(async (response) => {
            vscode.window.showInformationMessage(DebugConstants.MESSAGE_SERVICE_IMPORT_SUCCESS);
        }).catch((err) => {
            vscode.window.showErrorMessage(DebugConstants.MESSAGE_ACCESS_TOKEN_EXPIRED);
        });
    }
}
