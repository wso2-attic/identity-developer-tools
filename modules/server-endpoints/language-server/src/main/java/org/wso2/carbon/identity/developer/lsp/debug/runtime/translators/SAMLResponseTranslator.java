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

import java.util.Base64;

/**
 * Translator to translate the  SAML Response arguments.
 */
public class SAMLResponseTranslator implements VariableTranslator {

    private static Log log = LogFactory.getLog(SAMLResponseTranslator.class);

    private SAMLResponseTranslator() {

    }

    private static class SAMLResponseTranslatorHolder {

        private static final SAMLResponseTranslator INSTANCE = new SAMLResponseTranslator();
    }

    /**
     * This static method allow to get the instance of the SAMLResponseTranslator.
     *
     * @return the SAMLResponseTranslatorHolder instance.
     */
    public static SAMLResponseTranslator getInstance() {

        return SAMLResponseTranslatorHolder.INSTANCE;
    }

    @Override
    public Object translate(Object object, int variablesReference) {

        if (object != null) {
            try {
                return new String(Base64.getDecoder().decode((String) object));
            } catch (IllegalArgumentException e) {
                log.error("Error when decoding the SAML Response.", e);
                return object;
            }
        }
        return object;
    }
}
