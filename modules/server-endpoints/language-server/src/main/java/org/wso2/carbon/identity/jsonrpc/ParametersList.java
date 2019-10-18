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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parameter list in JSON RPC Request.
 *
 */
public class ParametersList {

    private static final Log log = LogFactory.getLog(ParametersList.class);

    /**
     * List of parameters in the request
     */
    private Map<String, Parameter> parameters;

    /**
     * Default constructor for un-marshallers
     */
    public ParametersList() {

    }

    public ParametersList(List<Parameter> parameters) {

        this.parameters = new HashMap<>();
        if(parameters != null) {
            for(Parameter parameter: parameters) {
                this.parameters.put(parameter.getName(), parameter);
            }
        }
    }

    public String getParameter(String name) {
        Parameter parameter = parameters.get(name);
        if (parameter == null) {
            if(log.isDebugEnabled()) {
                log.debug("Parameter List is null. Unable to get value for parameter: "+name);
            }
            return null;
        }

        return  parameter.getValue();
    }

    public int getParameterAsInt(String name, int defaultValue) {
        String s = getParameter(name);
        if(s == null) {
            return defaultValue;
        }
        return Integer.parseInt(s);
    }
}
