/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 *  Dissemination of any information or reproduction of any material
 * contained herein in any form is strictly forbidden, unless
 * permitted by WSO2 expressly. You may not alter or remove any
 * copyright or other notice from copies of this content.
 */

package org.wso2.identity.artifact.service.artifact;

import org.wso2.identity.artifact.service.artifact.builder.ArtifactBuilder;
import org.wso2.identity.artifact.service.model.ArtifactRequestData;
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

    public Artifact findArtifact(String name, ArtifactRequestData artifactRequestData) throws BuilderException, ClientException {

        ArtifactBuilder artifactBuilder = ArtifactRegistry.getBuilder(name, rootPath);
        artifactBuilder.setArtifactRequestData(artifactRequestData);
        return artifactBuilder.build();
    }

    public Set<String> getArtifactNames() {

        return new HashSet<String>() {{
            add("spring-boot");
        }};
    }
}
