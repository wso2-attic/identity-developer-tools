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

package org.wso2.carbon.identity.java.agent.host;

import org.wso2.carbon.identity.java.agent.connect.InterceptionEngine;
import org.wso2.carbon.identity.java.agent.connect.InterceptionListener;
import org.wso2.carbon.identity.java.agent.connect.MethodEntryInterceptionFilter;
import org.wso2.carbon.identity.java.agent.internal.EventPublisher;

import java.util.HashMap;
import java.util.Map;

/**
 * Java interception and instrumentation engine.
 */
public class DefaultInterceptionEngine implements InterceptionEngine, EventPublisher {

    private Map<MethodEntryInterceptionFilter, InterceptionListener> filters = new HashMap<>();

    @Override
    public void fireEvent(InterceptionEventType type, MethodContext context) {

        for (Map.Entry<MethodEntryInterceptionFilter, InterceptionListener> entry : filters.entrySet()) {
            boolean shouldIntercept = entry.getKey().shouldIntercept(type, context);
            if (shouldIntercept) {
                notifyListener(type, context, entry.getValue());
                // Currently only one event lister per event type is supported.
                break;
            }
        }
    }

    @Override
    public void addListener(MethodEntryInterceptionFilter filter, InterceptionListener listener) {

        filters.put(filter, listener);
    }

    @Override
    public void removeListener(InterceptionListener listener) {

        MethodEntryInterceptionFilter key = null;
        for (Map.Entry<MethodEntryInterceptionFilter, InterceptionListener> entry : filters.entrySet()) {
            if (entry.getValue() == listener) {
                key = entry.getKey();
            }
        }

        if (key != null) {
            filters.remove(key);
        }
    }

    private void notifyListener(InterceptionEventType type, MethodContext context, InterceptionListener listener) {

        listener.handleEvent(type, context);
    }
}
