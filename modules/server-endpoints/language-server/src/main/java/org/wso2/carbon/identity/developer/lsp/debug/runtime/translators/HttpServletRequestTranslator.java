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

import org.wso2.carbon.identity.developer.lsp.debug.DAPConstants;

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

/**
 * Translator to translate the Http Servlet Request arguments.
 */
public class HttpServletRequestTranslator implements VariableTranslator {

    private HttpServletRequestTranslator() {

    }

    private static class HttpServletRequestTranslatorHolder {

        private static final HttpServletRequestTranslator INSTANCE = new HttpServletRequestTranslator();
    }

    /**
     * This static method allow to get the instance of the HttpServletRequestTranslator.
     *
     * @return the HttpServletRequestTranslatorHolder instance.
     */
    public static HttpServletRequestTranslator getInstance() {

        return HttpServletRequestTranslatorHolder.INSTANCE;
    }

    @Override
    public Object translate(Object object, int variablesReference) {

        HashMap<String, Object> requestdetails = new HashMap<>();
        if (object != null && object instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) object;
            requestdetails.put(DAPConstants.JSON_KEY_FOR_COOKIES, httpServletRequest.getCookies());
            requestdetails.put(DAPConstants.JSON_KEY_FOR_HEADERS, this.getRequestHeaders(httpServletRequest));
            requestdetails.put(DAPConstants.JSON_KEY_FOR_VARIABLE_REFERENCE, variablesReference);

        }
        return requestdetails;

    }

    private HashMap<String, String> getRequestHeaders(HttpServletRequest request) {

        HashMap<String, String> headerdetails = new HashMap<>();
        for (Enumeration<?> e = request.getHeaderNames(); e.hasMoreElements(); ) {
            String nextHeaderName = (String) e.nextElement();
            String headerValue = request.getHeader(nextHeaderName);
            headerdetails.put(nextHeaderName, headerValue);
        }
        return headerdetails;
    }
}
