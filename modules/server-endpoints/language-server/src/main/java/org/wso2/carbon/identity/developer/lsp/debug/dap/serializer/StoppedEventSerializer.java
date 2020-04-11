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
import org.wso2.carbon.identity.developer.lsp.debug.DAPConstants;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.StoppedEvent;

/**
 * Success StoppedEventSerializer Serializer.
 */
public class StoppedEventSerializer extends EventSerializer<StoppedEvent> {

    @Override
    protected JsonElement formatParams(StoppedEvent event) {
        JsonObject object = new JsonObject();
        object.addProperty(DAPConstants.JSON_KEY_FOR_LINE, event.getLine());
        object.addProperty(DAPConstants.JSON_KEY_FOR_SOURCE, event.getResourceName());
        return object;
    }
}
