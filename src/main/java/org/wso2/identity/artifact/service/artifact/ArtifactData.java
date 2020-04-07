package org.wso2.identity.artifact.service.artifact;

public class ArtifactData {

    private String type;
    private byte[] data;
    private String path;

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    public byte[] getData() {

        return data;
    }

    public void setData(byte[] data) {

        this.data = data;
    }

    public String getPath() {

        return path;
    }

    public void setPath(String path) {

        this.path = path;
    }
}
