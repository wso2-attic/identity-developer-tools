package org.wso2.identity.artifact.service.artifact.builder.spring;

import com.hubspot.jinjava.Jinjava;
import org.wso2.identity.artifact.service.artifact.Artifact;
import org.wso2.identity.artifact.service.artifact.ArtifactData;
import org.wso2.identity.artifact.service.artifact.ArtifactInfo;
import org.wso2.identity.artifact.service.artifact.builder.ArtifactBuilder;
import org.wso2.identity.artifact.service.exception.BuilderException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class SpringBootArtifactBuilder implements ArtifactBuilder {

    private static final String JAVA_FILE_NAME = "OIDCAppRegistration.java";
    private static final String PROPERTIES_FILE_NAME = "spring-security-properties.j2";
    private static final String ARTIFACT_NAME = "spring-boot";
    private final String resourcePath;

    public SpringBootArtifactBuilder(String resourcePath) {

        this.resourcePath = resourcePath;
    }

    private ArtifactData getJavaFile() throws IOException {

        Path path = Paths.get(resourcePath, JAVA_FILE_NAME);
        ArtifactData artifactData = new ArtifactData();
        artifactData.setData(Files.readAllBytes(path));
        return artifactData;
    }

    private ArtifactData getPropertiesFile(Map<String, String> context) throws IOException {

        Path path = Paths.get(resourcePath, PROPERTIES_FILE_NAME);

        String template = new String(Files.readAllBytes(path));
        Jinjava jinjava = new Jinjava();

        String renderedTemplate = jinjava.render(template, context);
        ArtifactData artifactData = new ArtifactData();
        artifactData.setData(renderedTemplate.getBytes());
        return artifactData;
    }

    @Override
    public Artifact build() throws BuilderException {

        Artifact artifact = new Artifact();
        ArtifactInfo artifactInfo = new ArtifactInfo();
        artifactInfo.setName(ARTIFACT_NAME);
        artifact.setArtifactInfo(artifactInfo);

        ISServerUtil isServerUtil = new ISServerUtil();
        Map<String, String> context = isServerUtil.getOAuthProperties();

        try {
            artifact.getData().add(getJavaFile());
            artifact.getData().add(getPropertiesFile(context));
            return artifact;
        } catch (IOException e) {
            throw new BuilderException(e);
        }
    }
}
