# IAM-CTL

This will use to get support for create service providers inside the Identity Server.
Here you can create service providers by entering inputs as a command  and flags or entering inputs in an interactive way.


### Pre-Requisites
* WSO2 IS 5.10.0 alpha2 

### How to run
1. first clone or download ```commannd-line-extension``` module
2. copy the path of ```<commannd-line-extension/IAM-CTL_PATH>/iamcli```
3. Then you select your directory to work and open a terminal
4. You can choose any name as keyword of IAM-CTL and set that using following command.
Here I have chosen 'iamctl' as keyword.

    ```
    alias iamctl="<commannd-line-extension/IAM-CTL_PATH>/iamctl" 
    ```
5. Now you can run IAM-CTL using your keyword.
6. First you need to configure service provider to get access from identity server. For that this [link](https://docs.wso2.com/display/IS570/Configuring+OAuth2-OpenID+Connect+Single-Sign-On) will help to you..
7. Then use client_key, client_secret of created service provider to do the authorization relevant to server domain. It should be completed as follows.

```
iamctl existApp
```
Now you should gives answers for questions asked by CTL.
```
:~$ iamcli existApp
  ___      _      __  __            ____   _____   _     
 |_ _|    / \    |  \/  |          / ___| |_   _| | |    
  | |    / _ \   | |\/| |  _____  | |       | |   | |    
  | |   / ___ \  | |  | | |_____| | |___    | |   | |___ 
 |___| /_/   \_\ |_|  |_|          \____|   |_|   |_____|
                                                         
? Enter clientID: M4fucPehFTuFkHHLNGfxIEf6ydka
? Enter clientSecret: DWXnw7UgvsRUKWXrftvnU_vclzAa
? Enter Tenant domain: carbon.super
```
8. Then you need to configure server domain as follows.
### Set server domain by entering inputs as a command and flags.
```
iamctl serverConfiguration [flags]
```

Flags:
```
  -h, --help              help for serverConfiguration
  -p, --password string   enter your password
  -s, --server string     set server domain
  -u, --username string   enter your username
```
example:-
```
iamctl serverConfiguration -h                                           //help for serverConfiguration
iamctl serverConfiguration -s=https://localhost:9443 -u=admin -p=*****  //to complete the authorization
```
### Set server domain by entering inputs in an interactive way.
```
iamctl serverConfiguration
```
example:-
```
~$ iamcli serverConfiguration
  ___      _      __  __            ____   _____   _     
 |_ _|    / \    |  \/  |          / ___| |_   _| | |    
  | |    / _ \   | |\/| |  _____  | |       | |   | |    
  | |   / ___ \  | |  | | |_____| | |___    | |   | |___ 
 |___| /_/   \_\ |_|  |_|          \____|   |_|   |_____|
                                                         
? Enter IAM URL: https://localhost:9443
? Enter Username: admin
? Enter Password: *****
```
9.Now authorization part is completed. You can add application and get list of applications using IAM-CTL. Structure as follows.

### create service providers by entering inputs as a command and flags.
**Add application**
```
iamctl application [commands]
iamctl application     add      [flags]
```

 Flags:
 ```
 -c, --callbackURl string    callbackURL  of SP - **for oauth application
 -d, --description string    description of SP - **for basic application
 -h, --help                  help for add
 -n, --name string           name of service provider - **compulsory
 -s, --serverDomain string   server Domain - **compulsory
 -t, --type string           Enter application type as 'basic' or 'oauth' (default "oauth")
 ```
 
example:-
```
//create an oauth application
iamctl application add -s=https://localhost:9443 -n=Test 
iamctl application add -t=oauth -s=https://localhost:9443 -n=Test
iamctl application add -t=oauth -s=https://localhost:9443 -n=Test -d=this is description
iamctl application add -t=oauth -s=https://localhost:9443 -n=Test -c=https://localhost:8010/oauth
iamctl application add -t=oauth -s=https://localhost:9443 -n=Test -c=https://localhost:8010/oauth -d=this is description

//create an basic application
iamctl application add -t=basic -s=https://localhost:9443 -n=Test
iamctl application add -t=basic -s=https://localhost:9443 -n=Test -d=this is description
```
**Get list of applications**
```
iamctl application     list     [flags]
```
Flags:
```
-h, --help            help for list
-s, --server string   server
```
example:-
```
//get list of applications
iamctl application list -s=https://localhost:9443
```
### create service providers by entering inputs in an interactive way.
**Add application and get list of applications**

```
iamctl application
```
It gives following output after entering the server domain.
```
$ iamctl application
  ___      _      __  __            ____   _____   _     
 |_ _|    / \    |  \/  |          / ___| |_   _| | |    
  | |    / _ \   | |\/| |  _____  | |       | |   | |    
  | |   / ___ \  | |  | | |_____| | |___    | |   | |___ 
 |___| /_/   \_\ |_|  |_|          \____|   |_|   |_____|
                                                         
? Enter IS URL: https://localhost:9443
? Select the option to move on:  [Use arrows to move, type to filter]
> Create Service Provider
  Get List
  Exit
```
To add application you should select ```add application``` from selections.
example:-
```
~$ iamcli application
  ___      _      __  __            ____   _____   _     
 |_ _|    / \    |  \/  |          / ___| |_   _| | |    
  | |    / _ \   | |\/| |  _____  | |       | |   | |    
  | |   / ___ \  | |  | | |_____| | |___    | |   | |___ 
 |___| /_/   \_\ |_|  |_|          \____|   |_|   |_____|
                                                         
? Enter IS URL: https://localhost:9443
? Select the option to move on: Create Service App
? Select the configuration type: Basic application
? Enter service provider name: BasicApp
? Enter service provider description: this is description
```
To view list of applications you should select ```list``` from selections.
example:-
```
$ iamctl application
  ___      _      __  __            ____   _____   _     
 |_ _|    / \    |  \/  |          / ___| |_   _| | |    
  | |    / _ \   | |\/| |  _____  | |       | |   | |    
  | |   / ___ \  | |  | | |_____| | |___    | |   | |___ 
 |___| /_/   \_\ |_|  |_|          \____|   |_|   |_____|
                                                         
? Enter IS URL: https://localhost:9443
? Select the option to move on: Get List
```


    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
 
