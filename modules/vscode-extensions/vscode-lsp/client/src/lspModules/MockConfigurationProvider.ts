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

import * as vscode from "vscode";
import {CancellationToken, DebugConfiguration, ProviderResult, WorkspaceFolder} from "vscode";

/**
 * Configuration for Debugger.
 */
export class MockConfigurationProvider implements vscode.DebugConfigurationProvider {

    /**
     * Massage a debug configuration just before a debug session is being launched,
     * e.g. add all missing attributes to the debug configuration.
     */
    public resolveDebugConfiguration(folder: WorkspaceFolder | undefined, config: DebugConfiguration,
                                     token?: CancellationToken): ProviderResult<DebugConfiguration> {

        // if launch.json is missing or empty
        if (!config.type && !config.request && !config.name) {
            const editor = vscode.window.activeTextEditor;

            if (editor && editor.document.languageId === "javascript") {
                config.type = "mock";
                config.name = "Launch";
                config.request = "launch";
                config.program = "${file}";
                config.outFiles = "${file}.out";
                config.stopOnEntry = true;
            }
        }

        if (!config.program) {
            // return config;
            return vscode.window.showInformationMessage("Cannot find a program to debug")
                .then((_) => {
                    return undefined;	// abort launch
                });
        }

        return config;
    }
}
