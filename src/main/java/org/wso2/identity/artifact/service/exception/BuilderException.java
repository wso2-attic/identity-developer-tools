package org.wso2.identity.artifact.service.exception;

public class BuilderException extends Exception {

    public BuilderException() {
        super();
    }

    public BuilderException(String message) {
        super(message);
    }

    public BuilderException(Throwable throwable) {
        super(throwable);
    }

    public BuilderException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
