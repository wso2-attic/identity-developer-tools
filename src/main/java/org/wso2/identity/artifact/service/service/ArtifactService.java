package org.wso2.identity.artifact.service.service;

import org.wso2.identity.artifact.service.artifact.Artifact;
import org.wso2.identity.artifact.service.artifact.ArtifactInfo;
import org.wso2.identity.artifact.service.artifact.ArtifactsRepository;
import org.wso2.identity.artifact.service.exception.BuilderException;
import org.wso2.identity.artifact.service.exception.ClientException;
import org.wso2.identity.artifact.service.exception.ServiceException;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

public class ArtifactService {

    public List<ArtifactInfo> getArtifactInfo() {

        return new ArrayList<>();
    }

    public Artifact getArtifact(String artifactName, ServletContext servletContext)
            throws ServiceException, ClientException {

        try {
            ArtifactsRepository artifactsRepository = ArtifactsRepository.getInstance(servletContext);
            Artifact artifact = artifactsRepository.findArtifact(artifactName);
            if (artifact == null) {
                throw new ClientException("Cannot find the artifact.");
            }
            return artifact;
        } catch (BuilderException e) {
            throw new ServiceException("Error occurred while finding the artifact.", e);
        }
    }
}
