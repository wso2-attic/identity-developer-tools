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

package org.wso2.carbon.identity.java.agent.connect;

/**
 * Interception engine which will be used by other components to listen to execution.
 */
public interface InterceptionEngine {

    /**
     * Adds an interception listener to the engine.
     * The listener is fired if the filter is applied.
     *
     * @param filter   Filter for method entry.
     * @param listener Which is interested in the event.
     */
    void addListener(MethodEntryInterceptionFilter filter, InterceptionListener listener);

    /**
     * Removed The listener from the engine.
     *
     * @param listener Which is interested in the event.
     */
    void removeListener(InterceptionListener listener);
}
