/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 *  Dissemination of any information or reproduction of any material
 * contained herein in any form is strictly forbidden, unless
 * permitted by WSO2 expressly. You may not alter or remove any
 * copyright or other notice from copies of this content.
 */

package org.wso2.identity.artifact.service.artifact.builder.spring;

import org.json.simple.JSONObject;
import org.wso2.identity.artifact.service.model.ArtifactRequestData;
import org.wso2.identity.artifact.service.exception.BuilderException;

import java.util.HashMap;
import java.util.Map;

public class ISServerUtil {

    public Map<String, String> getOAuthProperties(ArtifactRequestData artifactRequestData) throws BuilderException {

        String serverUrl = artifactRequestData.getServer();
        JSONObject clientProperties = null;
        clientProperties = DCRClient.getApplication(artifactRequestData.getServer(), artifactRequestData.getApplication());

        if (clientProperties != null) {
            String clientId = (String) clientProperties.get("client_id");
            String clientSecret = (String) clientProperties.get("client_secret");

            return new HashMap<String, String>() {{
                put("client_name", "WSO2 Identity Server");
                put("client_id", clientId);
                put("client_secret", clientSecret);
                put("authorization_url", serverUrl + "/oauth2/authorize");
                put("token_url", serverUrl + "/oauth2/token");
                put("user_info_url", serverUrl + "/oauth2/userinfo");
                put("jwks_url", serverUrl + "/oauth2/jwks");
            }};
        }
        return null;
    }
}
