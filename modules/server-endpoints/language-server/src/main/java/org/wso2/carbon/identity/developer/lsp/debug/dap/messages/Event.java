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
 * Debug Event.
 */
public class Event extends ProtocolMessage {

    private String event;

    private Argument body;

    public Event(long seq, String type, String event,
                 Argument body) {

        super(seq, type);
        this.event = event;
        this.body = body;
    }

    public String getEvent() {

        return event;
    }

    public void setEvent(String event) {

        this.event = event;
    }

    public Argument getBody() {

        return body;
    }

    public void setBody(Argument body) {

        this.body = body;
    }
}
