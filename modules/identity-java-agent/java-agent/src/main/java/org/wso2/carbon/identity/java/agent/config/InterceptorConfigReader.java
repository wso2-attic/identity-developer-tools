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
     * Reads the configs in the class resource
     * "instrumentation-config.json".
     *
     * @return
     */
    public List<InterceptorConfig> readConfig() {

        ArrayList<InterceptorConfig> result = new ArrayList<>();

        //TODO: Read the instrumentation-config.json
        InterceptorConfig interceptorConfig = new InterceptorConfig();
        interceptorConfig.setClassName(
                "org/wso2/carbon/identity/application/authentication/framework/handler/request/impl/DefaultRequestCoordinator");
        interceptorConfig.addMethodSignature("handle",
                "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;)V");

        result.add(interceptorConfig);

        interceptorConfig = new InterceptorConfig();
        interceptorConfig.setClassName(
                "jdk/nashorn/internal/runtime/DebuggerSupport");
        interceptorConfig.addMethodSignature("notifyInvoke",
                "(Ljava/lang/invoke/MethodHandle;)V");
        result.add(interceptorConfig);
        return result;
    }
}
