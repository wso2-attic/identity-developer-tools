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
public class SAMLExitVariableBuilder implements VariableBuilder {

    private SAMLExitResponseVariable samlExitResponseVariable;
    private VariableTranslateRegistry variableTranslateRegistry;

    public SAMLExitVariableBuilder(VariableTranslateRegistry variableTranslateRegistry) {

        this.samlExitResponseVariable = new SAMLExitResponseVariable();
        this.variableTranslateRegistry = variableTranslateRegistry;
    }

    @Override
    public Argument<Map<String, Object>> build(Object[] arguments, int variablesReference) {

        this.samlExitResponseVariable.setHttpServletResponse(
                variableTranslateRegistry.translateHttpResponse(arguments[1],
                        variablesReference));
        this.samlExitResponseVariable.setSAMLResponse(variableTranslateRegistry.translateSAMLResponse(arguments[3],
                variablesReference));
        return new Argument<>(samlExitResponseVariable.getVariables());
    }

}
