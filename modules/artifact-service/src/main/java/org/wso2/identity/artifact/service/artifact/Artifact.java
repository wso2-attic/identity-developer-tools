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

import java.util.ArrayList;
import java.util.List;

public class Artifact {

    private ArtifactInfo artifactInfo;
    private List<ArtifactData> artifactData = new ArrayList<>();

    public ArtifactInfo getArtifactInfo() {

        return artifactInfo;
    }

    public void setArtifactInfo(ArtifactInfo artifactInfo) {

        this.artifactInfo = artifactInfo;
    }

    public List<ArtifactData> getData() {

        return artifactData;
    }

    public void setData(List<ArtifactData> artifactData) {

        this.artifactData = artifactData;
    }
}
