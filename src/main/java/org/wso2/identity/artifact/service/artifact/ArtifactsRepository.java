package org.wso2.identity.artifact.service.artifact;

import org.wso2.identity.artifact.service.artifact.builder.spring.SpringBootArtifactBuilder;
import org.wso2.identity.artifact.service.exception.BuilderException;

import java.util.HashMap;
import java.util.Map;

public class ArtifactsRepository {

    private static Map<String, Artifact> artifactRegistry = new HashMap<>();

    private static final String ROOT_PATH = "";

    public static Artifact findArtifact(String name) throws BuilderException {

        return artifactRegistry.get(name);
    }

    private void populateArtifactRegistry() throws BuilderException {

        SpringBootArtifactBuilder springBootArtifactBuilder = new SpringBootArtifactBuilder(ROOT_PATH);
        Artifact springArtifact = springBootArtifactBuilder.build();
        artifactRegistry.put(springArtifact.getArtifactInfo().getName(), springArtifact);
    }

}
