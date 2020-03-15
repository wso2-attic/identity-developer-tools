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
 * JSON Debug Response.
 */
public class Response extends Message {

    private long requestSeq;
    private boolean success;
    private String command;
    private String message;

    private Argument body;

    public Response(String type, long seq, long requestSeq, boolean success, String command, String message,
                    Argument body) {

        super(type, seq);
        this.requestSeq = requestSeq;
        this.success = success;
        this.command = command;
        this.message = message;
        this.body = body;
    }

    public long getRequestSeq() {

        return requestSeq;
    }

    public void setRequestSeq(long requestSeq) {

        this.requestSeq = requestSeq;
    }

    public boolean isSuccess() {

        return success;
    }

    public void setSuccess(boolean success) {

        this.success = success;
    }

    public String getCommand() {

        return command;
    }

    public void setCommand(String command) {

        this.command = command;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = message;
    }

    public Argument getBody() {

        return body;
    }

    public void setBody(Argument body) {

        this.body = body;
    }
}
