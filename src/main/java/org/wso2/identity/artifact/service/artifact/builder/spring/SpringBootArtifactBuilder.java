package org.wso2.identity.artifact.service.artifact.builder.spring;

import com.hubspot.jinjava.Jinjava;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.wso2.identity.artifact.service.artifact.Artifact;
import org.wso2.identity.artifact.service.artifact.ArtifactData;
import org.wso2.identity.artifact.service.artifact.ArtifactInfo;
import org.wso2.identity.artifact.service.artifact.builder.ArtifactBuilder;
import org.wso2.identity.artifact.service.endpoint.CLIInput;
import org.wso2.identity.artifact.service.exception.BuilderException;
import org.wso2.identity.artifact.service.exception.ClientException;
import org.wso2.identity.artifact.service.exception.ServiceException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private final String resourcePath;
    private CLIInput cliInput;

    public SpringBootArtifactBuilder(String resourcePath, CLIInput cliInput) {

        this.resourcePath = resourcePath;
        this.cliInput = cliInput;
    }

    private ArtifactData getJavaFile() throws IOException {

        Path path = Paths.get(resourcePath, JAVA_FILE_NAME);
        ArtifactData artifactData = new ArtifactData();
        String packageName = "package " + cliInput.getPackageName() + ".controller;\n\n";
        byte[] concatBytes = ArrayUtils.addAll(packageName.getBytes(), Files.readAllBytes(path));
        artifactData.setData(concatBytes);
        String packagePath = StringUtils.replace(cliInput.getPackageName(), ".", "/");
        artifactData.setPath("src/main/java/" + packagePath + "/controller/" + JAVA_FILE_NAME);
        return artifactData;
    }

    private ArtifactData getIndexHtmlPage() throws IOException {

        Path path = Paths.get(resourcePath, INDEX_HTML_FILE_NAME);
        ArtifactData artifactData = new ArtifactData();
        artifactData.setData(Files.readAllBytes(path));
        artifactData.setPath(INDEX_HTML_PATH);
        return artifactData;
    }

    private ArtifactData getUserInfoHtmlPage() throws IOException {

        Path path = Paths.get(resourcePath, USERINFO_HTML_FILE_NAME);
        ArtifactData artifactData = new ArtifactData();
        artifactData.setData(Files.readAllBytes(path));
        artifactData.setPath(UNERINFO_HTML_PATH);
        return artifactData;
    }

    private ArtifactData getPropertiesFile(Map<String, String> context) throws IOException {

        Path path = Paths.get(resourcePath, PROPERTIES_FILE_NAME);
        String template = new String(Files.readAllBytes(path));
        Jinjava jinjava = new Jinjava();
        String renderedTemplate = jinjava.render(template, context);
        ArtifactData artifactData = new ArtifactData();
        artifactData.setData(renderedTemplate.getBytes());
        artifactData.setPath(PROPERTIES_FILE_PATH);
        return artifactData;
    }

    @Override
    public Artifact build() throws BuilderException{

        Artifact artifact = new Artifact();
        ArtifactInfo artifactInfo = new ArtifactInfo();
        artifactInfo.setName(ARTIFACT_NAME);
        artifact.setArtifactInfo(artifactInfo);

        ISServerUtil isServerUtil = new ISServerUtil();
        try {
            Map<String, String> context = isServerUtil.getOAuthProperties(cliInput);
            artifact.getData().add(getJavaFile());
            artifact.getData().add(getPropertiesFile(context));
            artifact.getData().add(getIndexHtmlPage());
            artifact.getData().add(getUserInfoHtmlPage());
            return artifact;
        } catch (IOException e ) {
            throw new BuilderException("Unable to get artifacts", e);
        } catch (ClientException e){
            throw new BuilderException("Unable to get proper response from DCR endpoint ", e);
        }
    }
}
