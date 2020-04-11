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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.developer.lsp.debug.DAPConstants;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.Argument;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.BreakpointRequest;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.ContinueRequest;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.EventRequest;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.ProtocolMessage;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.Request;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.UnknownRequest;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.VariablesRequest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Deserialize the JSON RPC Request.
 */
public class RequestDeserializer implements JsonDeserializer<ProtocolMessage> {

    private static final Log log = LogFactory.getLog(RequestDeserializer.class);
    
    public ProtocolMessage deserialize(JsonElement jsonElement, Type type,
                                       JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        JsonElement seqElement = jsonObject.get(DAPConstants.JSON_KEY_FOR_SEQ);
        if (seqElement != null) {
            long seq = seqElement.getAsLong();
            return createEventRequest(jsonObject, seq);
        } else {
            return createMessageRequest(jsonObject);
        }
    }

    private Request createMessageRequest(JsonObject jsonObject) {

        String method = getAsString(jsonObject, DAPConstants.JSON_KEY_FOR_METHOD);

        JsonElement idElement = jsonObject.get(DAPConstants.JSON_KEY_FOR_ID);
        long id = idElement != null ? idElement.getAsLong() : 0;

        if (method != null) {
            switch (method) {
                case DAPConstants.JSON_KEY_FOR_CONTINUE:
                    return constructContinueRequest(DAPConstants.JSON_KEY_FOR_CONTINUE, id, jsonObject);
                case DAPConstants.JSON_KEY_FOR_SET_BREAKPOINT:
                    return constructSetBreakpointRequest(DAPConstants.JSON_KEY_FOR_SET_BREAKPOINT, jsonObject);
                case DAPConstants.JSON_KEY_FOR_VARIABLES:
                    return constructVariablesRequest(DAPConstants.JSON_KEY_FOR_SET_BREAKPOINT, id, jsonObject);
            }
        }

        return new UnknownRequest(DAPConstants.JSON_KEY_FOR_UNKNOWN, id, method, null);
    }

    private Request constructContinueRequest(String method, long id, JsonObject jsonObject) {

        return new ContinueRequest(DAPConstants.JSON_KEY_FOR_MESSAGE, id, method, null);
    }

    private VariablesRequest constructVariablesRequest(String method, long id, JsonObject jsonObject) {

        JsonElement paramElement = jsonObject.get(DAPConstants.JSON_KEY_FOR_PARAMS);
        if (paramElement == null) {
            log.error("Set breakpoint request received without params");
            return (VariablesRequest) (Request) new UnknownRequest(DAPConstants.JSON_KEY_FOR_UNKNOWN, 0, method, null);
        }
        // get the arguments from the request
        JsonObject paramsObject = paramElement.getAsJsonObject();
        JsonElement variablesReference = paramsObject.get(DAPConstants.JSON_KEY_FOR_VARIABLE_REFERENCE);
        Argument argument = new Argument(variablesReference);
        List<Argument> arguments = new ArrayList<>();
        arguments.add(argument);
        return new VariablesRequest(DAPConstants.JSON_KEY_FOR_MESSAGE, id,
                DAPConstants.JSON_KEY_FOR_VARIABLES, arguments);
    }

    private Request constructSetBreakpointRequest(String method, JsonObject jsonObject) {

        JsonElement paramElement = jsonObject.get(DAPConstants.JSON_KEY_FOR_PARAMS);
        if (paramElement == null) {
            log.error("Set breakpoint request received without params");
            return new UnknownRequest(DAPConstants.JSON_KEY_FOR_UNKNOWN, 0, method, null);
        }
        JsonObject paramsObject = paramElement.getAsJsonObject();

        JsonElement linesElement = paramsObject.get(DAPConstants.JSON_KEY_FOR_LINES);
        JsonObject sourceElement = (JsonObject) paramsObject.get(DAPConstants.JSON_KEY_FOR_SOURCE);
        JsonElement nameElement = sourceElement.get(DAPConstants.JSON_KEY_FOR_NAME);
        JsonElement pathElement = sourceElement.get(DAPConstants.JSON_KEY_FOR_PATH);
        JsonElement breakpointsElements = paramsObject.get(DAPConstants.JSON_KEY_FOR_BREAKPOINTS);
        JsonElement sourceModifiedElement = paramsObject.get(DAPConstants.JSON_KEY_FOR_SOURCE_MODIFIED);

        BreakpointRequest request = new BreakpointRequest(0, DAPConstants.JSON_KEY_FOR_MESSAGE, method, null);

        JsonArray linesArray = linesElement.getAsJsonArray();
        int[] lines = new int[linesArray.size()];
        for (int i = 0; i < lines.length; i++) {
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
                breakpointsArray[i] = jsonArray.get(i).getAsJsonObject().get(DAPConstants.JSON_KEY_FOR_LINE).getAsInt();
            }
            request.setBreakpoints(breakpointsArray);
        }

        return request;
    }

    private ProtocolMessage createEventRequest(JsonObject jsonObject, long seqId) {

        JsonObject paramsObject = jsonObject.get(DAPConstants.JSON_KEY_FOR_PARAMS).getAsJsonObject();

        String msgType = getAsString(paramsObject, DAPConstants.JSON_KEY_FOR_TYPE);
        String command = getAsString(paramsObject, DAPConstants.JSON_KEY_FOR_COMMAND);

        switch (msgType) {
            case DAPConstants.JSON_KEY_FOR_EVENT:
                return constructEventRequest(seqId, paramsObject);
        }
        return new Request(msgType, seqId, command, null);
    }

    private ProtocolMessage constructEventRequest(long seq, JsonObject paramsObject) {

        String event = getAsString(paramsObject, DAPConstants.JSON_KEY_FOR_EVENT);

        return new EventRequest(DAPConstants.JSON_KEY_FOR_EVENT, event);
    }

    private String getAsString(JsonObject jsonObject, String key) {

        JsonElement jsonElement = jsonObject.get(key);
        if (jsonElement == null) {
            return null;
        }
        return jsonElement.getAsString();
    }
}
