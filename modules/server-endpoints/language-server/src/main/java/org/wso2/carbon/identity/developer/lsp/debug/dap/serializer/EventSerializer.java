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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.wso2.carbon.identity.developer.lsp.debug.DAPConstants;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.Event;

import java.lang.reflect.Type;

/**
 * Serializes the DAP event to protocol payload.
 *
 * @param <T> generified version of class Class allows to pass Event type.
 */
public abstract class EventSerializer<T extends Event> implements JsonSerializer<T> {

    public JsonElement serialize(T event, Type type,
                                 JsonSerializationContext jsonSerializationContext) {

        JsonObject object = new JsonObject();
        object.addProperty(DAPConstants.JSON_KEY_FOR_JSONRPC, DAPConstants.JSON_RPC_VERSION);
        object.addProperty(DAPConstants.JSON_KEY_FOR_METHOD, event.getEvent());     //This is the type of event
        object.addProperty(DAPConstants.JSON_KEY_FOR_EVENT, event.getEvent());
        object.add(DAPConstants.JSON_KEY_FOR_PARAMS, formatParams(event));
        return object;
    }

    protected abstract JsonElement formatParams(T event);
}
