/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 *  Dissemination of any information or reproduction of any material
 * contained herein in any form is strictly forbidden, unless
 * permitted by WSO2 expressly. You may not alter or remove any
 * copyright or other notice from copies of this content.
 */

package org.wso2.identity.artifact.service.service;

import org.wso2.identity.artifact.service.artifact.Artifact;
import org.wso2.identity.artifact.service.artifact.ArtifactInfo;
import org.wso2.identity.artifact.service.artifact.ArtifactsRepository;
import org.wso2.identity.artifact.service.model.ArtifactRequestData;
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

    public Artifact getArtifact(String artifactName, ServletContext servletContext, ArtifactRequestData artifactRequestData)
            throws ServiceException, ClientException {

        try {
            ArtifactsRepository artifactsRepository = ArtifactsRepository.getInstance(servletContext);
            Artifact artifact = artifactsRepository.findArtifact(artifactName, artifactRequestData);
            if (artifact == null) {
                throw new ClientException("Cannot find the artifact.");
            }
            return artifact;
        } catch (BuilderException e) {
            throw new ServiceException("Error occurred while finding the artifact.", e);
        }
    }
}
