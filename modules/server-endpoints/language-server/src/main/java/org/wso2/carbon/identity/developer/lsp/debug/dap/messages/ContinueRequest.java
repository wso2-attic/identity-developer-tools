package org.wso2.carbon.identity.developer.lsp.debug.dap.messages;

import java.util.List;

public class ContinueRequest extends Request {

    public ContinueRequest(String type, long seq, String command, List<Argument> arguments) {
        super(type, seq, command, arguments);
    }
}
