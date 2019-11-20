import * as express from "express";
import { Server } from "http";
import { URL } from "url";
import * as vscode from 'vscode';
export class Wso2OAuth {
	public app: express.Express;
	public server: Server;

	constructor(public port: number) {
		this.app = express();
		this.app.use(express.json(), express.urlencoded({ extended: false }));
	}

	public async StartProcess() {
		
		const host = new URL("http://localhost:8010");

		this.server = this.app.listen(this.port);
		this.app.get("/oauth", async (req, res) => {
			const axios = require('axios');
			try {
				const clientID = '0aT4RyRSYOqsBKwsbmHsIcYwq1ca';
				const clientSecret = 'VffkV69lSXw1FysVJYojxshSi9Ea';
				let data = clientID + ':' + clientSecret;
				let buff = new Buffer(data);
				let base64data = buff.toString('base64');
				const requestToken = req.query.code;
				console.log(requestToken);
				process.env["NODE_TLS_REJECT_UNAUTHORIZED"] = "0";
				axios({
					// make a POST request
					method: 'post',
					// to the Github authentication API, with the client ID, client secret
					// and request token

					url: `https://localhost:9443/oauth2/token?grant_type=authorization_code&code=${requestToken}&redirect_uri=http://localhost:8010/oauth`,
					// Set the content type header, so that we get the response in JSOn
					headers: {
						Authorization: 'Basic ' + base64data,
						accept: 'application/json'						

					}
				}).then((response) => {
					// Once we get the response, extract the access token from
					// the response body
					console.log("access token is"+response.data.access_token);	
					vscode.workspace.getConfiguration().update("IAM.acessToken", response.data.access_token);				
				}).catch((err) => {
					// Do somthing
					console.log(err);
				});
				res.send(`
        <!doctype html>
        <html lang="en">
          <head>
            <meta charset="utf-8">
            <meta
              http-equiv="Content-Security-Policy"
              content="default-src vscode-resource:; form-action vscode-resource:; frame-ancestors vscode-resource:; img-src vscode-resource: https:; script-src 'self' 'unsafe-inline' vscode-resource:; style-src 'self' 'unsafe-inline' vscode-resource:;"
            />
            <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
          </head>
          <body>
              <h1>Success! You may now close this tab.</h1>
              <style>
                html, body {
                  background-color: #1a1a1a;
                  color: #c3c3c3;
                  display: flex;
                  justify-content: center;
                  align-items: center;
                  height: 100%;
                  width: 100%;
                  margin: 0;
                }
              </style>
          </body>
        </html>
        `);
				this.server.close();
			} catch (err) {
				const error = new Error(err);
				console.log(error);				
			}
		});
	}
}
