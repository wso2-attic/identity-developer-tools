package cmd

import (
	"crypto/tls"
	"encoding/json"
	"io/ioutil"

	"log"
	"net/http"
	"strings"

	"net/url"
)

var IAMURL string
var AUTHURL string
//var REDIRECTURI string
var CLIENTID string
var CLIENTSECRET string
var TENANTDOMAIN string
var accessToken string
var refreshToken string
var err error

const SCOPE string ="/permission/admin/manage/identity/applicationmgt/update /permission/admin/manage/identity/applicationmgt/create /permission/admin/manage/identity/applicationmgt/view internal_application_mgt_update internal_application_mgt_create internal_application_mgt_view"

type oAuthResponse struct {
	AccessToken  string   `json:"access_token"`
	RefreshToken      string      `json:"refresh_token"`
	Scope string `json:"scope"`
	TokenType    string   `json:"token_type"`
	Expires int `json:"expires_in"`
}

func start(url string,userName string, password string){
	IAMURL=url
	AUTHURL= IAMURL+"/oauth2/token"

	accessToken,refreshToken,err=sendOAuthRequest(userName,password)
	if err != nil {
				log.Fatalln(err)
	}

	writeFiles(IAMURL,accessToken,refreshToken)
}

func sendOAuthRequest(userName string, password string) (string,string, error) {

	CLIENTID,CLIENTSECRET,TENANTDOMAIN=readSPConfig()

	var err error
	var accessToken string
	var refreshToken string
	var list oAuthResponse

	data := CLIENTID+":"+CLIENTSECRET
	//sEnc := b64.StdEncoding.EncodeToString([]byte(data))

	// Build response body to POST :=
	body :=url.Values{}
	body.Set("grant_type","password")
	body.Set("username",userName)
	body.Set("password", password)
	body.Set("scope", SCOPE)
	body.Set("user", data)

	req, err := http.NewRequest("POST", AUTHURL,strings.NewReader(body.Encode()))
	req.SetBasicAuth(CLIENTID,CLIENTSECRET)
	req.Header.Set("Content-Type","application/x-www-form-urlencoded")
	defer req.Body.Close()

	httpClient := &http.Client{
		Transport: &http.Transport{
			                      TLSClientConfig: &tls.Config{
				                  InsecureSkipVerify: true,
				},
		},
	}

	resp, err := httpClient.Do(req)
	if err != nil {
		log.Fatalln(err)
		return accessToken,refreshToken, err
	}
	defer resp.Body.Close()

	body1, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		log.Fatalln(err)
	}

	err2 := json.Unmarshal(body1, &list)
	if err2!=nil{
		log.Fatalln(err2)
	}

	accessToken = list.AccessToken

	refreshToken= list.RefreshToken
	return accessToken,refreshToken, err
}



