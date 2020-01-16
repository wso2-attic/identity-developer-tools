package cmd

import (
	"fmt"
	"github.com/AlecAivazis/survey/v2"
	"github.com/mbndr/figlet4go"
	"github.com/spf13/cobra"
	"log"
	"net/url"
)


var configCmd = &cobra.Command{
	Use:  "serverConfiguration" ,
	Short: "you can set your server domain",
	Long: `You can set your server domain`,
	Run: func(cmd *cobra.Command, args []string) {
		server1, _ := cmd.Flags().GetString("server")

		if server1 =="" {
			setServer()
		}else {
			_, err := url.ParseRequestURI(server1)
			if err != nil {
				fmt.Println(err)
			}


			userName, _ := cmd.Flags().GetString("username")
			password, _ := cmd.Flags().GetString("password")
			start(server1,userName,password)
		}
	},
}

var server = []*survey.Question{
	{
		Name:     "server",
		Prompt:   &survey.Input{Message: "Enter IAM URL:"},
		Validate: survey.Required,
	},
}
var userNamePassword = []*survey.Question{
	{
		Name:     "username",
		Prompt:   &survey.Input{Message: "Enter Username:"},
		Validate: survey.Required,
	},
	{
		Name:     "password",
		Prompt:   &survey.Password{Message: "Enter Password:"},
		Validate: survey.Required,
	},
}
func init(){
	rootCmd.AddCommand(configCmd)
	configCmd.Flags().StringP("server", "s", "", "set server domain")
	configCmd.Flags().StringP("username", "u", "", "enter your username")
	configCmd.Flags().StringP("password", "p", "", "enter your password")

}

func setServer(){
	ascii := figlet4go.NewAsciiRender()
	renderStr, _ := ascii.Render(appName)
	fmt.Print(renderStr)

	serverAnswer := struct{
		Server string `survey:"server"`
	}{}
	err1 := survey.Ask(server, &serverAnswer)
	if err1 != nil {
		log.Fatal(err1)
		return
	}
	_, err := url.ParseRequestURI(serverAnswer.Server)
	if err != nil {
		fmt.Println(err)
	}

	userNamePasswordAnswer:= struct {
		UserName  string `survey:"username"`
		Password   string `survey:"password"`
	}{}
	err1 = survey.Ask(userNamePassword, &userNamePasswordAnswer)
	if err1 != nil {
		log.Fatal(err1)
		return
	}
	start(serverAnswer.Server,userNamePasswordAnswer.UserName,userNamePasswordAnswer.Password)


}
