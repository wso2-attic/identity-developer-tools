# Debugger for Identity Server Authentication Flow

This will use to get debugger support for Identity Server Authentication Flow.

It has three components
1. VS-Code Plugin to connect Debug Server via websocket
2. Debug Server analyse debug requests and responses
3. Java agent to intercept IS

### Pre-Requisites
* WSO2IS
* Java 1.8 or later

### How to build
1. Checkout the java-agent module.
2. Build with maven.
    ```
    mvn clean install
    ```
3. Copy the built jar file  ``` org.wso2.carbon.identity.developer.java-agent-1.0.0-SNAPSHOT-jar-with-dependencies.jar  ``` on  ```modules/identity-java-agent/java-agent/target/ ``` path in to WSO2IS  ```lib ```.
     ```
    cp modules/identity-java-agent/java-agent/target/org.wso2.carbon.identity.developer.java-agent-1.0.0-SNAPSHOT-jar-with-dependencies.jar $CARBON_HOME/lib
    ```
4. Modify the startup script ```wso2server.sh``` in ```$CARBON_HOME/bin```  to enable agent .
    ```
    -javaagent:$CARBON_HOME/lib/org.wso2.carbon.identity.developer.java-agent-1.0.0-SNAPSHOT-jar-with-dependencies.jar \
    ```
5. Checkout to language-server module.
6. Build with maven.
    ```
    mvn clean install
    ```

7. Copy the built war file ```lsp.war``` on ```modules/server-endpoints/language-server/target/``` path in to the wso2is webapps ```$CARBON_HOME/repository/deployment/server/webapps```.
    ```
   cp modules/server-endpoints/language-server/target/lsp.war $CARBON_HOME/repository/deployment/server/webapps

    ```
8. Modify the ```identity.xml``` file in ```$CARBON_HOME/repository/conf/identity/identity.xml``` path in to deploy the ```lsp.war```.
by adding follows inside ``` <ResourceAccessControl> </ResourceAccessControl>```.
    ```
    <Resource context="(.*)/lsp/(.*)" secured="false" http-method="all">
        </Resource>
     ```
9. Start the debug mode on vscode-debug module.
10. Then another vscode workspace will open.
11. Click the "script" button on diagram and put breakpoints on script.
12. Start the debug mode on script file.
