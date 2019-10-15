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

package org.wso2.carbon.identity.jsonrpc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import org.wso2.carbon.identity.jsonrpc.serializer.ErrorResponseSerializer;
import org.wso2.carbon.identity.jsonrpc.serializer.JsonRpcParseException;
import org.wso2.carbon.identity.jsonrpc.serializer.JsonRpcSerializeException;
import org.wso2.carbon.identity.jsonrpc.serializer.RequestDeserializer;
import org.wso2.carbon.identity.jsonrpc.serializer.SuccessResponseSerializer;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Convenience class which handles the JSON RPC serialization and deserialization via
 * Google GSON library.
 * <p>
 * init() needs to be called before calling any serialization logic.
 */
public class JsonRPC {

    private Gson gson;
    private JsonParser jsonParser;

    /**
     * Init needs to be called before calling any serialization logic.
     */
    public void init() {

        gson = new GsonBuilder()
                .registerTypeAdapter(Request.class, new RequestDeserializer())
                .registerTypeAdapter(ErrorResponse.class, new ErrorResponseSerializer())
                .registerTypeAdapter(SuccessResponse.class, new SuccessResponseSerializer())
                .create();
    }

    /**
     * Decode the request from the json string
     *
     * @param json The JSON string
     * @return The decoded request
     * @throws JsonRpcParseException when ther is an error while parsing the request
     */
    public Request decode(String json) throws JsonRpcParseException {

        try {
            return gson.fromJson(json, Request.class);
        } catch (JsonParseException e) {
            throw new JsonRpcParseException(e);
        }
    }

    /**
     * Encodes the response to string format.
     *
     * @param response
     * @return
     */
    public String encode(Response response) throws JsonRpcSerializeException {

        JsonElement jsonElement = gson.toJsonTree(response);

        try {
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.setLenient(true);
            Streams.write(jsonElement, jsonWriter);
            return stringWriter.toString();
        } catch (IOException var3) {
            throw new JsonRpcSerializeException(var3);
        }
    }
}
