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
	"github.com/AlecAivazis/survey/v2"
	"github.com/mbndr/figlet4go"
	"github.com/spf13/cobra"
	"log"
	"net/url"
)

var createSPCmd = &cobra.Command{
	Use:   "application",
	Short: "Create a service provider",
	Long: `This will help you to create the service providers`,
	Run: func(cmd *cobra.Command, args []string) { create() },}

var qs = []*survey.Question{
	{
		Name: "question",
		Prompt: &survey.Select{
			Message: "Select the option to move on:",
			Options: []string{"Add application", "Get List" , "Exit"},
			Default: "Add application",
		},
	},
}
var types = []*survey.Question{
	{
		Name: "type",
		Prompt: &survey.Select{
			Message: "Select the configuration type:",
			Options: []string{"Basic application", "oauth"},
			Default: "Basic application",
		},
	},
}
var details = []*survey.Question{
		{
			Name:     "spName",
			Prompt:   &survey.Input{Message: "Enter service provider name:"},
			Validate: survey.Required,
			Transform: survey.Title,
		},
		{
		Name:     "spDescription",
		Prompt:   &survey.Input{Message: "Enter service provider description:"},
		Validate: survey.Required,
		},
}
var domainName=[]*survey.Question{
	{
		Name:      "domainName",
		Prompt:    &survey.Input{Message: "Enter IS URL:"},
		Validate:  survey.Required,
	},
}
var oauthDetails = []*survey.Question{
	{
		Name:      "oauthName",
		Prompt:    &survey.Input{Message: "Enter Oauth application name:"},
		Validate:  survey.Required,
		Transform: survey.Title,
	},
	{
		Name:      "callbackURls",
		Prompt:    &survey.Input{Message: "Enter callbackURLs:"},
	},
}

func init() {

	rootCmd.AddCommand(createSPCmd)
}

func create(){

	ascii := figlet4go.NewAsciiRender()
	renderStr, _ := ascii.Render(appName)
	fmt.Print(renderStr)

	answers := struct{
		Selected string `survey:"question"`
		Name string  `survey:"spName"`
		Description string `survey:"spDescription"`
	}{}
	answersOfType := struct{
		Selected string `survey:"type"`
	}{}
	domain := struct{
		Name string `survey:"domainName"`
	}{}
	answersOauth:= struct {
		Name string `survey:"oauthName"`
		CallbackURLs string `survey:"callbackURls"`
	}{}

	err := survey.Ask(domainName, &domain)
	if err != nil {
		log.Fatalln(err)
		return
	}
	_, err2 := url.ParseRequestURI(domain.Name)
	if err2 != nil {
		log.Fatalln(err2)
	}else {
		err := survey.Ask(qs, &answers)
		if err == nil && answers.Selected == "Add application" {
			err := survey.Ask(types, &answersOfType)
			if err == nil && answersOfType.Selected == "Basic application" {
				err1 := survey.Ask(details, &answers)
				if err1 != nil {
					log.Fatalln(err1)
					return
				}
				createSPBasicApplication(domain.Name, answers.Name, answers.Description)
			}
			if err == nil && answersOfType.Selected == "oauth" {
				err1 := survey.Ask(oauthDetails, &answersOauth)
				if err1 != nil {
					log.Fatalln(err)
					return
				}
				if  answersOauth.CallbackURLs == ""  {
					grantTypes := []string{"password", "client_credentials", "refresh_token"}
					createSPOauthApplication(domain.Name,answersOauth.Name, answersOauth.Name, answersOauth.CallbackURLs, grantTypes)
				}else  {
					_, err := url.ParseRequestURI(answersOauth.CallbackURLs)
					if err!=nil{
						log.Fatalln(err)
					}else {
						grantTypes := []string{"authorization_code","implicit","password","client_credentials","refresh_token"}
						createSPOauthApplication(domain.Name, answersOauth.Name, answersOauth.Name, answersOauth.CallbackURLs, grantTypes)
					}
				}
			}
		}

		if err == nil && answers.Selected == "Get List" {
			getList(domain.Name)
		}
	}
}

