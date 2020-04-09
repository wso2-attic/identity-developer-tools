package org.wso2.identity.artifact.service.artifact.builder;

import org.wso2.identity.artifact.service.artifact.Artifact;
import org.wso2.identity.artifact.service.exception.BuilderException;
import org.wso2.identity.artifact.service.model.ArtifactRequestData;

public interface ArtifactBuilder {

    Artifact build() throws BuilderException;

    void setArtifactRequestData(ArtifactRequestData artifactRequestData);
}
