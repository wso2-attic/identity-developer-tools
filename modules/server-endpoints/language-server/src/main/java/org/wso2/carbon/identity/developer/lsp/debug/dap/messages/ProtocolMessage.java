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
 * Generic protocol message for Debug protocol.
 *
 */
public class ProtocolMessage {

    private String type;

    public ProtocolMessage(String type) {

        this.type = type;
    }

    /**
     * Getter which gets the type.
     *
     * @return type
     */
    public String getType() {

        return type;
    }

    /**
     *  Setter which sets the type.
     *
     * @param type Message type.  Values can be 'request', 'response', 'event', etc.
     */
    public void setType(String type) {

        this.type = type;
    }
}
