/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 *  Dissemination of any information or reproduction of any material
 * contained herein in any form is strictly forbidden, unless
 * permitted by WSO2 expressly. You may not alter or remove any
 * copyright or other notice from copies of this content.
 */

package org.wso2.identity.artifact.service.artifact.builder.spring;

import com.hubspot.jinjava.Jinjava;
import org.apache.commons.lang3.StringUtils;
import org.wso2.identity.artifact.service.artifact.Artifact;
import org.wso2.identity.artifact.service.artifact.ArtifactData;
import org.wso2.identity.artifact.service.artifact.ArtifactInfo;
import org.wso2.identity.artifact.service.artifact.ArtifactMetadata;
import org.wso2.identity.artifact.service.artifact.builder.ArtifactBuilder;
import org.wso2.identity.artifact.service.model.ArtifactRequestData;
import org.wso2.identity.artifact.service.exception.BuilderException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SpringBootArtifactBuilder implements ArtifactBuilder {

    private static final String JAVA_FILE_NAME = "IndexController.java";
    private static final String PROPERTIES_FILE_NAME = "spring-security-properties.j2";
    private static final String INDEX_HTML_FILE_NAME = "index.html";
    private static final String USERINFO_HTML_FILE_NAME = "userinfo.html";
    private static final String ARTIFACT_NAME = "spring-boot";

    private static final String INDEX_HTML_PATH = "src/main/resources/templates/index.html";
    private static final String UNERINFO_HTML_PATH = "src/main/resources/templates/userinfo.html";
    private static final String PROPERTIES_FILE_PATH = "src/main/resources/application.properties";
    private static final String METADATA_COPY_OPERATION = "copy";

    private final String resourcePath;
    private ArtifactRequestData artifactRequestData;

    public SpringBootArtifactBuilder(String resourcePath) {

        this.resourcePath = resourcePath;
    }

    public void setArtifactRequestData(ArtifactRequestData artifactRequestData) {

        this.artifactRequestData = artifactRequestData;
    }

    @Override
    public Artifact build() throws BuilderException {

        Artifact artifact = new Artifact();
        ArtifactInfo artifactInfo = new ArtifactInfo();
        artifactInfo.setName(ARTIFACT_NAME);
        artifact.setArtifactInfo(artifactInfo);

        ISServerUtil isServerUtil = new ISServerUtil();
        try {
            Map<String, String> context = isServerUtil.getOAuthProperties(artifactRequestData);
            if (context == null) {
                throw new BuilderException("Error while getting application details from DCR endpoint");
            }
            artifact.getData().add(getJavaFile());
            artifact.getData().add(getPropertiesFile(context));
            artifact.getData().add(getIndexHtmlPage());
            artifact.getData().add(getUserInfoHtmlPage());
            return artifact;
        } catch (IOException e) {
            throw new BuilderException("Unable to get artifacts", e);
        }
    }

    private ArtifactData getJavaFile() throws IOException {

        Path path = Paths.get(resourcePath, JAVA_FILE_NAME);
        String template = new String(Files.readAllBytes(path));
        String packageName = artifactRequestData.getPackageName() + ".controller";
        Map<String, String> myMap = new HashMap<String, String>() {{
            put("packageName", packageName);
        }};
        Jinjava jinjava = new Jinjava();
        String renderedTemplate = jinjava.render(template, myMap);

        String packagePath = StringUtils.replace(artifactRequestData.getPackageName(), ".", "/");
        String controllerPath = "src/main/java/" + packagePath + "/controller/" + JAVA_FILE_NAME;

        return createArtifactData(renderedTemplate.getBytes(), controllerPath, METADATA_COPY_OPERATION);
    }

    private ArtifactData getIndexHtmlPage() throws IOException {

        Path path = Paths.get(resourcePath, INDEX_HTML_FILE_NAME);
        return createArtifactData(Files.readAllBytes(path), INDEX_HTML_PATH, METADATA_COPY_OPERATION);
    }

    private ArtifactData getUserInfoHtmlPage() throws IOException {

        Path path = Paths.get(resourcePath, USERINFO_HTML_FILE_NAME);
        return createArtifactData(Files.readAllBytes(path), UNERINFO_HTML_PATH, METADATA_COPY_OPERATION);
    }

    private ArtifactData getPropertiesFile(Map<String, String> context) throws IOException {

        Path path = Paths.get(resourcePath, PROPERTIES_FILE_NAME);
        String template = new String(Files.readAllBytes(path));
        Jinjava jinjava = new Jinjava();
        String renderedTemplate = jinjava.render(template, context);
        return createArtifactData(renderedTemplate.getBytes(), PROPERTIES_FILE_PATH, METADATA_COPY_OPERATION);
    }

    private ArtifactData createArtifactData(byte[] data, String path, String operation) {

        ArtifactData artifactData = new ArtifactData();
        artifactData.setData(data);
        artifactData.setMetadata(createMetaData(path, operation));
        return artifactData;
    }

    private ArtifactMetadata createMetaData(String path, String operation) {

        ArtifactMetadata metadata = new ArtifactMetadata();
        metadata.setPath(path);
        metadata.setOperation(operation);
        return metadata;
    }
}
