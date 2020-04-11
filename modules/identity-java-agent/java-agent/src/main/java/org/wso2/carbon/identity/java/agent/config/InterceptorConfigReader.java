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

package org.wso2.carbon.identity.java.agent.config;


import java.util.ArrayList;

import java.util.List;

/**
 * Reads the interceptor config from the resources file in the classpath.
 */
public class InterceptorConfigReader {

    /**
     * Reads the configs in the class resource.
     * As per the documentation have to pass the Method signature in binary format.
     * use this link https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html to add the binary format.
     *
     * @return result
     */
    public List<InterceptorConfig> readConfig() {

        ArrayList<InterceptorConfig> result = new ArrayList<>();

        InterceptorConfig samlFilterConfig = new InterceptorConfig();
        /**
         * Add the Java DOC for the Strings.
         */
        samlFilterConfig.setClassName(
                "org/wso2/carbon/identity/sso/saml/servlet/SAMLSSOProviderServlet");

        samlFilterConfig.addMethodConfigs("doPost",
                "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;)V",
                true, false);

        samlFilterConfig.addMethodConfigs("sendResponse",
                "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;" +
                        "Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/" +
                        "String;Ljava/lang/String;)V", false, true);

        result.add(samlFilterConfig);
        return result;
    }
}
