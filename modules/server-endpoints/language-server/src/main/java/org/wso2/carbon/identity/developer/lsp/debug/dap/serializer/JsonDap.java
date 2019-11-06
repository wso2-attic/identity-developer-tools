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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.BreakpointRequest;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.Event;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.ProtocolMessage;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.Request;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.Response;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.StoppedEvent;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Convenience class which handles the JSON RPC serialization and deserialization via
 * Google GSON library.
 * <p>
 * init() needs to be called before calling any serialization logic.
 */
public class JsonDap {

    private Gson gson;
    private JsonParser jsonParser;

    /**
     * Init needs to be called before calling any serialization logic.
     */
    public void init() {

        gson = new GsonBuilder()
                .registerTypeAdapter(Request.class, new RequestDeserializer())
                .registerTypeAdapter(Response.class, new ResponseSerializer())
                .registerTypeAdapter(StoppedEvent.class, new StoppedEventSerializer())
                .create();
    }

    /**
     * Decode the request from the json string
     *
     * @param json The JSON string
     * @return The decoded request
     * @throws JsonParseException when ther is an error while parsing the request
     */
    public Request decode(String json) throws JsonParseException {

        try {
            return gson.fromJson(json, Request.class);
        } catch (JsonParseException e) {
            throw new JsonParseException(e);
        }
    }

    /**
     * Encodes the response to string format.
     *
     * @param response
     * @return
     */
    public String encode(ProtocolMessage response) throws JsonDapSerializeException {

        JsonElement jsonElement = gson.toJsonTree(response);

        try {
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.setLenient(true);
            Streams.write(jsonElement, jsonWriter);
            return stringWriter.toString();
        } catch (IOException var3) {
            throw new JsonDapSerializeException(var3);
        }
    }
}
