package cmd

import (
	"bytes"
	"crypto/tls"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
)

type ServiceProvider struct{
	Name string `json:"name"`
	Description string `json:"description"`
}

func createSPBasicApplication(domainName string,spName string, spDescription string) {

	CLIENTID,CLIENTSECRET,TENANTDOMAIN=readSPConfig()

	var ADDAPPURL =domainName+"/t/"+TENANTDOMAIN+"/api/server/v1/applications"
	var err error
	var status int

	token := readFile(domainName)

	toJson := ServiceProvider{spName, spDescription}
	jsonData, err := json.Marshal(toJson)
	if err != nil {
		log.Println(err)
	}

	http.DefaultTransport.(*http.Transport).TLSClientConfig = &tls.Config{InsecureSkipVerify: true}

	req, err := http.NewRequest("POST", ADDAPPURL, bytes.NewBuffer(jsonData))
	if err!=nil{
		log.Fatalln(err)
	}
	req.Header.Set("Authorization", "Bearer "+token)
	req.Header.Set("accept", "*/*")
	req.Header.Set("Content-Type", "application/json")

	defer req.Body.Close()

	httpClient := &http.Client{}
	resp, err := httpClient.Do(req)
	if err != nil {
		log.Fatalln(err)
	}

	status = resp.StatusCode
	defer resp.Body.Close()

	if status == 401 {
		fmt.Println("Unauthorized access.\nPlease enter your Username and password for server.")

	}
	if status == 400 {
		fmt.Println("Provided parameters are not in correct format.")
	}
	if status == 403 {
		fmt.Println("Forbidden")
	}
	if status == 201{
		fmt.Println("Successfully created the service provider named '"+spName+"' at "+resp.Header.Get("Date"))
	}
	if status == 409 {
		fmt.Println("Already exists an application with same name:"+spName)
	}
}
