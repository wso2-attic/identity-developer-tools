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

package org.wso2.carbon.identity.developer.lsp.debug.runtime;

import org.wso2.carbon.identity.developer.lsp.debug.DAPConstants;
import org.wso2.carbon.identity.developer.lsp.debug.runtime.builders.SAMLEntryVariableBuilder;
import org.wso2.carbon.identity.developer.lsp.debug.runtime.builders.SAMLExitVariableBuilder;
import org.wso2.carbon.identity.developer.lsp.debug.runtime.builders.VariableBuilder;
import org.wso2.carbon.identity.java.agent.host.MethodContext;

import java.util.HashMap;

/**
 * Registry to get the relevant Translator for the Method Context.
 */
public class VariableTranslateRegistry {
    private HashMap<String, VariableBuilder> registry = new HashMap<>();

    public VariableTranslateRegistry() {
        readconfig();

    }

    private void readconfig() {

        String samlEntrykey =
                DAPConstants.SAML_ENTRY_CLASS + DAPConstants.SAML_ENTRY_METHOD + DAPConstants.SAML_ENTRY_SIGNATURE;

        String samlExitkey =
                DAPConstants.SAML_EXIT_CLASS + DAPConstants.SAML_EXIT_METHOD + DAPConstants.SAML_EXIT_SIGNATURE;

        registry.put(samlEntrykey, new SAMLEntryVariableBuilder());
        registry.put(samlExitkey, new SAMLExitVariableBuilder());
    }

    public VariableBuilder getVariablesBuilder(MethodContext methodContext) {
        return registry.get(
                methodContext.getClassName() + methodContext.getMethodName() + methodContext.getMethodSignature());
    }
}

