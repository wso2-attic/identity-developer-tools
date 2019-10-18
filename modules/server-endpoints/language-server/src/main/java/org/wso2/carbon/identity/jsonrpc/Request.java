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

package org.wso2.carbon.identity.jsonrpc;

import com.google.gson.stream.JsonReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.developer.lsp.endpoints.OSGIBindingConfigurator;

/**
 * JSON RPC Request
 */
public class Request {

    private static final Log log = LogFactory.getLog(Request.class);

    /**
     * The id of the request
     */
    private String id;

    /**
     * The method requested
     */
    private String method;

    /**
     * The request parameters
     */
    private ParametersList paramsList;

    /**
     * Default constructor for un-marshallers
     */
    public Request() {

    }

    public Request(String id, String method, ParametersList paramsList) {

        this.id = id;
        this.method = method;
        this.paramsList = paramsList;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getMethod() {

        return method;
    }

    public void setMethod(String method) {

        this.method = method;
    }

    public ParametersList getParamsList() {

        return paramsList;
    }

    public void setParamsList(ParametersList paramsList) {

        this.paramsList = paramsList;
    }

    public String getParameter(String name) {

        if (paramsList == null) {
            if (log.isDebugEnabled()) {
                log.debug("Parameter List is null. Unable to get value for parameter: " + name);
            }
            return null;
        }

        return paramsList.getParameter(name);
    }

    public int getParameterAsInt(String name, int defaultValue) {

        if (paramsList == null) {
            if (log.isDebugEnabled()) {
                log.debug("Parameter List is null. Unable to get value for parameter: " + name);
            }
            return defaultValue;
        }

        return paramsList.getParameterAsInt(name, defaultValue);
    }
}
