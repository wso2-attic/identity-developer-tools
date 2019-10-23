/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */

import * as path from 'path';
import { workspace, ExtensionContext } from 'vscode';
import * as vscode from 'vscode';
import {
	LanguageClient,
	LanguageClientOptions,
	ServerOptions,
	TransportKind
} from 'vscode-languageclient';

let client: LanguageClient;

export function activate(context: ExtensionContext) {
	// The server is implemented in node
	let serverModule = context.asAbsolutePath(
		path.join('server', 'out', 'server.js')
	);
	// The debug options for the server
	// --inspect=6009: runs the server in Node's Inspector mode so VS Code can attach to the server for debugging
	let debugOptions = { execArgv: ['--nolazy', '--inspect=6009'] };

	// If the extension is launched in debug mode then the debug server options are used
	// Otherwise the run options are used
	let serverOptions: ServerOptions = {
		run: { module: serverModule, transport: TransportKind.ipc },
		debug: {
			module: serverModule,
			transport: TransportKind.ipc,
			options: debugOptions
		}
	};

	// Options to control the language client
	let clientOptions: LanguageClientOptions = {
		// Register the server for plain text documents
		documentSelector: [{ scheme: 'file', language: 'IAM' }],
		synchronize: {
			// Notify the server about file changes to '.clientrc files contained in the workspace
			fileEvents: workspace.createFileSystemWatcher('**/.clientrc')
		}
	};

	context.subscriptions.push(
		vscode.commands.registerCommand('extension.diagram', () => {
			// Create and show panel				
			// var doc = vscode.window.activeTextEditor.document;
			// var untitledFile = doc.uri.with({
			// 	scheme: 'untitled',
			// 	path: ''
			// });
			// vscode.workspace.openTextDocument(untitledFile);
			const { activeTextEditor } = vscode.window;
			const { document } = activeTextEditor;
			const Code = document.getText();
			// vscode.window.showInformationMessage(String(Code));
			const panel = vscode.window.createWebviewPanel(
				'Diagram',
				'Show Diagram',
				vscode.ViewColumn.Two,
				{
					enableScripts: true,
					retainContextWhenHidden: true

				}
			);
			// And set its HTML content
			panel.webview.html = getWebviewContent(Code);



		})
	);



	// Create the language client and start the client.
	client = new LanguageClient(
		'languageServerExample',
		'Language Server Example',
		serverOptions,
		clientOptions
	);


	// Start the client. This will also launch the server
	client.start();
}

export function deactivate(): Thenable<void> | undefined {
	if (!client) {
		return undefined;
	}
	return client.stop();
}

