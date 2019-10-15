# Language Server Webapp for Identity Server

This is a work-in-progress language server support for identity server

It has two components
1. Java side Webapp to host language server 
2. VS-Code Plugin to connect Java webapp wia websocket

### Pre-Requisites
* Tomcat 9
* Java 1.8 or later

### How to build
1. checkout the project
2. build with maven
    ```
    mvn clean install
    ```
3. Copy the built war file into tomcat 9
    ```
    cp target/lsp.war $TOMCAT_HOME/webapps    
    ```
