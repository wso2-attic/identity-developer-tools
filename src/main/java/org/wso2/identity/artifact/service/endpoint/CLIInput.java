package org.wso2.identity.artifact.service.endpoint;

public class CLIInput {

    private String server;
    private String packageName;
    private String sp;

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

    public String getSp() {

        return sp;
    }

    public void setSp(String sp) {

        this.sp = sp;
    }
}
