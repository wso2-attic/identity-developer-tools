package org.wso2.identity.artifact.service.artifact;

import org.wso2.identity.artifact.service.artifact.builder.spring.SpringBootArtifactBuilder;
import org.wso2.identity.artifact.service.endpoint.CLIInput;
import org.wso2.identity.artifact.service.exception.BuilderException;
import org.wso2.identity.artifact.service.exception.ClientException;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletContext;

public class ArtifactsRepository {

    private static ArtifactsRepository instance;
    private static String rootPath;

    private ArtifactsRepository(ServletContext servletContext) {

        rootPath = Paths.get(servletContext.getRealPath("/"), "WEB-INF", "classes").toString();
    }

    public static ArtifactsRepository getInstance(ServletContext servletContext) {

        if (instance == null) {
            instance = new ArtifactsRepository(servletContext);
        }
        return instance;
    }

    public Artifact findArtifact(String name, CLIInput cliInput) throws BuilderException, ClientException {

        switch (name) {
            case "spring-boot":
                return getSpringArtifacts(cliInput);
            default:
                throw new ClientException("Cannot find artifacts corresponding for technology: " + name);
        }
    }

    public Set<String> getArtifactNames() {

        return new HashSet<String>() {{
            add("spring-boot");
        }};
    }

    private Artifact getSpringArtifacts(CLIInput cliInput) throws BuilderException {

        SpringBootArtifactBuilder springBootArtifactBuilder = new SpringBootArtifactBuilder(rootPath, cliInput);
        return springBootArtifactBuilder.build();
    }
}
