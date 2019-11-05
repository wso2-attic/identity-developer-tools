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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * JSON Debug Request
 */
public class Request extends Message {

    private static final Log log = LogFactory.getLog(Request.class);

    private String command;
    private List<Argument> arguments;

    public Request(String type, long seq, String command,
                   List<Argument> arguments) {

        super(type, seq);
        this.command = command;
        this.arguments = arguments;
    }

    public String getCommand() {

        return command;
    }

    public void setCommand(String command) {

        this.command = command;
    }

    public List<Argument> getArguments() {

        return arguments;
    }

    public void setArguments(List<Argument> arguments) {

        this.arguments = arguments;
    }
}
