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

    /**
     * Gets the request Seq number.
     *
     * @return requestSeq
     */
    public long getRequestSeq() {

        return requestSeq;
    }

    /**
     * Sets the request Seq number.
     *
     * @param requestSeq Sequence number of the corresponding request.
     */
    public void setRequestSeq(long requestSeq) {

        this.requestSeq = requestSeq;
    }

    /**
     * Gets whether success.
     *
     * @return  is success
     */
    public boolean isSuccess() {

        return success;
    }

    /**
     * Sets whether success.
     *
     * @param success Outcome of the request.
     *                If true,the request was successful and the 'body' attribute may contain the result of the request.
     *                If the value is false, the attribute 'message' contains the error in short form and the 'body'
     *                may contain additional information (see 'ErrorResponse.body.error').
     */
    public void setSuccess(boolean success) {

        this.success = success;
    }

    /**
     * Gets the command.
     *
     * @return command
     */
    public String getCommand() {

        return command;
    }

    /**
     * Sets the command.
     *
     * @param command The command requested.
     */
    public void setCommand(String command) {

        this.command = command;
    }

    /**
     * Gets the message.
     *
     * @return
     */
    public String getMessage() {

        return message;
    }

    /**
     *  Sets the message.
     *
     * @param message Contains the raw error in short form if 'success' is false.
     *                This raw error might be interpreted by the frontend and is not shown in the UI.
     *                Some predefined values exist.
     *                Values:
     *                'cancelled': request was cancelled.
     *                etc.
     */
    public void setMessage(String message) {

        this.message = message;
    }

    /**
     * Gets the body.
     *
      * @return body
     */
    public Argument getBody() {

        return body;
    }

    /**
     * Sets the body.
     *
     * @param body Contains request result if success is true and optional error details if success is false.
     */
    public void setBody(Argument body) {

        this.body = body;
    }
}
