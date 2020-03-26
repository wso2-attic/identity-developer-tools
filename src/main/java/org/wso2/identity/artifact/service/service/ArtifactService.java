package org.wso2.identity.artifact.service.service;

import org.wso2.identity.artifact.service.artifact.Artifact;
import org.wso2.identity.artifact.service.artifact.ArtifactInfo;
import org.wso2.identity.artifact.service.artifact.ArtifactsRepository;
import org.wso2.identity.artifact.service.exception.BuilderException;
import org.wso2.identity.artifact.service.exception.ClientException;
import org.wso2.identity.artifact.service.exception.ServiceException;

import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;

public class ArtifactService {

    public List<ArtifactInfo> getArtifactInfo(ServletContext servletContext) {

        ArtifactsRepository artifactsRepository = ArtifactsRepository.getInstance(servletContext);
        return artifactsRepository.getArtifactNames().stream().map(ArtifactInfo::new).collect(Collectors.toList());
    }

    public Artifact getArtifact(String artifactName, String spName, ServletContext servletContext)
            throws ServiceException, ClientException {

        try {
            ArtifactsRepository artifactsRepository = ArtifactsRepository.getInstance(servletContext);
            Artifact artifact = artifactsRepository.findArtifact(artifactName, spName);
            if (artifact == null) {
                throw new ClientException("Cannot find the artifact.");
            }
            return artifact;
        } catch (BuilderException e) {
            throw new ServiceException("Error occurred while finding the artifact.", e);
        }
    }
}
