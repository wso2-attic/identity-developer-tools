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

package org.wso2.carbon.identity.developer.lsp.debug.runtime.builders;

import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.Argument;
import org.wso2.carbon.identity.developer.lsp.debug.runtime.VariableTranslateRegistry;

import java.util.Map;

/**
 * Builder to build the Variable Response for the variable Response.
 */
public class SAMLEntryVariableBuilder implements VariableBuilder {

    private SAMLEntryRequestVariable samlEntryRequestVariable;
    private VariableTranslateRegistry variableTranslateRegistry;


    public SAMLEntryVariableBuilder(VariableTranslateRegistry variableTranslateRegistry) {

        this.variableTranslateRegistry = variableTranslateRegistry;
        this.samlEntryRequestVariable = new SAMLEntryRequestVariable();
    }

    @Override
    public Argument<Map<String, Object>> build(Object[] arguments, int variablesReference) {

        this.samlEntryRequestVariable.setHttpServletRequest(variableTranslateRegistry.translateHttpRequest(arguments[0],
                variablesReference));
        this.samlEntryRequestVariable.setSAMLRequest(variableTranslateRegistry.translateSAMLRequest(arguments[0],
                variablesReference));
        return new Argument<>(samlEntryRequestVariable.getVariables());
    }
}
