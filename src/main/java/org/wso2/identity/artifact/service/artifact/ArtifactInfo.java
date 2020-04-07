package org.wso2.identity.artifact.service.artifact;

public class ArtifactInfo {

    public ArtifactInfo() {

        super();
    }

    public ArtifactInfo(String name) {

        this.name = name;
    }

    private String name;

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }
}
