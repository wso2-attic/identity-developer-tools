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

package org.wso2.carbon.identity.developer.lsp.endpoints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.ProtocolMessage;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.Request;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.Response;
import org.wso2.carbon.identity.developer.lsp.debug.dap.serializer.JsonDap;
import org.wso2.carbon.identity.developer.lsp.debug.runtime.DebugSession;
import org.wso2.carbon.identity.developer.lsp.debug.runtime.DebugSessionManager;

import java.io.IOException;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * The entry endpoint for language server.
 */

@ServerEndpoint(value = "/debug", configurator = OSGIBindingConfigurator.class)
public class DebugEndpoint {

    private static final Log log = LogFactory.getLog(DebugEndpoint.class);

    private JsonDap jsonDap;

    @Inject
    private DebugSessionManager debugSessionManager;

    public DebugEndpoint() {

        this.jsonDap = new JsonDap();
        jsonDap.init();

    }

    /**
     * Method is called when a connection is established.
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {

        debugSessionManager.addSession(session);
        try {
            JsonDap jsonDap = new JsonDap();
            jsonDap.init();
            ProtocolMessage message = new ProtocolMessage("connected");
            String text = jsonDap.encode(message);
            session.getBasicRemote().sendText(text);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method is called when user closes the connection.
     * <p>
     * Note: You cannot send messages to the client from this method
     */
    @OnClose
    public void onClose(Session session) {

        log.info("Session " + session.getId() + " has ended");
        debugSessionManager.removeSession(session);
    }

    /**
     * Method is called when a user sends a message to this server endpoint.
     * Method intercepts the message and allows us to react accordingly.var onLoginRequest = function (context) {

     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException, EncodeException {

        try {
            Request request = jsonDap.decode(message);
            Response response = debugSessionManager.handle(session, request);
            session.getBasicRemote().sendText(jsonDap.encode(response));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method is called when an error occurs.
     *
     * @param e
     */

    @OnError
    public void onError(Throwable e) {

        log.error("Web socket session error", e);
    }

}
