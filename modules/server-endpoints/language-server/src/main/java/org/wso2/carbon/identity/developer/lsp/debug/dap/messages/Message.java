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
 * A message, which have a sequence number.
 */
public class Message extends ProtocolMessage {

    private long id;

    public Message(String type, long id) {

        super(type);
        this.id = id;
    }

    /**
     * Getter which gets the id.
     *
     * @return the id of the message.
     */
    public long getId() {

        return id;
    }

    /**
     * Setter which sets the id.
     *
     * @param seq Sequence number of the corresponding request.
     */
    public void setId(long seq) {

        this.id = seq;
    }
}
