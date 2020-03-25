package org.wso2.identity.artifact.service.artifact;

import org.wso2.identity.artifact.service.artifact.builder.spring.SpringBootArtifactBuilder;
import org.wso2.identity.artifact.service.exception.BuilderException;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;

public class ArtifactsRepository {

    private static Map<String, Artifact> artifactRegistry = new HashMap<>();
    private static ArtifactsRepository instance;
    private static String rootPath;

    private ArtifactsRepository(ServletContext servletContext) throws BuilderException {

        rootPath = Paths.get(servletContext.getRealPath("/"), "WEB-INF", "classes").toString();
        populateArtifactRegistry();
    }

    public static ArtifactsRepository getInstance(ServletContext servletContext) throws BuilderException {

        if (instance == null) {
            instance = new ArtifactsRepository(servletContext);
        }
        return instance;
    }

    public Artifact findArtifact(String name) {

        return artifactRegistry.get(name);
    }

    public Set<String> getArtifactNames() {

        return artifactRegistry.keySet();
    }

    private void populateArtifactRegistry() throws BuilderException {

        SpringBootArtifactBuilder springBootArtifactBuilder = new SpringBootArtifactBuilder(rootPath);
        Artifact springArtifact = springBootArtifactBuilder.build();
        artifactRegistry.put(springArtifact.getArtifactInfo().getName(), springArtifact);
    }

}
