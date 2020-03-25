package org.wso2.identity.artifact.service.artifact.builder.spring;

import java.util.HashMap;
import java.util.Map;

public class ISServerUtil {

    public Map<String, String> getOAuthProperties() {

        return new HashMap<String, String>() {{
            put("client_name", "WSO2 Identity Server");
            put("client_id", "CLYPeMKNEpkEKtp22uZ4FMoZfxoa");
            put("client_secret", "nrtoV8EBD67OmNtTvcroGTfYWusa");
            put("authorization_url", "https://localhost:9443/oauth2/authorize");
            put("token_url", "https://localhost:9443/oauth2/token");
            put("user_info_url", "https://localhost:9443/oauth2/userinfo");
            put("jwks_url", "https://localhost:9443/oauth2/jwks");
        }};
    }
}
