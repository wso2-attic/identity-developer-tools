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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * Default variable translator, uses reflection to get the variables and values.
 */
public class DefaultVariableTranslator implements VariableTranslator {

    private static Log log = LogFactory.getLog(DebugSessionManagerImpl.class);

    @Override
    public Object translate(Object object) {
        if (object instanceof  HttpServletRequest) {
            HashMap<String, Object> requestdetails = new HashMap<>();
            HttpServletRequest httpServletRequest = (HttpServletRequest) object;
            requestdetails.put("cookies", httpServletRequest.getCookies());
            requestdetails.put("headers", this.getHeaders(httpServletRequest));
            try {
                requestdetails.put("body", this.getBody(httpServletRequest));
            } catch (IOException e) {
                log.error("Error Parsing the body of the Request. ",e);
            } finally {
                return requestdetails;
            }

        } else if (object instanceof HttpServletResponse) {
            return object;
        }

        return object;
    }


    private String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        BufferedReader bufferedReader = null;
        try {
            StringBuilder buffer = new StringBuilder();
            bufferedReader = request.getReader();
            if (bufferedReader != null) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line);
                }
                body = buffer.toString();
            } else {
                body = "";
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    log.error("Error Parsing the body of the Request.", ex);
                }
            }
        }
        return body;
    }


    private HashMap<String, String> getHeaders(HttpServletRequest request) {
        HashMap<String, String> headerdetails = new HashMap<>();
        for (Enumeration<?> e = request.getHeaderNames(); e.hasMoreElements();) {
            String nextHeaderName = (String) e.nextElement();
            String headerValue = request.getHeader(nextHeaderName);
            headerdetails.put(nextHeaderName, headerValue);
        }
        return headerdetails;
    }
}
