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

import org.apache.commons.lang3.StringUtils;
import org.wso2.identity.artifact.service.artifact.builder.ArtifactBuilder;
import org.wso2.identity.artifact.service.artifact.builder.spring.SpringBootArtifactBuilder;
import org.wso2.identity.artifact.service.exception.ClientException;
import org.wso2.identity.artifact.service.model.ArtifactRequestData;

import java.util.HashMap;
import java.util.Map;

public class ArtifactRegistry {

    private static Map<String, ArtifactBuilder> builders = new HashMap<>();

    public static void addToRegistry(String technology, String resourcePath)
            throws ClientException {

        switch (technology) {
            case "spring-boot":
                builders.put(technology, new SpringBootArtifactBuilder(resourcePath));
                break;
            default:
                throw new ClientException("Cannot find artifacts corresponding for technology: " + technology);
        }
    }

    public static ArtifactBuilder getBuilder(String technology, String resourcePath) throws ClientException {

        if (builders.get(technology) == null) {
            addToRegistry(technology, resourcePath);
        } else{
            ArtifactBuilder builder = builders.get(technology);
        }
        return builders.get(technology);
    }
}
