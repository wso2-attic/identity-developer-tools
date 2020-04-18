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

public class ArtifactData {

    private byte[] data;
    private ArtifactMetadata metadata;

    public byte[] getData() {

        return data;
    }

    public void setData(byte[] data) {

        this.data = data;
    }

    public ArtifactMetadata getMetadata() {

        return metadata;
    }

    public void setMetadata(ArtifactMetadata metadata) {

        this.metadata = metadata;
    }
}
