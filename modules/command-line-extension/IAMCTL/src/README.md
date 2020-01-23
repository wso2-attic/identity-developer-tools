## IAM-CTL

### How to build the executable file 
To build the IAMCTL in your computer, you should have the go in your computer. If you  have not go in your computer you can install go using this [link](https://golang.org/doc/install).

Now you can build the IAMCTL in your computer.
1. Open a terminal and set directory to ```<command-line-extension_ PATH>/IAMCTL```

2. Then build the IAMCTL.
```
go build
```
 As the result created the executable file named ```iamctl```
 
 ### If you want to build for supports to cross platform you can do as follows.
 
  Open a terminal and set directory to ```<command-line-extension_ PATH>/IAMCTL```
  Then build the IAMCTL.
  
  To build for IOS:
  ```
GOOS=darwin GOARCH=amd64 go build
   ```
To build for Windows:
```
GOOS=windows GOARCH=amd64 go build
```
To build for Linux 64-bit:
```
GOOS=linux GOARCH=amd64 go build
```

 
         
    