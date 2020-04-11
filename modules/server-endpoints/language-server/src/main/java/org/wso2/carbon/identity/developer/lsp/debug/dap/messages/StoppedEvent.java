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

package org.wso2.carbon.identity.developer.lsp.debug.dap.messages;

/**
 * Stopped event for the debug session.
 */
public class StoppedEvent extends Event {

    private int line;
    private String resourceName;

    public StoppedEvent(String type, String event,
                        int line, String resourceName) {

        super(type, event);
        this.resourceName = resourceName;
        this.line = line;
    }

    /**
     * Gets the line.
     *
     * @return line
     */
    public int getLine() {

        return line;
    }

    /**
     * Gets the resourceName.
     *
     * @return the source name of the breakpoints.
     */
    public String getResourceName() {

        return resourceName;
    }
}
