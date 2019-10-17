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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.wso2.carbon.identity.application.authentication.framework.JsFunctionRegistry;
import org.wso2.carbon.identity.developer.lsp.LanguageProcessor;
import org.wso2.carbon.identity.developer.lsp.LanguageProcessorFactory;
import org.wso2.carbon.identity.developer.lsp.completion.CompletionListGenerator;
import org.wso2.carbon.identity.jsonrpc.JsonRPC;
import org.wso2.carbon.identity.jsonrpc.Request;
import org.wso2.carbon.identity.jsonrpc.SuccessResponse;
import org.wso2.carbon.identity.parser.ParserT;

import java.io.IOException;
import javax.script.ScriptException;
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

@ServerEndpoint(value = "/lsp", configurator = OSGIBindingConfigurator.class )
public class LspEndpoint {

    private JsonRPC jsonRPC;

    @OsgiService
    private JsFunctionRegistry jsFunctionRegistry;

    private LanguageProcessorFactory languageProcessorFactory;

    public LspEndpoint() {

        this.jsonRPC = new JsonRPC();
        jsonRPC.init();

        this.languageProcessorFactory = new LanguageProcessorFactory();
        languageProcessorFactory.init();
    }

    /**
     * Method is called when a connection is established.
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {

        System.out.println(session.getId() + " has opened a connection");
        try {
            Gson gson = new Gson();
//            session.getBasicRemote().sendText("enwada");
            session.getBasicRemote().sendObject(gson.toJson("Hello"));
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (EncodeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method is called when user closes the connection.
     * <p>
     * Note: You cannot send messages to the client from this method
     */
    @OnClose
    public void onClose(Session session) {

        System.out.println("Session " + session.getId() + " has ended");
    }

    /**
     * Method is called when a user sends a message to this server endpoint.
     * Method intercepts the message and allows us to react accordingly.
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException, EncodeException {

        try {
            Request request = jsonRPC.decode(message);
            LanguageProcessor languageProcessor = languageProcessorFactory.getProcessor(request);
            SuccessResponse response = new SuccessResponse();
            if(languageProcessor == null) {
                //TODO: Descriptive error, no processor found
                response = new SuccessResponse();
            } else {
                if(request.getMethod().equals("onCompletion")){
                    JsonElement jsonElement1 = new JsonParser().parse(message);
                    JsonElement jsonElement2 = new JsonParser().parse(jsonElement1.getAsJsonObject().get("params").getAsString());
                    int line = Integer.parseInt(jsonElement2.getAsJsonObject().get("line").getAsString());
                    int charPosition = Integer.parseInt(jsonElement2.getAsJsonObject().get("character").getAsString());


                    ParserT parserT = new ParserT();
                    String scope = parserT.generateParseTree(jsonElement2.getAsJsonObject().get("text").getAsString(),line,charPosition);

//                response.setId(String.valueOf(line) + " ----> "+ String.valueOf(charPosition));
                    response.setId(scope);
//                    response.setResult(new JsonParser().parse(parserT.generateParseTree(jsonElement2.getAsJsonObject().get("text").getAsString(),line,charPosition)));
                    CompletionListGenerator completionListGenerator = new CompletionListGenerator();
                    completionListGenerator.setJsFunctionRegistry(jsFunctionRegistry);
                    response.setResult(completionListGenerator.getList(scope));

                }else{

                    JsonElement jsonElement1 = new JsonParser().parse(message);
                    JsonElement jsonElement2 = new JsonParser().parse(jsonElement1.getAsJsonObject().get("params").getAsString());

                    ParserT parserT = new ParserT();
//                response.setId(String.valueOf(line) + " ----> "+ String.valueOf(charPosition));
                    response.setId(request.getMethod());
//                response.setResult(new JsonParser().parse(parserT.generateParseTree(jsonElement2.getAsJsonObject().get("text").getAsString(),line,charPosition)));

                }
            }
            if(response == null) {
                //TODO: Descriptive error
                response = new SuccessResponse();
            }
            session.getBasicRemote().sendText(jsonRPC.encode(response));
        } catch (IOException | ScriptException ex) {
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

        e.printStackTrace();
    }

}
