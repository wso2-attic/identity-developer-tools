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

public class ArtifactRequestData {

    private String server;
    private String packageName;
    private String application;

    public String getServer() {

        return server;
    }

    public void setServer(String server) {

        this.server = server;
    }

    public String getPackageName() {

        return packageName;
    }

    public void setPackageName(String packageName) {

        this.packageName = packageName;
    }

    public String getApplication() {

        return application;
    }

    public void setApplication(String application) {

        this.application = application;
    }
}
