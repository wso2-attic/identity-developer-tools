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

package org.wso2.carbon.identity.developer.lsp.debug.dap.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.Argument;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.VariablesResponse;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;

/**
 * Serializes the variables response.
 */
public class VariablesResponseSerializer implements JsonSerializer<VariablesResponse> {

    @Override
    public JsonElement serialize(VariablesResponse response, Type type,
                                 JsonSerializationContext jsonSerializationContext) {

        JsonObject object = new JsonObject();

        object.addProperty("jsonrpc", "2.0");
        object.addProperty("id", response.getId());
        object.add("result", generateResultObject(response));


        return object;
    }

    private JsonElement generateResultObject(VariablesResponse response) {

        JsonObject object = new JsonObject();

        object.addProperty("command", response.getCommand());
        object.addProperty("message", response.getMessage());

        JsonObject body = new JsonObject();
        object.add("body", body);
        body.add("variables", generateVariablesArray(response));
        return object;
    }

    private JsonElement generateVariablesArray(VariablesResponse response) {

        JsonArray jsonArray = new JsonArray();
        Argument<HashMap<String, Object>> body = response.getBody();
        HashMap<String, Object> variables = body.getValue();

        if (variables == null) {
            return jsonArray;
        }

        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            JsonObject arrayElement = new JsonObject();
            arrayElement.addProperty("name", entry.getKey());
            JsonObject valueObject = new JsonObject();

            if (entry.getKey().equals("httpServletRequest")) {
                arrayElement.addProperty("type", "Object");
                HashMap<String, Object> requestdetails = (HashMap<String, Object>) entry.getValue();
                valueObject.add("cookies", this.getCookies(requestdetails));
                valueObject.add("headers", this.getHeaders(requestdetails));
                valueObject.addProperty("body", this.getBody(requestdetails));
                arrayElement.add("value", valueObject);
                arrayElement.addProperty("variablesReference", this.getVariablesReference(requestdetails));
            } else if (entry.getKey().equals("httpServletResponse")) {
                HashMap<String, Object> responsedetails = (HashMap<String, Object>) entry.getValue();
                arrayElement.add("value", new JsonObject());
                valueObject.add("headers", this.getHeaders(responsedetails));
                valueObject.addProperty("body", this.getBody(responsedetails));
                arrayElement.addProperty("variablesReference", this.getVariablesReference(responsedetails));
                arrayElement.addProperty("type", "Object");
            }
            arrayElement.addProperty("variablesReference", "0");
            jsonArray.add(arrayElement);
        }
        return jsonArray;
    }

    private JsonArray getCookies(HashMap<String, Object> requestdetails) {

        Object object = requestdetails.get("cookies");
        JsonArray cookieJsonArray = new JsonArray();
        if (object != null) {
            Cookie[] cookies = (Cookie[]) object;
            for (Cookie cookie : cookies) {
                JsonObject valueObject = new JsonObject();
                valueObject.addProperty("name", cookie.getName());
                valueObject.addProperty("value", cookie.getValue());
                valueObject.addProperty("version", cookie.getVersion());
                valueObject.addProperty("secure", cookie.getSecure());
                valueObject.addProperty("path", cookie.getPath());
                valueObject.addProperty("maxage", cookie.getMaxAge());
                valueObject.addProperty("domain", cookie.getDomain());
                cookieJsonArray.add(valueObject);
            }
        }
        return cookieJsonArray;
    }

    private JsonObject getHeaders(HashMap<String, Object> requestdetails) {

        HashMap<String, String> headers = (HashMap<String, String>) requestdetails.get("headers");
        JsonObject arrayElement = new JsonObject();
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                arrayElement.addProperty(header.getKey(), header.getValue());
            }
        }
        return arrayElement;
    }

    private String getBody(HashMap<String, Object> requestdetails) {

        return (String) requestdetails.get("body");
    }

    private int getVariablesReference(HashMap<String, Object> requestdetails) {

        return (int) requestdetails.get("variablesReference");
    }

}
