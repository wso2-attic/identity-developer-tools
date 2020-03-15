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

package org.wso2.carbon.identity.developer.lsp.debug.runtime;

import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.Request;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.Response;
import org.wso2.carbon.identity.java.agent.host.InterceptionEventType;
import org.wso2.carbon.identity.java.agent.host.MethodContext;

import javax.websocket.Session;

/**
 * Manages the debug session.
 */
public interface DebugSessionManager {

    /**
     * Adds a new debug session to manage.
     *
     * @param session
     */
    void addSession(Session session);

    /**
     * Handles the debug request.
     * @param request
     * @return
     */
    Response handle(Session session, Request request);

    /**
     * Handles the event coming from the instrumentation agent.
     *
     * @param type
     * @param methodContext
     */
    void handleEvent(InterceptionEventType type, MethodContext methodContext);

    /**
     * Removes the debug session.
     * We need to remove our internal references when the client is disconnected.
     *
     * @param session
     */
    void removeSession(Session session);

    /**
     * Get DebugSession using session.
     * @param session
     * @return
     */
    DebugSession getDebugSession(Session session);
}
