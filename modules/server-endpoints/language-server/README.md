# Language Server Webapp for Identity Server

This is a work-in-progress language server support for identity server

It has two components
1. Java side Webapp to host language server 
2. VS-Code Plugin to connect Java webapp wia websocket

### Pre-Requisites
* wso2is
* Java 1.8 or later

### How to build
1. checkout the project
2. build with maven
    ```
    mvn clean install
    ```
3. Copy the built war in to the wso2is webapps
    ```
    cp target/lsp.war $wso2is/repository/deployment/server/webapps    
    ```
