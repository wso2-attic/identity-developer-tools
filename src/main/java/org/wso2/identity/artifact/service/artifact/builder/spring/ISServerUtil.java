package org.wso2.identity.artifact.service.artifact.builder.spring;

import org.json.simple.JSONObject;
import org.wso2.identity.artifact.service.endpoint.CLIInput;
import org.wso2.identity.artifact.service.exception.ClientException;

import java.util.HashMap;
import java.util.Map;

public class ISServerUtil {

    public Map<String, String> getOAuthProperties(CLIInput cliInput) throws ClientException {

        String serverUrl = cliInput.getServer();
        JSONObject clientProperties = DCRClient.getApplication(cliInput.getSp());
        String clientid = "null";
        String clientsecret = "null";

        if (clientProperties != null) {
            clientid = (String) clientProperties.get("client_id");
            clientsecret = (String) clientProperties.get("client_secret");
        }

        String finalClientid = clientid;
        String finalClientsecret = clientsecret;
        return new HashMap<String, String>() {{
            put("client_name", "WSO2 Identity Server");
            put("client_id", finalClientid);
            put("client_secret", finalClientsecret);
            put("authorization_url", serverUrl + "/oauth2/authorize");
            put("token_url", serverUrl + "/oauth2/token");
            put("user_info_url", serverUrl + "/oauth2/userinfo");
            put("jwks_url", serverUrl + "/oauth2/jwks");
        }};
    }
}
