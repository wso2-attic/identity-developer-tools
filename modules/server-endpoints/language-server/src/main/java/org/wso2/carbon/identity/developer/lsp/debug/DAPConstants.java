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


    public static final String JSON_KEY_FOR_COOKIES = "cookies";
    public static final String JSON_KEY_FOR_HEADERS = "headers";
    public static final String JSON_KEY_FOR_BODY = "body";
    public static final String JSON_KEY_FOR_NAME = "name";
    public static final String JSON_KEY_FOR_RESULT = "result";
    public static final String JSON_KEY_FOR_PATH = "path";
    public static final String JSON_KEY_FOR_MAXAGE = "maxage";
    public static final String JSON_KEY_FOR_DOMAIN = "domain";
    public static final String JSON_KEY_FOR_STATUS = "status";
    public static final String JSON_KEY_FOR_MESSAGE = "message";
    public static final String JSON_KEY_FOR_VARIABLES = "variables";
    public static final String JSON_KEY_FOR_VARIABLE_REFERENCE = "variablesReference";
    public static final String JSON_KEY_FOR_VERSION = "version";
    public static final String JSON_KEY_FOR_SECURE = "secure";
    public static final String JSON_KEY_FOR_VALUE = "value";
    public static final String JSON_KEY_FOR_TYPE = "type";
    public static final String JSON_KEY_FOR_ID = "id";
    public static final String JSON_KEY_FOR_JSONRPC = "jsonrpc";
    public static final String JSON_KEY_FOR_COMMAND = "command";

    public static final String VARAIBLE_TYPE_STRING = "String";
    public static final String VARAIBLE_TYPE_OBJECT = "Object";
    public static final String VARAIBLE_TYPE_UNKNOWN = "Unknown";

    public static final String JSONRPC_VERSION = "2.0";



}
