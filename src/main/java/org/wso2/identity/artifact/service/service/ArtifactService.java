package org.wso2.identity.artifact.service.service;

import org.wso2.identity.artifact.service.exception.BuilderException;
import org.wso2.identity.artifact.service.exception.ClientException;
import org.wso2.identity.artifact.service.exception.ServiceException;
import org.wso2.identity.artifact.service.artifact.Artifact;
import org.wso2.identity.artifact.service.artifact.ArtifactInfo;
import org.wso2.identity.artifact.service.artifact.ArtifactsRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArtifactService {

    public List<ArtifactInfo> getArtifactInfo() {

        return new ArrayList<>();
    }

    public Artifact getArtifact(String artifactName) throws ServiceException, ClientException {

        try {
             Artifact artifact = ArtifactsRepository.findArtifact(artifactName);
             if (artifact == null) {
                 throw new ClientException("Cannot find the artifact.");
             }
             return artifact;
        } catch (BuilderException e) {
            throw new ServiceException("Error occurred while finding the artifact.", e);
        }
    }
}
