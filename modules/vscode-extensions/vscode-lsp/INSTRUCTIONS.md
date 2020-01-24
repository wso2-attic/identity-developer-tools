# Language Server Protocol For Adaptive Authentication Scripts


### Introduction

Language Server is meant to provide the language-specific smarts and communicate with development tools over a protocol that enables inter-process communication. This is what Microsoft is saying about the language server. In simply we can say when you are writing a programing code, IDE connects with the language server and it provides all those specific features of the language to the IDE. So you get the completion items, you get error suggestions and IDE so friendly for programmers. All these are because of the language server.

WSO2 Identity Server (WSO2 IS) supports script-based adaptive authentication, which allows you to use a script to set up appropriate authentication factors depending on your scenario. This enables ensuring security without impacting usability at the time of authentication. 

The project was mainly focused on giving a langage server support for the adaptive authentication scripts from a vscode extension. Apart from that, the project had a main focus to enhance the developer experience on application management from the vscode extension as well. For that I have implemented the following functionalities in the vscode extension.

Language server support for adaptive authentication scripts.

![](https://i.imgur.com/QndiXfd.png)


Created the Method to configure the details and the get the access token from the vscode itself.

![](https://i.imgur.com/L56N8wi.png)


Created an IAM own activity bar inside the vscode extension. And extract the service providers and the script libraries from the Identity servers and show as a tree view.


![](https://i.imgur.com/IUDUMnP.png)



Generate a graphical view for the service providers details.

![](https://i.imgur.com/z2MH4QP.png)



Created the method to give adaptive scripts templates as the snippents.

![](https://i.imgur.com/pVeKP57.png)




Created the way to suggest the exported functions of the function libraries.

![](https://i.imgur.com/7CyKd81.png)



Created the way to update the service providers adaptive script from vscode extension (Pressing the sync button in the right hand corner of the editor.).

![](https://i.imgur.com/iBJ1B79.png)



### How to set up

* Clone https://github.com/wso2-incubator/identity-developer-tools

* Go to `modules/identity-java-agent` and run `mvn clean install`.

* Go to `modules/server-endpoints/language-server/`  and run `mvn clean install`.

* Then go to `modules/identity-java-agent/java-agent/target` and copy, `org.wso2.carbon.identity.developer.java-agent-1.0.0-SNAPSHOT-jar-with-dependencies.jar` file to `$CARBON_HOME/lib` 

* Then go to `modules/server-endpoints/language-server/target` and copy `lsp.war` file to `$CARBON_HOME/repository/deployment/server/webapps`

* Next modify the startup script `("wso2server.sh" )` by adding the following code.
`javaagent:$CARBON_HOME/lib/org.wso2.carbon.identity.developer.java-agent-1.0.0-SNAPSHOT-jar-with-dependencies.jar=org.wso2.carbon.identity.java.agent.LoggingInterceptor\;$CARBON_HOME/repository/logs/intercept.log \`


* Then add the following code at the end of the `deplyment.toml` file.

```
[[resource.access_control]]
context = "(.*)/lsp/(.*)"
secure = false
http_method = "all"
```

* Open the `modules/vscode-extensions/vscode-lsp/` from the vscode and use the terminal of the vscode and run command `npm i`.

* Then run the command `npm run compile`.

* Then open the debug by pressing `F5`.



### How to run

Then Create a service provider inside your wso2 IS for more click here. (make sure to add the redirect url of the service provider to http://localhost:8010/oauth).

Next open a new workspace or a folder  using the vscode [Extension Development Host].

Then press ctrl+p and from the command palette search Login with WSO2 IAM and press enter.

![](https://i.imgur.com/GdWYbNT.png)


Then you will get a webview as follows.

![](https://i.imgur.com/c9ixqyg.png)




Fill the IAM url , tenant domain, client Id and the client secret correctly and then you will move to the WSO2 IS login.

![](https://i.imgur.com/SmfJaGR.png)


Enter your credential and login to the system. After the success message goes back to the vscode then you will get a list of service providers and the list of script libraries in the tenant domain you entered. Then click any service provider name.


![](https://i.imgur.com/pRqUdXb.png)


Then you will get a  graphical view of the service provider details. There you have a button named script. Then click the script button.

 ![](https://i.imgur.com/is9hZQI.png)



It will open the adaptive script of the service provider.

![](https://i.imgur.com/hDzHPCR.png)




Then you can edit your adaptive script file and press the sync button in the upper right hand corner of the right hand side. 

![](https://i.imgur.com/ekZDkJO.png)


Then the adaptive script details of the service providers will be updated in the domain you entered.
