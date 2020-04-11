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
 * This class is to add the Static Config constants.
 */
export class Config {

    public static readonly WEBSOCKET_END_POINT = "wss://localhost:9443/lsp/debug";
    public static readonly LANGUAGE_CLIENT_ID = "wso2isLanguageServer";
    public static readonly LANGUAGE_CLIENT_NAME = "Language Server for WSO2 IS";
    public static readonly PATH_OAUTH = "/oauth";
    public static readonly VSCODE_SP_REDIRECT_URL = "http://localhost:8010/oauth";
    public static readonly SUCCESS_HTML_NAME = "success.html";
    public static readonly DIAGRAM_HTML_NAME = "diagram.html";
    public static readonly AUTHENTICATION_HTML_NAME = "oAuth.html";
    public static readonly PATH_APPLICATION_IMPORT: (iamUrl, tenant) => string =
        (iamUrl, tenant) => `${iamUrl}/t/${tenant}/api/server/v1/applications/import`;

    public static readonly PATH_GET_AUTH_CODE: (iamUrl, clientID, redirectUri, scope) => string =
        (iamUrl, clientID, redirectUri, scope) => `${iamUrl}/oauth2/authorize?response_type=code&` +
            `redirect_uri=${redirectUri}&client_id=${clientID}&scope="${scope}`;

    public static readonly PATH_GET_SCRIPT_LIBRARY_BY_NAME: (iamUrl, tenant, scriptLibraryName) => string =
        (iamUrl, tenant, scriptLibraryName) => `${iamUrl}/t/${tenant}/api/server/v1/` +
            `script-libraries/${scriptLibraryName}/content`;
    public static readonly PATH_GET_ALL_SCRIPT_LIBRARY: (iamUrl, tenant) => string =
        (iamUrl, tenant) => `${iamUrl}/t/${tenant}/api/server/v1/` +
            `script-libraries`;
    public static readonly PATH_APPLICATION_EXPORT: (iamUrl, tenant, serviceID) => string =
        (iamUrl, tenant, serviceID) => `${iamUrl}/t/${tenant}/api/server/v1/applications/${serviceID}/export`;
    public static readonly PATH_AUTHORISE: (iamUrl, requestToken, redirectUrl) => string =
        (iamUrl, requestToken, redirectUrl) => `${iamUrl}/oauth2/token?grant_type=authorization_code&` +
            `code=${requestToken}&redirect_uri=${redirectUrl}`;
    public static readonly PATH_APPLICATIONS: (iamUrl, tenant) => string =
        (iamUrl, tenant) => `${iamUrl}/t/${tenant}/api/server/v1/applications`;
}
