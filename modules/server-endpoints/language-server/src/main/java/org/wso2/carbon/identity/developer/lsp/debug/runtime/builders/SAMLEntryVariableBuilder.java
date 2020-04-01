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
import org.wso2.carbon.identity.developer.lsp.debug.runtime.translators.HttpServletRequestTranslator;
import org.wso2.carbon.identity.developer.lsp.debug.runtime.translators.SAMLRequestTranslator;

import java.util.Map;

/**
 * Builder to build the Variable Response for the variable Response.
 */
public class SAMLEntryVariableBuilder implements VariableBuilder {

    private SAMLEntryRequestVariable samlEntryRequestVariable;

    public SAMLEntryVariableBuilder() {

        this.samlEntryRequestVariable = new SAMLEntryRequestVariable();
    }


    public Object translateHttpRequest(Object[] arguments, int variablesReference) {
            return new HttpServletRequestTranslator().translate(arguments[0],
                variablesReference);
    }

    public Object translateSAMLRequest(Object[] arguments, int variablesReference) {
        return new SAMLRequestTranslator().translate(arguments[0],
                variablesReference);
    }


    @Override
    public Argument<Map<String, Object>> build(Object[] arguments, int variablesReference) {

        this.samlEntryRequestVariable.setHttpServletRequest(translateHttpRequest(arguments,
                variablesReference + 1));
        this.samlEntryRequestVariable.setSAMLRequest(translateSAMLRequest(arguments,
                variablesReference + 1));
        return new Argument<Map<String, Object>>(samlEntryRequestVariable.getVariables());
    }


}
