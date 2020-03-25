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
