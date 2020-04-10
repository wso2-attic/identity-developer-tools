/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 *  Dissemination of any information or reproduction of any material
 * contained herein in any form is strictly forbidden, unless
 * permitted by WSO2 expressly. You may not alter or remove any
 * copyright or other notice from copies of this content.
 */

package org.wso2.identity.artifact.service.artifact;

public class ArtifactMetadata {

    private String path;
    private String operation;

    public String getPath() {

        return path;
    }

    public void setPath(String path) {

        this.path = path;
    }

    public String getOperation() {

        return operation;
    }

    public void setOperation(String operation) {

        this.operation = operation;
    }
}
