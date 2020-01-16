
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
		Validate:  survey.Required,
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
	answers1 := struct{
		Selected string `survey:"type"`
	}{}
	domain := struct{
		Name string `survey:"domainName"`
	}{}
	answersOauth:= struct {
		Name string `survey:"oauthName"`
		CallbackURLs string `survey:"callbackURls"`
	}{}

	err1 := survey.Ask(domainName, &domain)
	if err1 != nil {
		fmt.Println(err1.Error())
		return
	}
	_, err2 := url.ParseRequestURI(domain.Name)
	if err2 != nil {
		fmt.Println(err2)

	}else {
		err := survey.Ask(qs, &answers)
		if err == nil && answers.Selected == "Add application" {
			err := survey.Ask(types, &answers1)
			if err == nil && answers1.Selected == "Basic application" {
				err1 := survey.Ask(details, &answers)
				if err1 != nil {
					fmt.Println(err1.Error())
					return
				}
				createSPBasicApplication(domain.Name, answers.Name, answers.Description)
			}
			if err == nil && answers1.Selected == "oauth" {
				err1 := survey.Ask(oauthDetails, &answersOauth)
				if err1 != nil {
					log.Fatalln(err)
					return
				}
				_, err := url.ParseRequestURI(answersOauth.CallbackURLs)
				if err != nil {
					log.Fatalln(err)
				}else {
					if answersOauth.CallbackURLs == "" {
						grantTypes:=[]string{"password","client_credentials","refresh_token"}

						createSPOauthApplication(domain.Name,domain.Name, answers.Name,answersOauth.CallbackURLs, grantTypes)

					} else {
						grantTypes:=[]string{"authorization_code","implicit","password","client_credentials","refresh_token"}

						createSPOauthApplication(domain.Name,domain.Name, answers.Name, answersOauth.CallbackURLs,grantTypes)
					}
				}
			}
		}
		if err == nil && answers.Selected == "Get List" {
			getList(domain.Name)
		}
	}
}

