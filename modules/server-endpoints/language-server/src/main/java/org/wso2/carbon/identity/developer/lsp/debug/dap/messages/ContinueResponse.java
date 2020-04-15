package org.wso2.carbon.identity.developer.lsp.debug.dap.messages;

/**
 * JSON Debug ContinueResponse.
 * Response to ‘continue’ request.
 */
public class ContinueResponse extends Response {

    /**
     * If all ThreadsContinued true.
     * If this attribute is missing a value of 'true' is assumed for backward compatibility.
     */
    private Boolean allThreadsContinued;

    public ContinueResponse(String type, long seq, long requestSeq, boolean success, String command,
                            String message, Argument body) {

        super(type, seq, requestSeq, success, command, message, body);
    }

    /**
     * Gets whether allThreadsContinued.
     *
     * @return whether allThreadsContinued
     */
    public Boolean getAllThreadsContinued() {

        return allThreadsContinued;
    }

    /**
     * Sets whether allThreadsContinued.
     *
     * @param allThreadsContinued If 'allThreadsContinued' is true, a debug adapter can announce that all threads
     *                            have continued.
     */
    public void setAllThreadsContinued(Boolean allThreadsContinued) {

        this.allThreadsContinued = allThreadsContinued;
    }

}
