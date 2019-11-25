                   
# **Debugger For IS Authentication flow.**

Steps for Setup


**1.  vscode-lsp**
* build

    `npm install`

**2. java-agent**
* Build

   ` mvn clean install`
* copy agent to IS

    `cp modules/identity-java-agent/java-agent/target/org.wso2.carbon.identity.developer.java-agent-1.0.0-SNAPSHOT-jar-with-dependencies.jar $CARBON_HOME/lib`
     
* Modify the startup script to enable agent "wso2server.sh"

    `-javaagent:$IS_HOME/lib/org.wso2.carbon.identity.developer.java-agent-1.0.0-SNAPSHOT-jar-with-dependencies.jar=org.wso2.carbon.identity.java.agent.LoggingInterceptor\;$ICARBON_HOME/repository/logs/intercept.log \`

**3. language-Server**
* Copy the lsp endpoint war

     `cp modules/server-endpoints/language-server/target/lsp.war $CARBON_HOME/repository/deployment/server/webapps`
    
    After,
* Start the IS Server

* Start debug mode on vscode-debug(click F5).Then workspace will open.

* Start debug mode on the Extension Development Host(click F5) and select “WSO2 IAM DEBUG” as debug environment. Add “readme.md” file to debug on workspace.

* Open the sample application on Tomcat server.

* Put a break point on “readme.md” file.

* Send a login request using sample application.





