package org.wso2.carbon.identity.developer.lsp.debug.dap.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.ContinueResponse;

import java.lang.reflect.Type;

/**
 * Success ContinueResponseSerializer Serializer.
 */
public class ContinueResponseSerializer extends ResponseSerializer<ContinueResponse> {

    public JsonElement serialize(ContinueResponse response, Type type,
                                 JsonSerializationContext jsonSerializationContext) {

        JsonObject object = (JsonObject) super.serialize(response, type, jsonSerializationContext);
        if (response.getAllThreadsContinued() != null) {
            object.addProperty("allThreadsContinued", response.getAllThreadsContinued());
        }
        return object;
    }

}
