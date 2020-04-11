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

package org.wso2.carbon.identity.java.agent;

import org.wso2.carbon.identity.java.agent.connect.InterceptionEngine;
import org.wso2.carbon.identity.java.agent.host.DefaultInterceptionEngine;

/**
 * Helper class to get hold of the interception engine.
 */
public class AgentHelper {

    private InterceptionEngine interceptionEngine;
    private static AgentHelper instance = new AgentHelper();

    private AgentHelper() {

        interceptionEngine = new DefaultInterceptionEngine();
    }

    /**
     * This method helps to get the AgentHelper instance without instantiating.
     *
     * @return
     */
    public static AgentHelper getInstance () {

        return instance;
    }

    /**
     * This method is to get the InterceptionEngine.
     *
     * @return
     */
    public InterceptionEngine getInterceptionEngine() {

        return interceptionEngine;
    }

}
