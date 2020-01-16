package cmd

import (
	"bytes"
	"crypto/tls"
	"encoding/json"
	"fmt"
	"github.com/AlecAivazis/survey/v2"
	"github.com/mbndr/figlet4go"
	"github.com/spf13/cobra"
	"io/ioutil"
	"net/http"
	"net/url"
	"os"
	"text/tabwriter"
)
var getListCmd = &cobra.Command{
	Use:  "list" ,
	Short: "get service providers List",
	Long: `You can get service provider List`,
	Run: func(cmd *cobra.Command, args []string) {
		//server, _ :=cmd.Flags().GetString("domainName")
		server1, _ := cmd.Flags().GetString("server")
		if  server1==""  {
			var server = []*survey.Question{
				{
					Name:     "server",
					Prompt:   &survey.Input{Message: "Enter IAM URL:"},
					Validate: survey.Required,
				},

			}
			ascii := figlet4go.NewAsciiRender()
			renderStr, _ := ascii.Render(appName)
			fmt.Print(renderStr)

			serverAnswer := struct{
				Server string `survey:"server"`
			}{}

			err1 := survey.Ask(server, &serverAnswer)
			_, err := url.ParseRequestURI(serverAnswer.Server)
			if err != nil {
				fmt.Println(err)
			}
			if err1 != nil {
				fmt.Println(err1.Error())
				return
			}
			getList(serverAnswer.Server)

		}else if  server1!=""{
			_, err := url.ParseRequestURI(server1)
			if err != nil {
				fmt.Println(err)
			}
			getList(server1)
		}
	},
}

type List struct{
	TotalResults int `json:"totalResults"`
	StartIndex int `json:"startIndex"`
	Count int `json:"count"`
	Applications []Application `json:"applications"`
	Links []string `json:"links"`
}
type Application struct{
	Id          string `json:"id"`
	Name        string `json:"name"`
	Description string `json:"description"`
	Self        string `json:"self"`
}

func init(){
	createSPCmd.AddCommand(getListCmd)

	getListCmd.Flags().StringP("server", "s", "", "server")
}
func getList(domainName string){
	CLIENTID,CLIENTSECRET,TENANTDOMAIN=readSPConfig()

	var GETLISTURL =domainName+"/t/"+TENANTDOMAIN+"/api/server/v1/applications"
	var status int
	token := readFile(domainName)

	http.DefaultTransport.(*http.Transport).TLSClientConfig = &tls.Config{InsecureSkipVerify: true}

	req, err := http.NewRequest("GET", GETLISTURL,bytes.NewBuffer(nil))
	req.Header.Set("Authorization", "Bearer "+token)
	req.Header.Set("accept", "*/*")
	defer req.Body.Close()

	httpClient := &http.Client{}
	resp, err := httpClient.Do(req)
	if err != nil {
		panic(err)
	}

	status = resp.StatusCode
	defer resp.Body.Close()

	if status == 401 {
		fmt.Println("Unauthorized access. \n Please enter your Username and password for server.")

	}
	if status == 400 {
		fmt.Println("Bad Request")
	}
	if status == 200{
		fmt.Println("Successfully Got the service provider List at "+resp.Header.Get("Date"))
		body, err := ioutil.ReadAll(resp.Body)
		if err != nil {
			panic(err.Error())
		}
		writer := new(tabwriter.Writer)
		writer.Init(os.Stdout, 8, 8, 0, '\t', 0)
		defer writer.Flush()
		var list List
		var app Application
		_ = json.Unmarshal(body, &list)
		_, _ = fmt.Fprintf(writer, "\n %s\t%s\t%s\t", "Application Id ","Name", "Description")
		_, _ = fmt.Fprintf(writer, "\n %s\t%s\t%s\t", " ----", "----", "----", )
		for i := 0; i < len(list.Applications); i++ {

			app.Id=list.Applications[i].Id
			app.Name=list.Applications[i].Name
			app.Description=list.Applications[i].Description
			_, _ = fmt.Fprintf(writer, "\n %s\t%s\t%s\t", app.Id, app.Name, app.Description)
		}
		_ = resp.Body.Close()
	}
	if status == 403 {
		fmt.Println("Forbidden")
	}
	if status == 404 {
		fmt.Println("Not Found")
	}
	if status == 500 {
		fmt.Println("Server Error")
	}
	if status == 501 {
		fmt.Println("Not Implemented")
	}
}
