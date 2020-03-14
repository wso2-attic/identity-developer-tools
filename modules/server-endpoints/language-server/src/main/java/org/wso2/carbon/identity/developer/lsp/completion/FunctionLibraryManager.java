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
package org.wso2.carbon.identity.developer.lsp.completion;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.developer.lsp.endpoints.OSGIBindingConfigurator;
import org.wso2.carbon.identity.functions.library.mgt.FunctionLibraryManagementService;
import org.wso2.carbon.identity.functions.library.mgt.FunctionLibraryManagementServiceImpl;
import org.wso2.carbon.identity.functions.library.mgt.exception.FunctionLibraryManagementException;
import org.wso2.carbon.identity.functions.library.mgt.model.FunctionLibrary;

import java.util.HashMap;
import java.util.List;

/**
 * The function libraries management class.
 * Use to get the details of the function libraries.
 */
public class FunctionLibraryManager {

    private static Log log = LogFactory.getLog(OSGIBindingConfigurator.class);

    /**
     * Get functionlibraries.
     *
     * @return
     */
    public List<FunctionLibrary> getFunctionLibrary() {

        FunctionLibraryManagementService functionLibMgtService;
        functionLibMgtService = FunctionLibraryManagementServiceImpl.getInstance();
        List<FunctionLibrary> functionLibraries = null;
        try {
            functionLibraries = functionLibMgtService.listFunctionLibraries("carbon.super");
        } catch (FunctionLibraryManagementException e) {
            log.error("Error in get function libraries from framework ", e);
        }
        return functionLibraries;
    }

    /**
     * Get the function library name and the script.
     *
     * @return
     */
    public JsonArray getFuntionLibraryDetails() {

        HashMap<String, String> functionLibraryDetails = new HashMap<>();
        List<FunctionLibrary> functionLibraries = this.getFunctionLibrary();
        FunctionLibraryManagementService functionLibMgtService = null;
        functionLibMgtService = FunctionLibraryManagementServiceImpl.getInstance();
        String functionLibraryScript = null;
        for (int i = 0; i < functionLibraries.size(); i++) {
            try {
                FunctionLibrary functionLibrary = functionLibMgtService.getFunctionLibrary(
                        functionLibraries.get(i).getFunctionLibraryName(), "carbon.super");
                functionLibraryScript = functionLibrary.getFunctionLibraryScript();
                functionLibraryDetails.put(functionLibrary.getFunctionLibraryName(), functionLibraryScript);
            } catch (Exception error) {
                log.error("Error in get function libraries details ", error);
            }
        }
        return generateArray(functionLibraryDetails);
    }

    /**
     * Generate the jason array from the hash map.
     *
     * @param keywords
     * @return
     */
    private JsonArray generateArray(HashMap<String, String> keywords) {

        final String functionLibraryName = "name";
        final String functionLibraryCode = "code";
        JsonArray arr = new JsonArray();
        HashMap<String, JsonObject> map = new HashMap<String, JsonObject>();
        int i = 0;
        for (String key : keywords.keySet()) {
            JsonObject json = new JsonObject();
            json.addProperty(functionLibraryName, key);
            json.addProperty(functionLibraryCode, keywords.get(key));
            map.put("json", json);
            i += 1;
            arr.add(map.get("json"));
        }
        return arr;
    }
}
