package org.wso2.identity.artifact.service.artifact.builder.spring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.identity.artifact.service.exception.ClientException;
import org.wso2.identity.artifact.service.exception.ServiceException;

import javax.ws.rs.core.HttpHeaders;

public class DCRClient {

    public static JSONObject getApplication(String spName) throws ClientException {

        JSONObject jsonData = null;
        try {

            HttpHeaders headers = ResteasyProviderFactory.getContextData(HttpHeaders.class);
            URL url = new URL("https://localhost:9443/api/identity/oauth2/dcr/v1.1/register?client_name=" + spName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", headers.getHeaderString("authorization"));

            if (conn.getResponseCode() != 200) {
                throw new ClientException("Obtained " + conn.getResponseCode() + "response from DCR endpoint");
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            JSONParser jsonParser = new JSONParser();
            jsonData = (JSONObject) jsonParser.parse(String.valueOf(result).trim());
            conn.disconnect();

        } catch (MalformedURLException e) {

            throw new ClientException("DCR endpoint that is used to get sp details is not valid");

        } catch (ParseException | IOException e) {
            throw new ClientException("DCR endpoint that is used to get sp details is not valid");
        }

        return jsonData;
    }
}
