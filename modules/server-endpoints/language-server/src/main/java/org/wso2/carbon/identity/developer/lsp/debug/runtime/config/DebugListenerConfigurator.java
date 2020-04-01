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

package org.wso2.carbon.identity.developer.lsp.debug.runtime.config;

import org.wso2.carbon.identity.developer.lsp.debug.runtime.DebugSessionManagerImpl;
import org.wso2.carbon.identity.java.agent.connect.InterceptionEngine;
import org.wso2.carbon.identity.java.agent.connect.MethodEntryInterceptionFilter;

/**
 * Configures the debug listeners for the current runtime.
 */
public class DebugListenerConfigurator {

    private final DebugSessionManagerImpl sessionManager;

    public DebugListenerConfigurator(
            DebugSessionManagerImpl sessionManager) {

        this.sessionManager = sessionManager;
    }

    public void configure(InterceptionEngine interceptionEngine) {

         MethodEntryInterceptionFilter samlEntryFilter = new MethodEntryInterceptionFilter(
                "org/wso2/carbon/identity/sso/saml/servlet/SAMLSSOProviderServlet",
                "doPost",
                "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;)V");


        MethodEntryInterceptionFilter samlExitFilter = new MethodEntryInterceptionFilter(
                "org/wso2/carbon/identity/sso/saml/servlet/SAMLSSOProviderServlet",
                "sendResponse",
                "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;Ljava/" +
                        "lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;" +
                        "Ljava/lang/String;)V");

        interceptionEngine.addListener(samlExitFilter, this.sessionManager);
        interceptionEngine.addListener(samlEntryFilter, this.sessionManager);

    }
}
