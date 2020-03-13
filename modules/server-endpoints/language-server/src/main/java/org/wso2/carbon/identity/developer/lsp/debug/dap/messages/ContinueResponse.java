package org.wso2.carbon.identity.developer.lsp.debug.dap.messages;

public class ContinueResponse extends Response {
    /**
     * If allThreadsContinued true,
     * the 'continue' request has ignored the specified thread and continued all threads instead.
     * If this attribute is missing a value of 'true' is assumed for backward compatibility.
     */
    private  Boolean allThreadsContinued;

    public ContinueResponse(String type, long seq, long requestSeq, boolean success, String command, String message, Argument body) {

        super(type, seq, requestSeq, success, command, message, body);
    }

    public Boolean getAllThreadsContinued() {

        return allThreadsContinued;
    }

    public void setAllThreadsContinued(Boolean allThreadsContinued) {

        this.allThreadsContinued = allThreadsContinued;
    }

}
