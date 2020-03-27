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

package org.wso2.carbon.identity.developer.lsp.debug.runtime.translators;

import org.wso2.carbon.identity.base.IdentityException;
import org.wso2.carbon.identity.sso.saml.util.SAMLSSOUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * Translator to translate the  SAML Request arguments.
 */
public class SAMLRequestTranslator implements VariableTranslator {

    @Override
    public Object translate(Object object, int variablesReference) {

        if (object instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) object;
            if (httpServletRequest.getParameter("SAMLRequest") != null) {
                String samlRequest = httpServletRequest.getParameter("SAMLRequest");
                try {
                    return SAMLSSOUtil.decodeForPost(samlRequest);
                } catch (IdentityException e) {
                    return "Error when decoding the SAML Request.";
                }
            }
            return "NO SAML Request Added";
        }
        return "NO SAML Request Added";
    }
}
