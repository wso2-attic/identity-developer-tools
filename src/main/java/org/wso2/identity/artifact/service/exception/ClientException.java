package org.wso2.identity.artifact.service.exception;

public class ClientException extends Exception {

    public ClientException() {
        super();
    }

    public ClientException(String message) {
        super(message);
    }

    public ClientException(Throwable throwable) {
        super(throwable);
    }

    public ClientException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
