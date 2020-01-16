package cmd

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"os"
)

var dir, _ = os.Getwd()
var path = dir+"/iamctl.json"

type ServerDetails struct {
	Server  string `json:"server"`
	AccessToken string`json:"accessToken"`
	RefreshToken   string `json:"refreshToken"`
}
type myJSON struct {
	Array []ServerDetails
}
func createFile() {
	// detect if file exists
	var _, err = os.Stat(path)
	// create file if not exists
	if os.IsNotExist(err) {
		var file, err = os.Create(path)
		checkError(err)
		defer file.Close()

		jsondat := &myJSON{Array: []ServerDetails{}}
		encjson, _ := json.Marshal(jsondat)

		if err != nil {
			log.Println(err)
		}
		err = ioutil.WriteFile(path, encjson, 0644)
		if err!=nil{
			log.Fatalln(err)
		}
	}
}

func writeFiles(server string,token string,refreshToken string) {
	var err error
	var data myJSON
	var msg=new(ServerDetails)

	file, err := ioutil.ReadFile(path)
	if err!=nil{
		log.Fatalln(err)
	}

	err= json.Unmarshal(file, &data)
	if err!=nil{
		log.Fatalln(err)
	}

	msg.AccessToken=token
	msg.Server=server
	msg.RefreshToken=refreshToken

	if len(data.Array)==0{
		data.Array = append(data.Array, *msg)
	}else {
		for i := 0; i < len(data.Array); i++ {
			if data.Array[i].Server == server {
				data.Array[i].AccessToken = token
				data.Array[i].RefreshToken = refreshToken
			} else {

				data.Array = append(data.Array, *msg)
			}
		}
	}

	jsonData, err := json.Marshal(data)
		if err != nil {
			log.Fatalln(err)
		}
		err = ioutil.WriteFile(path, jsonData, 0644)
		if err!=nil{
			log.Fatalln(err)
		} else{
			fmt.Println("Authorization is done for : "+server)
		}

	checkError(err)
}

func readFile(domain string) string {

	var a ServerDetails
	var data myJSON

	file,err := ioutil.ReadFile(path)
	if err!=nil{
		log.Fatalln(err)
	}

	err= json.Unmarshal(file, &data)
	if err!=nil{
		log.Fatalln(err)
	}

	for i := 0; i < len(data.Array); i++ {
		if domain == data.Array[i].Server {
			a = data.Array[i]
		}
	}
	return a.AccessToken
}

//func deleteFile() bool{
//	// delete file
//	var err = os.Remove(path)
//	return (err != nil)
//
//}
func checkError(err error) {
	if err != nil {
		fmt.Println(err.Error())
		os.Exit(0)
	}
}