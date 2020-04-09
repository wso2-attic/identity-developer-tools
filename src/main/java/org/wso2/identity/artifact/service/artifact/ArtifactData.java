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
