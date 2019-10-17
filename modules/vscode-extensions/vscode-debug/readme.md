# WSO2 Identity Server Debug

WSO2 Identity server debug support allows you to trace the following
* Login Flow - Each Step in the multi step, MFA
* Adaptive Authentication script execution, line by line.
* To introspect the variables in inbpund authentication request and its response

**WSO2 IAM Debug** uses the Deebug adapter for Visual Studio Code.
It supports *step*, *continue*, *breakpoints*, *exceptions*, and
*variable access* when it connected to the running Identity Server instance started with *Dev* profile.


How the debugger and the debug adapter is developped can be found
[here](https://code.visualstudio.com/docs/extensions/example-debuggers).
Or discuss debug adapters on Gitter:
[![Gitter Chat](https://img.shields.io/badge/chat-online-brightgreen.svg)](https://gitter.im/Microsoft/vscode)

## Using WSO2 IS Debug

* Install the **WSO2 IS Debug** extension in VS Code.
* Connect the running *Identity Server* Instance+Tenant with your credentials
* Switch to the debug viewlet and press the gear dropdown.
* Select the debug environment "WSO2 IS Debug".
* Press the green 'play' button to start debugging.

You can now 'step through' the `Authentication Flow` or `Authentication Script` set and hit breakpoints

![Mock Debug](images/iam-debug.gif)

## Build and Run

* npm install
* Open the project folder in VS Code.
* Press `F5` to build and launch Mock Debug in another VS Code window. In that window:
  * Open a new workspace, Connect to your `Identity Server`.
  * Switch to the debug viewlet and press the gear dropdown.
  * Select the debug environment "WSO2 IS Debug".
  * Press `F5` to start debugging.
