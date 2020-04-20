package org.wso2.carbon.identity.developer.lsp.debug.dap.messages;

import java.util.List;

/**
 * A ContinueRequest request for the debug protocol.
 * The request starts the debuggee to run again.
 */
public class ContinueRequest extends Request {

    public ContinueRequest(String type, long seq, String command, List<Argument> arguments) {

        super(type, seq, command, arguments);
    }
}
