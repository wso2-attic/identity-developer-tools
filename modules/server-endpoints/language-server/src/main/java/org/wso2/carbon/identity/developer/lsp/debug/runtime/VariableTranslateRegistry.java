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
import org.wso2.carbon.identity.developer.lsp.debug.runtime.translators.HttpServletRequestTranslator;
import org.wso2.carbon.identity.developer.lsp.debug.runtime.translators.HttpServletResponseTranslator;
import org.wso2.carbon.identity.developer.lsp.debug.runtime.translators.SAMLRequestTranslator;
import org.wso2.carbon.identity.developer.lsp.debug.runtime.translators.SAMLResponseTranslator;
import org.wso2.carbon.identity.java.agent.host.MethodContext;

import java.util.HashMap;

/**
 * Registry to get the relevant Translator for the Method Context.
 *  This class is used to get the corresponding Builder for the Method Context and
 *  Get relavant Translators for the arguments.
 */
public class VariableTranslateRegistry {
    private HashMap<String, VariableBuilder> registry = new HashMap<>();

    public VariableTranslateRegistry() {

        readConfig();

    }

    private void readConfig() {

        String samlEntrykey = getKeyFromContext(DAPConstants.SAML_ENTRY_CLASS, DAPConstants.SAML_ENTRY_METHOD,
                DAPConstants.SAML_ENTRY_SIGNATURE);

        String samlExitkey =
                getKeyFromContext(DAPConstants.SAML_EXIT_CLASS,
                        DAPConstants.SAML_EXIT_METHOD, DAPConstants.SAML_EXIT_SIGNATURE);

        registry.put(samlEntrykey, new SAMLEntryVariableBuilder(this));
        registry.put(samlExitkey, new SAMLExitVariableBuilder(this));
    }

    public VariableBuilder getVariablesBuilder(MethodContext methodContext) {

        return registry.get(
                getKeyFromContext(methodContext.getClassName(),
                        methodContext.getMethodName(), methodContext.getMethodSignature()));
    }

    private String getKeyFromContext(String className, String methodName, String methodSignature) {

        return className + "#" + methodName + "#" + methodSignature;
    }

    public Object translateHttpRequest(Object argument, int variablesReference) {
        return HttpServletRequestTranslator.getInstance().translate(argument,
                variablesReference);
    }

    public Object translateSAMLRequest(Object argument, int variablesReference) {
        return SAMLRequestTranslator.getInstance().translate(argument,
                variablesReference);
    }

    public Object translateHttpResponse(Object argument, int variablesReference) {
        return HttpServletResponseTranslator.getInstance().translate(argument,
                variablesReference);
    }

    public Object translateSAMLResponse(Object argument, int variablesReference) {
        return SAMLResponseTranslator.getInstance().translate(argument,
                variablesReference);
    }

}

