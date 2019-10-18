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

package org.wso2.carbon.identity.developer.lsp.language.sp;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.wso2.carbon.identity.application.authentication.framework.JsFunctionRegistry;
import org.wso2.carbon.identity.developer.lsp.LanguageException;
import org.wso2.carbon.identity.developer.lsp.LanguageProcessor;
import org.wso2.carbon.identity.developer.lsp.completion.CompletionListGenerator;
import org.wso2.carbon.identity.jsonrpc.Request;
import org.wso2.carbon.identity.jsonrpc.Response;
import org.wso2.carbon.identity.jsonrpc.SuccessResponse;
import org.wso2.carbon.identity.parser.ParserT;

import javax.script.ScriptException;

/**
 * Language processor for authentication script
 */
public class AuthenticationScriptProcessor implements LanguageProcessor {
    private JsFunctionRegistry jsFunctionRegistry;


    public Response process(Request request) throws LanguageException {

        SuccessResponse successResponse = new SuccessResponse();
        successResponse.setId(request.getId());

        if(request.getMethod().equals("onCompletion")) {
            int line = request.getParameterAsInt("line",
                    0);//Integer.parseInt(paramsElement.getAsJsonObject().get("line").getAsString());
            int charPosition = request.getParameterAsInt("character",
                    0);//Integer.parseInt(paramsElement.getAsJsonObject().get("character").getAsString());

            ParserT parserT = new ParserT();
            String text = request.getParameter(
                    "text");//parserT.generateParseTree(paramsElement.getAsJsonObject().get("text").getAsString(),line,charPosition);
            try {
                String scope = parserT.generateParseTree(text, line, charPosition);

                CompletionListGenerator completionListGenerator = new CompletionListGenerator();
                completionListGenerator.setJsFunctionRegistry(jsFunctionRegistry);
                successResponse.setResult(completionListGenerator.getList(scope));
            } catch (ScriptException e) {
                throw new LanguageException("Unable to parse the scope :" + text, e);
            }
        }
        return successResponse;
    }

    public void setJsFunctionRegistry(
            JsFunctionRegistry jsFunctionRegistry) {

        this.jsFunctionRegistry = jsFunctionRegistry;
    }
}
