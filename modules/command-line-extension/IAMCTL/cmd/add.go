/*
Copyright © 2020 NAME HERE <EMAIL ADDRESS>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package cmd

import (
	"fmt"
	"github.com/spf13/cobra"
	"log"
	"net/url"
)

var createUsingCommand = &cobra.Command{
	Use:   "add",
	Short: "create SP using commands ",
	Long: `A brief description about use flags to service provider `,
	Run: func(cmd *cobra.Command, args []string) {
		typeOfAPP, _ := cmd.Flags().GetString("type")
		name, _ :=cmd.Flags().GetString("name")
		description, _ :=cmd.Flags().GetString("description")
		domain,_:=cmd.Flags().GetString("serverDomain")
		_, err = url.ParseRequestURI(domain)
		if err != nil {
			log.Fatalln(err)
		}

		if typeOfAPP=="basic" {
			if  description==""{
				createSPBasicApplication(domain, name, name)
			}else{
				createSPBasicApplication(domain, name, description)
			}
		}else{
				callbackURl, _ :=cmd.Flags().GetString("callbackURl")

				if callbackURl == "" {
					grantTypes:=[]string{"password","client_credentials","refresh_token"}
					if description!=""{
						createSPOauthApplication(domain, name,description,callbackURl, grantTypes)
					}else{
						createSPOauthApplication(domain, name,description,callbackURl, grantTypes)
					}
				} else {
					grantTypes:=[]string{"authorization_code","implicit","password","client_credentials","refresh_token"}
					_, err := url.ParseRequestURI(callbackURl)
					if err != nil{
						log.Fatalln(err)
					}else {
						if description != "" {
							createSPOauthApplication(domain, name, description, callbackURl, grantTypes)
						} else {
							createSPOauthApplication(domain, name, description, callbackURl, grantTypes)
						}
					}
				}
		}
	},
}

var serverDomain string
var applicationName string

func init(){
	createSPCmd.AddCommand(createUsingCommand)

	createUsingCommand.Flags().StringP("type", "t", "oauth", "Enter application type")
	name:=createUsingCommand.Flags()
	name.StringVarP(&applicationName,"name", "n", "", "name of service provider - **compulsory")
	err := cobra.MarkFlagRequired(name, "name")
	if err!= nil{
		fmt.Println(err)
	}
	createUsingCommand.Flags().StringP("description", "d", "", "description of SP - **for basic application")
	createUsingCommand.Flags().StringP("callbackURl", "c", "", "callbackURL  of SP - **for oauth application")
	server:=createUsingCommand.Flags()
	server.StringVarP(&serverDomain,"serverDomain", "s", "", "server Domain - **compulsory")
	err = cobra.MarkFlagRequired(server, "serverDomain")
	if err != nil{
		log.Fatalln(err)
	}
}

