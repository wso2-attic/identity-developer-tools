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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.base.IdentityException;
import org.wso2.carbon.identity.developer.lsp.debug.DAPConstants;
import org.wso2.carbon.identity.sso.saml.util.SAMLSSOUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * Translator to translate the  SAML Request arguments.
 */
public class SAMLRequestTranslator implements VariableTranslator {

    private static Log log = LogFactory.getLog(SAMLRequestTranslator.class);

    private SAMLRequestTranslator() {}

    private static class SAMLRequestTranslatorHolder {
        private static final SAMLRequestTranslator INSTANCE = new SAMLRequestTranslator();
    }

    /**
     * This static method allow to get the instance of the SAMLRequestTranslator.
     *
     * @return
     */
    public static SAMLRequestTranslator getInstance() {
        return SAMLRequestTranslatorHolder.INSTANCE;
    }

    @Override
    public Object translate(Object object, int variablesReference) {

        if (object instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) object;
            if (httpServletRequest.getParameter(DAPConstants.SAML_REQUEST) != null) {
                String samlRequest = httpServletRequest.getParameter(DAPConstants.SAML_REQUEST);
                try {
                    return SAMLSSOUtil.decodeForPost(samlRequest);
                } catch (IdentityException e) {
                    log.error("Error when decoding the SAML Request.", e);
                    return "Error when decoding the SAML Request.";
                }
            }
            return "NO SAML Request Added";
        }
        return "NO SAML Request Added";
    }
}
