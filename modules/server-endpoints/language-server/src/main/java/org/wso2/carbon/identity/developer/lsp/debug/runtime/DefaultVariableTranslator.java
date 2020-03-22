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

package org.wso2.carbon.identity.developer.lsp.debug.runtime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Scanner;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Default variable translator, uses reflection to get the variables and values.
 */
public class DefaultVariableTranslator implements VariableTranslator {

    private static Log log = LogFactory.getLog(DebugSessionManagerImpl.class);

    @Override
    public Object translate(Object object, int variablesReference) {

        if (object instanceof HttpServletRequest) {
            HashMap<String, Object> requestdetails = new HashMap<>();
            HttpServletRequest httpServletRequest = (HttpServletRequest) object;
            requestdetails.put("cookies", httpServletRequest.getCookies());
            requestdetails.put("headers", this.getRequestHeaders(httpServletRequest));
            requestdetails.put("body", this.getRequestBody(httpServletRequest));
            requestdetails.put("variablesReference", variablesReference);
            return requestdetails;
        } else if (object instanceof HttpServletResponse) {
            HashMap<String, Object> responsedetails = new HashMap<>();
            HttpServletResponse httpServletResponse = (HttpServletResponse) object;
            responsedetails.put("status", httpServletResponse.getStatus());
            responsedetails.put("headers", this.getResponseHeaders(httpServletResponse));
            responsedetails.put("body", this.getResponseBody(httpServletResponse));
            responsedetails.put("variablesReference", variablesReference);
            return responsedetails;
        }

        return object;
    }


    private String getRequestBody(HttpServletRequest request) {

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            Scanner s = null;
            try {
                s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
            } catch (IOException e) {
                log.error("Error Parsing the body of the Request.", e);
            }
            return s.hasNext() ? s.next() : "";
        }
        return "";
    }

    private String getResponseBody(HttpServletResponse response) {
            return "";
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

    private HashMap<String, String> getResponseHeaders(HttpServletResponse response) {

        HashMap<String, String> headerdetails = new HashMap<>();
        for (String nextHeaderName : response.getHeaderNames()) {
            String headerValue = response.getHeader(nextHeaderName);
            headerdetails.put(nextHeaderName, headerValue);
        }
        return headerdetails;
    }





}
