/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 *  Dissemination of any information or reproduction of any material
 * contained herein in any form is strictly forbidden, unless
 * permitted by WSO2 expressly. You may not alter or remove any
 * copyright or other notice from copies of this content.
 */

package org.wso2.identity.artifact.service.artifact.builder;

import org.wso2.identity.artifact.service.artifact.Artifact;
import org.wso2.identity.artifact.service.exception.BuilderException;
import org.wso2.identity.artifact.service.model.ArtifactRequestData;

public interface ArtifactBuilder {

    Artifact build() throws BuilderException;

    void setArtifactRequestData(ArtifactRequestData artifactRequestData);
}