function getWebviewContent(code) {
	return "<html lang=\"en\">\n" +
		"\n" +
		"<head>\n" +
		"    <script src=\"https://code.jquery.com/jquery-3.4.1.js\"></script>\n" +
		"    <script src=\"https://cdnjs.cloudflare.com/ajax/libs/jsPlumb/2.12.0/js/jsplumb.min.js\"></script>\n" +
		"\n" +
		"    <meta charset=\"UTF-8\">\n" +
		"\n" +
		"    <title>Cat Coding</title>\n" +
		"    <style>\n" +
		"        .item {\n" +
		"            height: auto;\n" +
		"            width: 300px;\n" +
		"            border-radius: 40px;\n" +
		"            border: 1px solid rgb(202, 202, 245);\n" +
		"            float: left;\n" +
		"        }\n" +
		"\n" +
		"        .inner {\n" +
		"            height: auto;\n" +
		"            width: 100px;\n" +
		"            border-radius: 20px;\n" +
		"            margin-left: 30px;\n" +
		"            border: 1px solid rgb(245, 187, 187);\n" +
		"            float: left;\n" +
		"        }\n" +
		"\n" +
		"        .block {\n" +
		"            height: auto;\n" +
		"            width: 100%;\n" +
		"            margin-bottom: 30px;\n" +
		"            float: left;            \n" +
		"        }\n" +
		"\n" +
		"        .item_start {\n" +
		"            height: auto;\n" +
		"            width: 80px;\n" +
		"            margin-left: 110px;\n" +
		"            border-radius: 40px;\n" +
		"            border: 1px solid rgb(165, 165, 197);\n" +
		"            float: left;\n" +
		"        }\n" +
		"\n" +
		"        .ds {\n" +
		"            width: 100px;\n" +
		"            height: 100px;\n" +
		"            border: 1px solid brown;\n" +
		"            float: left;\n" +
		"            margin-left: 50px;\n" +
		"        }\n" +
		"\n" +
		"        .down {\n" +
		"            width: 100%;\n" +
		"            height: auto;\n" +
		"            float: left;\n" +
		"        }\n" +
		"\n" +
		"        .itemin {\n" +
		"            margin-top: 10px;            \n" +
		"            border: 2px pink solid;\n" +
		"            width: 300px;\n" +
		"            border-radius: 40px;\n" +
		"            height: auto;\n" +
		"            float: left;\n" +
		"        }\n" +
		"\n" +
		"        .body {\n" +
		"            margin-left: 20%;\n" +
		"            position: fixed;\n" +
		"        }\n" +
		"\n" +
		"        .txt {\n" +
		"            color: rgb(253, 251, 251);\n" +
		"            font-size: 11;\n" +
		"        }\n" +
		"\n" +
		"        .circleText {\n" +
		"            margin-top:20px;\n" +
		"            margin-left:20px\n" +
		"        }\n" +
		"    </style>\n" +
		"\n" +
		"</head>\n" +
		"\n" +
		"<body class=\"body\">\n" +
		"    <p id=\"a\"></p>\n" +
		"    <div class=\"block\">\n" +
		"        <div id=\"start\" class=\"item_start\">\n" +
		"            <p class=\"circleText\">Start </p>\n" +
		"        </div>\n" +
		"    </div>\n" +
		"\n" +
		"    <div class=\"block\">\n" +
		"        <div id=\"item_1\" class=\"item\"><span class=\"txt\">Inbound Authentication Config</span> \n" +
		"\n" +
		"        </div>\n" +
		"    </div>\n" +
		"\n" +
		"    <div class=\"block\">\n" +
		"        \n" +
		"        <div id=\"item_input\" class=\"itemin\"><span class=\"txt\">Local And OutBound Authentication Config</span> \n" +
		"           \n" +
		"        </div>\n" +
		"    </div>\n" +
		"\n" +
		"    <div class=\"block\">\n" +
		"        <div id=\"stop\" class=\"item_start\">\n" +
		"            <p class=\"circleText\">Stop </p>\n" +
		"        </div>\n" +
		"    </div>\n" +
		"    \n" +
		"</body>\n" +
		"<script>\n" +
		"    \n" +
		"    $(document).ready(function () {  \n" +
		"       var text =\`"+String(code)+"\`;"+
		"        xmlDoc = $.parseXML(text);\n" +
		"        $xml = $(xmlDoc);\n" +
		"\n" +
		"        x = document.getElementById(\"item_1\");\n" +
		"        y = document.getElementById(\"item_input\");\n" +
		"        $xml.find(\"InboundAuthenticationConfig\").find(\"InboundAuthenticationRequestConfigs\").find(\"InboundAuthenticationRequestConfig\").each(function () {\n" +
		"\n" +
		"            var $row = $(this),\n" +
		"                name = $row.find(\"column1\").text(),\n" +
		"                job = $row.find(\"InboundAuthType\").text();\n" +
		"            x.innerHTML += ' <div class=\"block\"><div id=\"item_1\" class=\"inner\"><span class=\"txt\">' + job + '</span></div></div>'\n" +
		"\n" +
		"        });\n" +
		"\n" +
		"        $xml.find(\"LocalAndOutBoundAuthenticationConfig\").find(\"AuthenticationSteps\").find(\"AuthenticationStep\").each(function () {\n" +
		"\n" +
		"            var $row = $(this),\n" +
		"                name = $row.find(\"column1\").text(),\n" +
		"                job = $row.find(\"LocalAuthenticatorConfigs\").find(\"Name\").text();\n" +
		"            y.innerHTML += '<div class=\"block\"> <div id=\"item_1\" class=\"inner\"><span class=\"txt\">' + job + '</span> </div></div>';\n" +
		"        });\n" +
		"\n" +
		"        var code = $xml.find(\"LocalAndOutBoundAuthenticationConfig\").find(\"AuthenticationScript\").text()\n" +
		"\n" +
		"        if(code.length >0){\n" +
		"            z= document.getElementById(\"item_input\");\n" +
		"            z.innerHTML += ' <button style=\"margin-right:180px;\">Script</button>';\n" +
		"        }\n" +
		"        \n" +
		"        \n" +
		"        \n" +
		"\n" +
		"        jsPlumb.ready(function () {\n" +
		"            /*First Instance*/\n" +
		"            var zeroInstance = jsPlumb.getInstance();\n" +
		"\n" +
		"            zeroInstance.importDefaults({\n" +
		"                ConnectionsDetachable: false,\n" +
		"                Connector: [\"Straight\", {\n" +
		"                    curviness: 150\n" +
		"                }\n" +
		"\n" +
		"                ],\n" +
		"                Anchors: [\"BottomCenter\", \"TopCenter\"]\n" +
		"            }\n" +
		"\n" +
		"            );\n" +
		"\n" +
		"            zeroInstance.connect({\n" +
		"                ConnectionsDetachable: false,\n" +
		"                source: \"start\",\n" +
		"                target: \"item_1\",\n" +
		"                scope: \"someScope\"\n" +
		"            }\n" +
		"\n" +
		"            );\n" +
		"\n" +
		"            var firstInstance = jsPlumb.getInstance();\n" +
		"\n" +
		"            firstInstance.importDefaults({\n" +
		"                ConnectionsDetachable: false,                \n" +
		"                Connector: [\"Straight\", {\n" +
		"                    curviness: 20,\n" +
		"                    stub:12\n" +
		"                }\n" +
		"\n" +
		"                ],\n" +
		"                Anchors: [\"BottomCenter\", \"TopCenter\"]\n" +
		"            }\n" +
		"\n" +
		"            );\n" +
		"\n" +
		"\n" +
		"            firstInstance.connect({\n" +
		"                source: \"item_1\",\n" +
		"                target: \"item_input\",\n" +
		"                scope: \"someScope\"\n" +
		"            }\n" +
		"\n" +
		"            );\n" +
		"\n" +
		"            /*Second Instance*/\n" +
		"            var secondInstance = jsPlumb.getInstance();\n" +
		"\n" +
		"            secondInstance.importDefaults({\n" +
		"                ConnectionsDetachable: false,\n" +
		"                Connector: [\"Straight\", {\n" +
		"                    curviness: 150\n" +
		"                }\n" +
		"\n" +
		"                ],\n" +
		"                Anchors: [\"BottomCenter\", \"TopCenter\"]\n" +
		"            }\n" +
		"\n" +
		"            );\n" +
		"\n" +
		"            secondInstance.connect({\n" +
		"                source: \"item_input\",\n" +
		"                target: \"stop\",\n" +
		"                scope: \"someScope\"\n" +
		"            }\n" +
		"\n" +
		"            );\n" +
		"        }\n" +
		"\n" +
		"        );\n" +
		"\n" +
		"\n" +
		"    })\n" +
		"\n" +
		"</script>\n" +
		"\n" +
		"</html>";
}