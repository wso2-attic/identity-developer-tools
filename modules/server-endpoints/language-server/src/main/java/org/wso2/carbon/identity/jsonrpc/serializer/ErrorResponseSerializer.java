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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.wso2.carbon.identity.jsonrpc.ErrorResponse;
import org.wso2.carbon.identity.jsonrpc.SuccessResponse;

import java.lang.reflect.Type;

/**
 * Success Response Serializer
 */
public class ErrorResponseSerializer implements JsonSerializer<ErrorResponse> {

    public JsonElement serialize(ErrorResponse errorResponse, Type type,
                                 JsonSerializationContext jsonSerializationContext) {

        JsonObject object = new JsonObject();

        object.addProperty("jsonrpc", errorResponse.getJsonrpc());
        object.addProperty("id", errorResponse.getId());

        object.add("error", jsonSerializationContext.serialize(errorResponse.getJsonRpcError()));

        return object;
    }
}
