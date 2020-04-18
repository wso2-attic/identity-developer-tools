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
