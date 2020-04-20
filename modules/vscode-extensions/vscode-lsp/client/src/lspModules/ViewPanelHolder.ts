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

/**
 * This class is to hold the panel variable so that It can be accessed after the panel variable is created.
 * It holds the panel variable and the current html because current html can't be accessed after it is set to panel.
 */
export class ViewPanelHolder {

    private readonly panel;
    private currentHtml;

    constructor(panel, currentHtml: string) {

        this.panel = panel;
        this.currentHtml = currentHtml;
    }

    /**
     * This is a getter to get the panel.
     */
    public getPanel() {

        return this.panel;
    }

    /**
     * Gets the currentHtml.
     */
    public getCurrentHtml() {

        return this.currentHtml;
    }

    /**
     * Sets the currentHtml.
     *
     * @param currentHtml The current Html hold by the panel.
     */
    public setCurrentHtml(currentHtml) {

        this.currentHtml = currentHtml;
    }
}
