package org.wso2.identity.artifact.service.model;

public class Response<T> {

    private boolean error;
    private String errorMessage;

    private T data;

    public Response(T data) {
        this.data = data;
    }

    public Response(String errorMessage) {
        this.errorMessage = errorMessage;
        this.error = true;
    }

    public T getResponseData() {
        return data;
    }

    public boolean isError() {

        return error;
    }

    public void setError(boolean error) {

        this.error = error;
    }

    public String getErrorMessage() {

        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {

        this.errorMessage = errorMessage;
    }
}
