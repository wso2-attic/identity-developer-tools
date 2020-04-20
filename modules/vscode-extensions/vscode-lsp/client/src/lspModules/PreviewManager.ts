import * as fs from "fs";
import keytar = require("keytar");
import * as path from "path";
import format = require("string-template");
import * as vscode from "vscode";
import {Config} from "../Config";
import {DebugConstants} from "../DebugConstants";
import {FileHandler} from "./fileHandler";
import {Wso2OAuth} from "./oAuthService";
import {ViewPanelHolder} from "./ViewPanelHolder";

const scope = "internal_application_mgt_create internal_application_mgt_delete internal_application_mgt_update " +
    "internal_application_mgt_view internal_functional_lib_view";
const fileHandler = new FileHandler();

/**
 * This is the class responsible for setting the web view.
 */
export class PreviewManager {

    /**
     * Method give access to get the instance of PreviewManager.
     */
    public static getInstance(): PreviewManager {

        if (!PreviewManager.instance) {
            PreviewManager.instance = new PreviewManager();
        }

        return PreviewManager.instance;
    }

    private static instance: PreviewManager;
    private previewManagers = new Map<string, ViewPanelHolder>();

    private constructor() {
    }

    /**
     * This is a getter which gets all the preview managers.
     */
    public getPreviewManagers() {

        return this.previewManagers;
    }

    /**
     * Generate the web View panel to render web view.
     */
    public generateWebViewPanel(xmlFilePath, context) {

        const previewManager = PreviewManager.getInstance();
        const previewManagers = previewManager.getPreviewManagers();
        const serviceName = String(fileHandler.extractFileName(xmlFilePath));
        // Read the XML file and generate the web view panel.
        fs.readFile(xmlFilePath, "utf8", (err: any, data: any) => {
            // Get the text of the file.
            const code = String(data);

            const pathUri = vscode.Uri.file(
                path.join(context.extensionPath, "client", "src", "ui", Config.DIAGRAM_HTML_NAME),
            ).with({scheme: "vscode-resource"});

            const pathCss = vscode.Uri.file(
                path.join(context.extensionPath, "client", "src", "ui", "css"),
            ).with({scheme: "vscode-resource"});

            const pathJS = vscode.Uri.file(
                path.join(context.extensionPath, "client", "src", "ui", "js"),
            ).with({scheme: "vscode-resource"});

            const panel = vscode.window.createWebviewPanel(
                "Diagram",
                serviceName,
                vscode.ViewColumn.One,
                {
                    enableScripts: true,
                    retainContextWhenHidden: true,
                },
            );
            // Assign html code to the web view panel.
            const htmlGenerated = previewManager.getWebviewContentForDiagram(code,
                pathUri, xmlFilePath, pathCss, pathJS);
            panel.webview.html = htmlGenerated;
            panel.webview.onDidReceiveMessage(
                (message) => {
                    fileHandler.handleButtonClick(message, xmlFilePath);
                },
                undefined,
                context.subscriptions,
            );
            const key = fileHandler.extractFileName(xmlFilePath).replace("%20", " ");
            const viewPanelHolder = new ViewPanelHolder(panel, htmlGenerated);
            previewManagers.set(key, viewPanelHolder);
        });
    }

    /**
     * Generate the html of web view for the diagram.
     */
    public getWebviewContentForDiagram(xmlCode, pathForHtml, filepath, pathCss, pathJS) {

        const htmlCode = fileHandler.getHTMLCode(pathForHtml.fsPath);

        return format(htmlCode, {
            SAML_REQUEST: DebugConstants.SAML_REQUEST_HTML,
            SAML_RESPONSE: DebugConstants.SAML_RESPONSE_HTML,
            myXML: xmlCode,
            myfilepath: filepath,
            pathCss,
            pathJS,
        });
    }

    /**
     * Generate the html of oAuth web.
     */
    public async generateOAuthPreview(context) {

        const htmlFilePath = vscode.Uri.file(
            path.join(context.extensionPath, "client", "src", "ui", Config.AUTHENTICATION_HTML_NAME),
        );
        const html = fileHandler.getHTMLCode(htmlFilePath.fsPath);
        const panel = vscode.window.createWebviewPanel(
            "WSO2 Login",
            "WSO2 Login",
            vscode.ViewColumn.One,
            {
                enableScripts: true,
                retainContextWhenHidden: true,
            },
        );

        const preClientId = vscode.workspace.getConfiguration().get(DebugConstants.IAM_SERVICE_CLIENT_ID);
        const preTenant = vscode.workspace.getConfiguration().get(DebugConstants.IAM_TENANT);
        let preClientSecret;
        const secret = keytar.getPassword(DebugConstants.CLIENT_SECRET, DebugConstants.CLIENT_SECRET);
        await secret.then((result) => {
            preClientSecret = result;
        });

        panel.webview.html = format(html, {
            wso2ISClientID: String(preClientId),
            wso2ISClientSecret: String(preClientSecret),
            wso2isTenant: String(preTenant),
            wso2isurl: vscode.workspace.getConfiguration().get(DebugConstants.IAM_URL),
        });

        const url = vscode.workspace.getConfiguration().get(DebugConstants.IAM_URL);
        panel.webview.onDidReceiveMessage(
            async (message) => {
                if (message.command === "login") {
                    // To start the server.
                     await new Wso2OAuth(8010, context).StartProcess();

                    // Set the url to extension configuration.
                     vscode.workspace.getConfiguration().update(DebugConstants.IAM_URL, message.url);

                    // Set the tenant domain to extension configuration.
                     vscode.workspace.getConfiguration().update(DebugConstants.IAM_TENANT, message.tenant);

                    // Set the client id to extension configurations.
                     vscode.workspace.getConfiguration().update(DebugConstants.IAM_SERVICE_CLIENT_ID, message.clientID);

                    // Set Client Secret to system key chain.
                     await keytar.setPassword(DebugConstants.CLIENT_SECRET,
                        DebugConstants.CLIENT_SECRET, message.clientSecret);

                    // Open the login page.
                     vscode.commands.executeCommand(
                        "vscode.open",
                        vscode.Uri.parse(Config.PATH_GET_AUTH_CODE(url, message.clientID,
                            Config.VSCODE_SP_REDIRECT_URL, scope)),
                    );
                    // TODO check typo changes broke anything
                } else if (message.command === "access") {
                    // Set Access Token to system key chain.
                    await keytar.setPassword(DebugConstants.ACCESS_TOKEN, DebugConstants.ACCESS_TOKEN,
                        message.accessToken);
                    // Close the webview
                    await vscode.commands.executeCommand("workbench.action.closeActiveEditor");
                    // Show the success message
                    vscode.window.showInformationMessage(DebugConstants.MESSAGE_CONFIGURATION_SUCCESS);
                }

            },
            undefined,
            context.subscriptions,
        );

    }

}
