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

package org.wso2.carbon.identity.developer.lsp;

import org.wso2.carbon.identity.developer.lsp.language.sp.AuthenticationScriptProcessor;
import org.wso2.carbon.identity.jsonrpc.Request;

import java.util.HashMap;
import java.util.Map;

public class LanguageProcessorFactory {

    private Map<String, LanguageProcessor> languageProcessorMap = new HashMap<String, LanguageProcessor>();


    public void init() {
        languageProcessorMap.put("Application", new AuthenticationScriptProcessor())   ;
    }

    public LanguageProcessor getProcessor(Request request) {
        return languageProcessorMap.get("Application");
    }
}
