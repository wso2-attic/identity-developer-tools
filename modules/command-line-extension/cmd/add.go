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
			}else {
				createSPBasicApplication(domain, name, description)
			}
		}else {
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
var name1 string

func init(){
	createSPCmd.AddCommand(createUsingCommand)
	createUsingCommand.Flags().StringP("type", "t", "oauth", "Enter application type")
	name:=createUsingCommand.Flags()
	name.StringVarP(&name1,"name", "n", "", "name of service provider - **compulsory")
	err := cobra.MarkFlagRequired(name, "name")
	if err!= nil{
		fmt.Println(err)
	}

	createUsingCommand.Flags().StringP("description", "d", "", "description of SP - **for basic application")
	createUsingCommand.Flags().StringP("callbackURl", "c", "", "callbackURL  of SP - **for oauth application")

	serverdomain:=createUsingCommand.Flags()
	serverdomain.StringVarP(&serverDomain,"serverDomain", "s", "", "server Domain - **compulsory")
	err2 := cobra.MarkFlagRequired(serverdomain, "serverDomain")
	if err2 != nil{
		fmt.Println(err2)
	}
}

