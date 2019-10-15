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

package org.wso2.carbon.identity.jsonrpc.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.wso2.carbon.identity.jsonrpc.Parameter;
import org.wso2.carbon.identity.jsonrpc.ParametersList;
import org.wso2.carbon.identity.jsonrpc.Request;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Deserialize the JSON RPC Request
 */
public class RequestDeserializer implements JsonDeserializer<Request> {

    private static final String LOCAL_NAME_JSON_RPC = "jsonrpc";
    private static final String LOCAL_NAME_PARAMS = "params";
    private static final String LOCAL_NAME_METHOD = "method";
    private static final String LOCAL_NAME_ID = "id";
    private static final String JSON_RPC_VERSION_20 = "2.0";

    public Request deserialize(JsonElement jsonElement, Type type,
                               JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        if (!jsonObject.has(LOCAL_NAME_JSON_RPC) || !jsonObject.get(LOCAL_NAME_JSON_RPC).getAsString().equals(
                JSON_RPC_VERSION_20)) {
            throw new JsonParseException("Invalid JSON-RPC request, expected version 2.0, but was, " + jsonObject.get(
                    LOCAL_NAME_JSON_RPC).getAsString());
        }

        List<Parameter> params = new ArrayList<Parameter>();

        if (jsonObject.has(LOCAL_NAME_PARAMS)) {
            JsonElement paramsElement = jsonObject.get(LOCAL_NAME_PARAMS);
            if (paramsElement.isJsonArray()) {
                JsonArray paramArray = paramsElement.getAsJsonArray();

                for (int i = 0; i < paramArray.size(); i++) {
                    JsonElement element0 = paramArray.get(i);
                    params.add(new Parameter(element0.getAsString(), element0.getAsString()));
                }
            } else if (paramsElement.isJsonObject()) {
                JsonObject paramObj = paramsElement.getAsJsonObject();

                for (Map.Entry<String, JsonElement> entry : paramObj.entrySet()) {
                    JsonElement element0 = entry.getValue();
                    params.add(new Parameter(entry.getKey(), element0.getAsString()));
                }
            }
        }

        ParametersList list = new ParametersList(params);

        JsonElement idElement = jsonObject.get(LOCAL_NAME_ID);
        String id = idElement == null? UUID.randomUUID().toString() : idElement.getAsString();

        return new Request(id, jsonObject.get(LOCAL_NAME_METHOD).getAsString(),
                list);
    }
}
