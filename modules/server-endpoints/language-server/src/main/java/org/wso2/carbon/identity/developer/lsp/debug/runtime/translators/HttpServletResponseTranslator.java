/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.identity.developer.lsp.debug.runtime.translators;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;

/**
 * Translator to translate the  Http Servlet Response arguments.
 */
public class HttpServletResponseTranslator implements VariableTranslator {

    @Override
    public Object translate(Object object, int variablesReference) {

        HashMap<String, Object> responsedetails = new HashMap<>();
        if (object != null && object instanceof HttpServletRequest) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) object;
            responsedetails.put("status", httpServletResponse.getStatus());
            responsedetails.put("headers", this.getResponseHeaders(httpServletResponse));
            responsedetails.put("variablesReference", variablesReference);
        }
        return responsedetails;

    }

    private HashMap<String, String> getResponseHeaders(HttpServletResponse response) {

        HashMap<String, String> headerdetails = new HashMap<>();
        for (String nextHeaderName : response.getHeaderNames()) {
            String headerValue = response.getHeader(nextHeaderName);
            headerdetails.put(nextHeaderName, headerValue);
        }
        return headerdetails;
    }
}
