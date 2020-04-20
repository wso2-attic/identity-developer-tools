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

/**
 * Helper class for Tree View.
 * Implemented to return UI representation (TreeItem) of the elements that gets displayed in vi.
 */
export class Dependency extends vscode.TreeItem {

    constructor(
        public readonly label: string,
        public readonly task: string,
        public readonly command: vscode.Command,
    ) {
        super(label);
    }

    /**
     * Get the label of the tag.
     */
    public get tooltip(): string {
        return `${this.label}`;
    }

    /**
     * Get the description of the tag.
     */
    public get description(): string {
        return this.task;
    }

}
