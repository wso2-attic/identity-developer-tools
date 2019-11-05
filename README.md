# identity-developer-tools
Tooling for enabling developer with rich experience on WSO2 Identity Server




# How to add debug support to IS
* build 
```
mvn clean install
``` 
* Copy agent to IS
```
cp modules/identity-java-agent/java-agent/target/org.wso2.carbon.identity.developer.java-agent-1.0.0-SNAPSHOT.jar $CARBON_HOME/lib
``` 
* Modify the startup script to enable agent "wso2server.sh"
```
-javaagent:$CARBON_HOME/lib/org.wso2.carbon.identity.developer.java-agent-1.0.0-SNAPSHOT-jar-with-dependencies.jar=org.wso2.carbon.identity.java.agent.LoggingInterceptor\;$CARBON_HOME/repository/logs/intercept.log \
```
* Copy the lsp endpoint war
```
cp modules/server-endpoints/language-server/target/lsp.war $CARBON_HOME/repository/deployment/server/webapps
```
* Start the server
* Play with VS-Code extensions