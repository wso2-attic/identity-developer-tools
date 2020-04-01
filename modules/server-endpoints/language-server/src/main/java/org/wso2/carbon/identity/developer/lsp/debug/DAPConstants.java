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

package org.wso2.carbon.identity.developer.lsp.debug;

/**
 * Constants Describes about the Variables.
 */
public class DAPConstants {
    public static final String HTTP_SERVLET_REQUEST = "HttpServletRequest";
    public static final String HTTP_SERVLET_RESPONSE = "HttpServletResponse";
    public static final String SAML_REQUEST = "SAMLRequest";
    public static final String SAML_RESPONSE = "SAMLResponse";
    public static final String SAML_ENTRY_CLASS = "org/wso2/carbon/identity/sso/saml/servlet/SAMLSSOProviderServlet";
    public static final String SAML_ENTRY_METHOD = "doPost";
    public static final String SAML_ENTRY_SIGNATURE = "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet" +
            "/http/HttpServletResponse;)V";
    public static final String SAML_EXIT_CLASS = "org/wso2/carbon/identity/sso/saml/servlet/SAMLSSOProviderServlet";
    public static final String SAML_EXIT_METHOD = "sendResponse";
    public static final String SAML_EXIT_SIGNATURE = "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/" +
            "HttpServletResponse;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;" +
            "Ljava/lang/String;Ljava/lang/String;)V";
}
