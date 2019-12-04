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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.Argument;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.BreakpointRequest;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.ProtocolMessage;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.Request;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.Response;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.StoppedEvent;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.VariablesRequest;
import org.wso2.carbon.identity.developer.lsp.debug.dap.messages.VariablesResponse;
import org.wso2.carbon.identity.developer.lsp.debug.dap.serializer.JsonDap;
import org.wso2.carbon.identity.developer.lsp.debug.runtime.config.DebugListenerConfigurator;
import org.wso2.carbon.identity.java.agent.AgentHelper;
import org.wso2.carbon.identity.java.agent.connect.InterceptionEngine;
import org.wso2.carbon.identity.java.agent.connect.InterceptionListener;
import org.wso2.carbon.identity.java.agent.host.InterceptionEventType;
import org.wso2.carbon.identity.java.agent.host.MethodContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.websocket.EncodeException;
import javax.websocket.Session;

/**
 * Manages the debug sessions.
 */
public class DebugSessionManagerImpl implements DebugSessionManager, InterceptionListener {

    private static Log log = LogFactory.getLog(DebugSessionManagerImpl.class);

    private Map<Session, DebugSession> activeDebugSessions = new HashMap<>();

    private InterceptionEngine interceptionEngine;

    private VariableTranslator variableTranslator;

    public void init() {

        interceptionEngine = AgentHelper.getInstance().getInterceptionEngine();
        variableTranslator = new DefaultVariableTranslator();
        if (interceptionEngine == null) {
            log.error(
                    "Java Instrumentation needed for debug is not initialized. Debugging will not function correctly");
            return;
        }

        initializeListeners();
    }

    public void destroy() {

        interceptionEngine.removeListener(this);
    }

    public void addSession(Session session) {

        activeDebugSessions.put(session, createSession(session));
    }

    @Override
    public Response handle(Session session, Request request) {

        DebugSession debugSession = activeDebugSessions.get(session);
        if (debugSession == null) {
            log.error("No session found in the active session list");
            return null;
        }
        switch (request.getCommand()) {
            case "setBreakpoint":
                return setBreakpoints(debugSession, (BreakpointRequest) request);
            case "variables":
                return readVariables(debugSession, (VariablesRequest) request);
        }
        Response response = new Response(request.getType(), request.getId(), request.getId(), true, "", "", null);
        return response;
    }

    private Response readVariables(DebugSession debugSession, VariablesRequest request) {

        System.out.println("Read Variables id : "+request.getId());
        MethodContext methodContext = debugSession.getCurrentMethodContext();
        if (methodContext == null) {
            HashMap<String, Object> variables = new HashMap<>();
            Argument<Map<String, Object>> variablesArgument = new Argument<Map<String, Object>>(variables);
            VariablesResponse variablesResponse = new VariablesResponse(request.getType(), request.getId(), request.getId(),true, request.getCommand(),
                    request.getCommand(), variablesArgument);
            return variablesResponse;
        }

        HashMap<String, Object> variables = new HashMap<>();

        Object[] arguments = methodContext.getArguments();
        if(arguments != null) {
            for(int i = 0; i< arguments.length; i++) {
                variables.put("var_"+i, variableTranslator.translate(arguments[i]));
            }
        }
        Argument<Map<String, Object>> variablesArgument = new Argument<Map<String, Object>>(variables);

        VariablesResponse variablesResponse = new VariablesResponse(request.getType(), request.getId(), request.getId(),true, request.getCommand(),
                request.getCommand(), variablesArgument);
        return variablesResponse;
    }

    @Override
    public void handleEvent(InterceptionEventType type, MethodContext methodContext) {

        if (activeDebugSessions.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("No active debug sessions found");
            }
            return;
        }
        Map.Entry<Session, DebugSession> interestedSession = findInterestedDebugSession(methodContext);
        if (interestedSession == null) {
            if (log.isDebugEnabled()) {
                log.debug("No debug session interested in this event: " + methodContext.getMethodName());
            }
            return;
        }

        DebugSession debugSession = interestedSession.getValue();

        ProtocolMessage messageToClient = null;
        switch (type) {
            case METHOD_ENTRY:
                messageToClient = processBreakpoint(type, methodContext, debugSession);
        }

        if (messageToClient != null) {
            sendRequestToClient(interestedSession.getKey(), messageToClient);
        }
    }

    private StoppedEvent processBreakpoint(InterceptionEventType type, MethodContext methodContext,
                                           DebugSession debugSession) {

        //This is just simple implementation.
        BreakpointInfo[] breakpointInfos = debugSession.getBreakpointInfos();

        debugSession.setCurrentMethodContext(methodContext);
        if (breakpointInfos.length > 0) {
            BreakpointInfo breakpointInfo = breakpointInfos[0];
            StoppedEvent stoppedEvent = new StoppedEvent("breakpoint", "breakpoint",
                    breakpointInfo.getBreakpointLocations()[0],
                    breakpointInfo.getResourceName());
            return stoppedEvent;
        }

        return null;
    }

    private void sendRequestToClient(Session websocketSession, ProtocolMessage message) {

        try {
            JsonDap jsonDap = new JsonDap();
            jsonDap.init();
            String text = jsonDap.encode(message);
            websocketSession.getBasicRemote().sendText(text);
        } catch (IOException e) {
            log.error("Error sending back a request to client", e);
        }
    }

    private Map.Entry<Session, DebugSession> findInterestedDebugSession(MethodContext methodContext) {

        //For not, just return the first entry. We need to have a better filter later.
        if (!activeDebugSessions.isEmpty()) {
            for (Map.Entry<Session, DebugSession> entry : activeDebugSessions.entrySet()) {
                return entry;
            }
        }
        return null;
    }

    /**
     * Creates a new debug session associated with current session.
     *
     * @param session
     * @return
     */
    private DebugSession createSession(Session session) {

        DebugSession debugSession = new DebugSession();
        debugSession.setSession(session);
        return debugSession;
    }

    @Override
    public void removeSession(Session session) {

        activeDebugSessions.remove(session);
    }

    private Response setBreakpoints(DebugSession debugSession, BreakpointRequest request) {

        Response response = new Response(request.getType(), request.getId(), request.getId(), true, "", "", null);

        debugSession.setBreakpoints(request.getSourceName(), request.getBreakpoints());
        return response;
    }

    private void initializeListeners() {

        DebugListenerConfigurator configurator = new DebugListenerConfigurator(this);
        configurator.configure(interceptionEngine);
    }
}
