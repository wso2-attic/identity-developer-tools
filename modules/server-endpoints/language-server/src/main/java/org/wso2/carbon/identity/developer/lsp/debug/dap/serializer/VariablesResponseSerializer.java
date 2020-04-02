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

import org.wso2.carbon.identity.developer.lsp.debug.DAPConstants;
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
        object.addProperty(DAPConstants.JSON_KEY_FOR_JSONRPC, DAPConstants.JSONRPC_VERSION);
        object.addProperty(DAPConstants.JSON_KEY_FOR_ID, response.getId());
        object.add(DAPConstants.JSON_KEY_FOR_RESULT, generateResultObject(response));


        return object;
    }

    private JsonElement generateResultObject(VariablesResponse response) {

        JsonObject object = new JsonObject();
        object.addProperty(DAPConstants.JSON_KEY_FOR_COMMAND, response.getCommand());
        object.addProperty(DAPConstants.JSON_KEY_FOR_MESSAGE, response.getMessage());

        JsonObject body = new JsonObject();
        object.add(DAPConstants.JSON_KEY_FOR_BODY, body);
        body.add(DAPConstants.JSON_KEY_FOR_VARIABLES, generateVariablesArray(response));
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
            arrayElement.addProperty(DAPConstants.JSON_KEY_FOR_NAME, entry.getKey());
            JsonObject valueObject = new JsonObject();

            switch (entry.getKey()) {
                case DAPConstants.HTTP_SERVLET_REQUEST:
                    arrayElement.addProperty(DAPConstants.JSON_KEY_FOR_TYPE, DAPConstants.VARAIBLE_TYPE_OBJECT);
                    HashMap<String, Object> requestdetails = (HashMap<String, Object>) entry.getValue();
                    valueObject.add(DAPConstants.JSON_KEY_FOR_COOKIES, this.getCookies(requestdetails));
                    valueObject.add(DAPConstants.JSON_KEY_FOR_HEADERS, this.getHeaders(requestdetails));
                    arrayElement.add(DAPConstants.JSON_KEY_FOR_VALUE, valueObject);
                    arrayElement.addProperty(DAPConstants.JSON_KEY_FOR_VARIABLE_REFERENCE, 0);

                    break;
                case DAPConstants.HTTP_SERVLET_RESPONSE:
                    arrayElement.addProperty(DAPConstants.JSON_KEY_FOR_TYPE, DAPConstants.VARAIBLE_TYPE_OBJECT);
                    HashMap<String, Object> responsedetails = (HashMap<String, Object>) entry.getValue();
                    valueObject.add(DAPConstants.JSON_KEY_FOR_HEADERS, this.getHeaders(responsedetails));
                    valueObject.addProperty(DAPConstants.JSON_KEY_FOR_STATUS, this.getResponseStatus(responsedetails));
                    arrayElement.add(DAPConstants.JSON_KEY_FOR_VALUE, valueObject);
                    arrayElement.addProperty(DAPConstants.JSON_KEY_FOR_VARIABLE_REFERENCE, 0);

                    break;
                case DAPConstants.SAML_REQUEST:
                case DAPConstants.SAML_RESPONSE:
                    arrayElement.addProperty(DAPConstants.JSON_KEY_FOR_TYPE, DAPConstants.VARAIBLE_TYPE_STRING);
                    arrayElement.addProperty(DAPConstants.JSON_KEY_FOR_VALUE, (String) entry.getValue());
                    arrayElement.addProperty(DAPConstants.JSON_KEY_FOR_VARIABLE_REFERENCE, 0);

                    break;
                default:
                    arrayElement.addProperty(DAPConstants.JSON_KEY_FOR_TYPE, DAPConstants.VARAIBLE_TYPE_UNKNOWN);
                    arrayElement.addProperty(DAPConstants.JSON_KEY_FOR_VARIABLE_REFERENCE, 0);
                    break;
            }
            jsonArray.add(arrayElement);
        }
        return jsonArray;
    }

    private Integer getResponseStatus(HashMap<String, Object> responsedetails) {

        return (Integer) responsedetails.get(DAPConstants.JSON_KEY_FOR_STATUS);
    }

    private JsonArray getCookies(HashMap<String, Object> requestdetails) {

        Object object = requestdetails.get(DAPConstants.JSON_KEY_FOR_COOKIES);
        JsonArray cookieJsonArray = new JsonArray();
        if (object != null) {
            Cookie[] cookies = (Cookie[]) object;
            for (Cookie cookie : cookies) {
                JsonObject valueObject = new JsonObject();
                valueObject.addProperty(DAPConstants.JSON_KEY_FOR_NAME, cookie.getName());
                valueObject.addProperty(DAPConstants.JSON_KEY_FOR_VALUE, cookie.getValue());
                valueObject.addProperty(DAPConstants.JSON_KEY_FOR_VERSION, cookie.getVersion());
                valueObject.addProperty(DAPConstants.JSON_KEY_FOR_SECURE, cookie.getSecure());
                valueObject.addProperty(DAPConstants.JSON_KEY_FOR_PATH, cookie.getPath());
                valueObject.addProperty(DAPConstants.JSON_KEY_FOR_MAXAGE, cookie.getMaxAge());
                valueObject.addProperty(DAPConstants.JSON_KEY_FOR_DOMAIN, cookie.getDomain());
                cookieJsonArray.add(valueObject);
            }
        }
        return cookieJsonArray;
    }

    private JsonObject getHeaders(HashMap<String, Object> requestdetails) {

        HashMap<String, String> headers =
                (HashMap<String, String>) requestdetails.get(DAPConstants.JSON_KEY_FOR_HEADERS);
        JsonObject arrayElement = new JsonObject();
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                arrayElement.addProperty(header.getKey(), header.getValue());
            }
        }
        return arrayElement;
    }

    private int getVariablesReference(HashMap<String, Object> requestdetails) {

        return (int) requestdetails.get(DAPConstants.JSON_KEY_FOR_VARIABLE_REFERENCE);
    }

}
