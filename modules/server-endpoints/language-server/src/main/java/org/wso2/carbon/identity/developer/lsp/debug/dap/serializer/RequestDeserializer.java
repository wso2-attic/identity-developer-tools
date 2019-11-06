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
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.Argument;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.BreakpointRequest;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.EventRequest;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.ProtocolMessage;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.Request;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Deserialize the JSON RPC Request
 */
public class RequestDeserializer implements JsonDeserializer<ProtocolMessage> {

    private static final String LOCAL_NAME_SEQ = "seq";
    private static final String LOCAL_NAME_TYPE = "type";
    private static final String LOCAL_NAME_COMMAND = "command";
    private static final String LOCAL_NAME_ARGUMENTS = "arguments";
    private static final String LOCAL_NAME_MESSAGE = "message";
    private static final String LOCAL_NAME_EVENT = "event";
    private static final String LOCAL_NAME_PARAMS = "params";
    private static final String LOCAL_NAME_METHOD = "method";
    private static final String LOCAL_NAME_SETBREAKPOINT = "setBreakpoint";
    private static final String LOCAL_NAME_LINES = "lines";
    private static final String LOCAL_NAME_SOURCE = "source";
    private static final String LOCAL_NAME_NAME = "name";
    private static final String LOCAL_NAME_PATH = "path";
    private static final String LOCAL_NAME_LINE = "line";
    private static final String LOCAL_NAME_BREAKPOINTS = "breakpoints";
    private static final String LOCAL_NAME_SOURCEMODIFIED = "sourceModified";
    private static final String LOCAL_NAME_BODY = "body";
    private static final String LOCAL_NAME_UNKNOWN = "unknown";

    public ProtocolMessage deserialize(JsonElement jsonElement, Type type,
                               JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        JsonElement seqElement = jsonObject.get(LOCAL_NAME_SEQ);
        if (seqElement != null) {
            long seq = seqElement.getAsLong();
            return createEventRequest(jsonObject, seq);
        } else {
            return createMessageRequest(jsonObject);
        }
    }

    private Request createMessageRequest(JsonObject jsonObject) {

        JsonObject paramsObject = jsonObject.get(LOCAL_NAME_PARAMS).getAsJsonObject();
        String method = getAsString(jsonObject, LOCAL_NAME_METHOD);

        switch (method) {
            case LOCAL_NAME_SETBREAKPOINT:
                return constructSetBreakpointRequest(LOCAL_NAME_SETBREAKPOINT, paramsObject);
        }

        return new Request( LOCAL_NAME_UNKNOWN, 0, LOCAL_NAME_UNKNOWN, null);
    }

    private Request constructSetBreakpointRequest(String method, JsonObject paramsObject) {

        JsonElement linesElement = paramsObject.get(LOCAL_NAME_LINES);
        JsonObject sourceElement = (JsonObject) paramsObject.get(LOCAL_NAME_SOURCE);
        JsonElement nameElement = sourceElement.get(LOCAL_NAME_NAME);
        JsonElement pathElement = sourceElement.get(LOCAL_NAME_PATH);
        JsonElement breakpointsElements = paramsObject.get(LOCAL_NAME_BREAKPOINTS);
        JsonElement sourceModifiedElement = paramsObject.get(LOCAL_NAME_SOURCEMODIFIED);

        BreakpointRequest request = new BreakpointRequest(0, LOCAL_NAME_MESSAGE, method, null);

        JsonArray linesArray = linesElement.getAsJsonArray();
        int[] lines = new int[linesArray.size()];
        for (int i=0; i< lines.length; i++) {
            lines[i] = linesArray.get(i).getAsInt();
        }
        request.setLines(lines);

        request.setSourceName(nameElement.getAsString());
        request.setSourcePath(pathElement.getAsString());
        request.setSourceModified(sourceModifiedElement.getAsBoolean());

        request.setArguments(Collections.EMPTY_LIST);

        if (breakpointsElements.isJsonArray()) {
            JsonArray jsonArray = breakpointsElements.getAsJsonArray();
            int[] breakpointsArray = new int[jsonArray.size()];
            for (int i = 0; i < breakpointsArray.length; i++) {
                breakpointsArray[i] = jsonArray.get(i).getAsJsonObject().get(LOCAL_NAME_LINE).getAsInt();
            }
            request.setBreakpoints(breakpointsArray);
        }

        return request;
    }

    private ProtocolMessage createEventRequest(JsonObject jsonObject, long seqId) {

        JsonObject paramsObject = jsonObject.get(LOCAL_NAME_PARAMS).getAsJsonObject();

        String msgType = getAsString(paramsObject, LOCAL_NAME_TYPE);
        String command = getAsString(paramsObject, LOCAL_NAME_COMMAND);

        switch (msgType) {
            case LOCAL_NAME_EVENT:
                return constructEventRequest(seqId, paramsObject);
        }
        return new Request(msgType, seqId, command, null);
    }

    private ProtocolMessage constructEventRequest(long seq, JsonObject paramsObject) {

        String event = getAsString(paramsObject, LOCAL_NAME_EVENT);

        return new EventRequest(LOCAL_NAME_EVENT, event);
    }

    private String getAsString(JsonObject jsonObject, String key) {

        JsonElement jsonElement = jsonObject.get(key);
        if (jsonElement == null) {
            return null;
        }
        return jsonElement.getAsString();
    }
}
