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

/**
 * This class is to add the Static Debug related constants.
 */
export class DebugConstants {

    public static readonly HTTP_SERVLET_REQUEST = "HttpServletRequest";
    public static readonly HTTP_SERVLET_RESPONSE = "HttpServletResponse";
    public static readonly SAML_REQUEST = "SAMLRequest";
    public static readonly SAML_RESPONSE = "SAMLResponse";
    public static readonly SAML_REQUEST_HTML = `{SAML_REQUEST}`;
    public static readonly SAML_RESPONSE_HTML = `{SAML_RESPONSE}`;
    public static readonly LOCAL_AND_OUTBOUND_AUTHENTICATION_CONFIG = `LocalAndOutBoundAuthenticationConfig`;
    public static readonly AUTHENTICATION_SCRIPTS = `AuthenticationScript`;
    public static readonly SCRIPT_FILE = `scriptFile`;
    public static readonly ADAPTIVE_SCRIPT = `adaptiveScript`;
    public static readonly DEFAULT_SCRIPT_FILE = `defaultScriptFile`;
    public static readonly AUTHENTICATION_SCRIPT_TRUE = `AuthenticationScript enabled="true"`;
    public static readonly AUTHENTICATION_SCRIPT_FALSE = `AuthenticationScript enabled="false"`;
    public static readonly IAM_URL = "IAM.URL";
    public static readonly IAM_SERVICE_CLIENT_ID = "IAM.ServiceClientID";
    public static readonly IAM_TENANT = "IAM.Tenant";
    public static readonly CLIENT_SECRET = "clientSecret";
    public static readonly ACCESS_TOKEN = "accessToken";
    public static readonly STRING_ENCODING = "base64";
    public static readonly DEBUG_STOP_ON_ENTRY = "stopOnEntry";
    public static readonly DEBUG_END = "end";
    public static readonly DEBUG_CLEAR_BREAKPOINT = "clearBreakpoints";
    public static readonly DEBUG_STOP_ON_BREAKPOINT = "stopOnBreakpoint";
    public static readonly DEBUG_SET_BREAKPOINT = "setBreakpoint";
    public static readonly DEBUG_BREAKPOINT_VALIDATED = "breakpointValidated";
    public static readonly DEBUG_CONTINUE = "continue";
    public static readonly DEBUG_STOP_ON_STEP = "stopOnStep";
    public static readonly DEBUG_STOP_ON_DATA_BREAKPOINT = "stopOnDataBreakpoint";
    public static readonly DEBUG_EXCEPTION = "exception";
    public static readonly DEBUG_STOP_ON_EXCEPTION = "stopOnException";
    public static readonly MESSAGE_FILE_SAVED_SUCCESS = "The file has been saved!";
    public static readonly MESSAGE_SERVICE_IMPORT_SUCCESS = "Service has been Successfully imported.";
    public static readonly MESSAGE_SERVICE_IMPORT_ERROR = "Error Populating the Services.";
    public static readonly MESSAGE_SELECT_SERVICE_INFO = "You do not select any service.";
    public static readonly MESSAGE_CONFIGURATION_SUCCESS = "Successfully Configured your extension.";
    public static readonly MESSAGE_SERVICE_FETCHED_SUCCESS = "Successfully retrieve the services.";
    public static readonly MESSAGE_ACCESS_TOKEN_EXPIRED = "Access Token has expired.";
    public static readonly MESSAGE_SCRIPTS_IMPORT_SUCCESS = "Successfully retrieved the script libraries.";
}
