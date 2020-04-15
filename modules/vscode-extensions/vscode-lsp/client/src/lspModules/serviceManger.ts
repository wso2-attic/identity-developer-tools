import * as vscode from "vscode";

import axios from "axios";
import keytar = require("keytar");
import {Config} from "../Config";
import {DebugConstants} from "../DebugConstants";
import {ExtensionConstants} from "../ExtensionConstants";
import {FileHandler} from "./fileHandler";
import {PreviewManager} from "./PreviewManager";

// Object of the FileHandler.
const fileHandler = new FileHandler();

export class ServiceManger {

    private readonly context;

    constructor(context) {

        this.context = context;
    }

    /**
     * Get the services using the apis.
     */
    public async getServicesList() {

        const url = vscode.workspace.getConfiguration().get(DebugConstants.IAM_URL);
        const tenant = vscode.workspace.getConfiguration().get(DebugConstants.IAM_TENANT);
        let accessToken;
        // Get the access token from the system key chain.
        const secret = keytar.getPassword(DebugConstants.ACCESS_TOKEN, DebugConstants.ACCESS_TOKEN);
        await secret.then((result) => {
            accessToken = result; // Assign the value to access toke.
        });

        // To bypass the self signed server error.
        process.env[ExtensionConstants.NODE_TLS_REJECT_UNAUTHORIZED] = "0";

        axios({
            method: "get",
            url: Config.PATH_APPLICATIONS(url, tenant),

            // Set the content type header, so that we get the response in JSOn
            headers: {
                Authorization: "Bearer " + accessToken,
                accept: "*/*",
            },
        }).then(async (response) => {
            this.createListOfServices(response.data.applications);
        })
            .catch((err) => {
                PreviewManager.getInstance().generateOAuthPreview(this.context);
                vscode.window.showErrorMessage(DebugConstants.MESSAGE_ACCESS_TOKEN_EXPIRED);
            });
    }

    /**
     * Create a list in command pallet.
     */
    public async createListOfServices(servicesArray) {

        const services = [];
        try {
            for (const service of servicesArray) {
                services.push(service.name);
            }
        } catch (err) {
            vscode.window.showErrorMessage(DebugConstants.MESSAGE_SERVICE_IMPORT_ERROR);
        }

        // Show the list of services in command pallet.
        const result = await vscode.window.showQuickPick(
            services,
            {placeHolder: "Select Service"},
        );
        if (result !== undefined) {
            this.getIDOfService(servicesArray, result);
        } else {
            vscode.window.showInformationMessage(DebugConstants.MESSAGE_SELECT_SERVICE_INFO);
        }

    }

    /**
     * Get the id of the selected service.
     */
    public getIDOfService(servicesArray, service) {

        let serviceID;
        for (const serviceEntity of servicesArray) {
            if (service === serviceEntity.name) {
                serviceID = serviceEntity.id;
            }
        }
        this.exportService(serviceID, service);
    }

    /**
     * Export the xml of the service.
     */
    public async exportService(serviceID, service) {

        const url = vscode.workspace.getConfiguration().get(DebugConstants.IAM_URL);
        const tenant = vscode.workspace.getConfiguration().get(DebugConstants.IAM_TENANT);
        let accessToken;
        // Get the access token from the system key chain.
        const secret = keytar.getPassword(DebugConstants.ACCESS_TOKEN, DebugConstants.ACCESS_TOKEN);
        await secret.then((result) => {
            accessToken = result; 		// Assign the value to access token.
        });

        // To bypass the self signed server error.
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

        axios({
            headers: {
                Authorization: "Bearer " + accessToken,
                accept: "*/*",

            },
            method: "get",
            params: {
                exportSecrets: false,
            },
            url: Config.PATH_APPLICATION_EXPORT(url, tenant, serviceID),
        }).then(async (response) => {
            await fileHandler.createXMLFile(response.data, service);
        }).catch((err) => {
            vscode.window.showErrorMessage(DebugConstants.MESSAGE_ACCESS_TOKEN_EXPIRED);
        });
    }
}
