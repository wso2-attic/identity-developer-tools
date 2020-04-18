/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
