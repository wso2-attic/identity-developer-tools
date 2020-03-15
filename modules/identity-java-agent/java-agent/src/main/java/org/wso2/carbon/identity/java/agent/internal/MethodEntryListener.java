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

package org.wso2.carbon.identity.java.agent.internal;

import org.wso2.carbon.identity.java.agent.AgentHelper;
import org.wso2.carbon.identity.java.agent.connect.InterceptionEngine;
import org.wso2.carbon.identity.java.agent.host.InterceptionEventType;
import org.wso2.carbon.identity.java.agent.host.MethodContext;

/**
 * Listens the method entry on a class, captures inbound parameters.
 */
public class MethodEntryListener {

    /**
     * This method will be injected by {@link InterceptingClassTransformer} upon matching the method name and signature.
     *
     * @param methodName The method name which was called.
     * @param signature  The signature of the method being called
     * @param args       The arguments (values) of the method.
     */
    public static void methodEntered(String className, String methodName, String signature,
                                     Class[] sig, Object[] args) {

        InterceptionEngine engine = AgentHelper.getInstance().getInterceptionEngine();
        if (engine instanceof EventPublisher) {
            Thread thread = Thread.currentThread();
            MethodContext methodContext = new MethodContext(thread, methodName, signature);
            methodContext.setArgumentValues(args);
            methodContext.setArgumentTypes(sig);
            methodContext.setClassName(className);
            ((EventPublisher) engine).fireEvent(InterceptionEventType.METHOD_ENTRY, methodContext);
        }
    }

}
