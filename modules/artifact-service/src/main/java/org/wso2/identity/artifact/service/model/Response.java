/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 *  Dissemination of any information or reproduction of any material
 * contained herein in any form is strictly forbidden, unless
 * permitted by WSO2 expressly. You may not alter or remove any
 * copyright or other notice from copies of this content.
 */

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
