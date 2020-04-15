/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.identity.developer.lsp.debug.dap.messages;

import com.google.gson.JsonElement;

import java.util.List;

/**
 * Variables request from the DAP.
 */
public class VariablesRequest extends Request {

    private int variablesReference;

    public VariablesRequest(String type, long id, String command,
                            List<Argument> arguments) {

        super(type, id, command, arguments);
        if (arguments.get(0) != null) {
            this.variablesReference = ((JsonElement) arguments.get(0).getValue()).getAsInt();
        }
    }

    /**
     * Gets the variablesReference.
     *
     * @return The Reference to the Variable container.
     */
    public int getVariablesReference() {

        return variablesReference;
    }

    /**
     * Sets the variablesReference.
     *
     * @param variablesReference Reference to the Variable container if the data breakpoint is requested for a child
     *                           of the container.
     */
    public void setVariablesReference(int variablesReference) {

        this.variablesReference = variablesReference;
    }
}
