/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 *  Dissemination of any information or reproduction of any material
 * contained herein in any form is strictly forbidden, unless
 * permitted by WSO2 expressly. You may not alter or remove any
 * copyright or other notice from copies of this content.
 */

package org.wso2.identity.artifact.service.endpoint;

import org.wso2.identity.artifact.service.artifact.Artifact;
import org.wso2.identity.artifact.service.artifact.ArtifactInfo;
import org.wso2.identity.artifact.service.exception.ClientException;
import org.wso2.identity.artifact.service.exception.ServiceException;
import org.wso2.identity.artifact.service.model.ArtifactRequestData;
import org.wso2.identity.artifact.service.model.Response;
import org.wso2.identity.artifact.service.service.ArtifactService;

import java.util.List;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/service")
public class ServiceEndpoint {

    @Context
    private ServletContext servletContext;

    @GET
    @Path("/names")
    @Produces(MediaType.APPLICATION_JSON)
    public Response<List<ArtifactInfo>> getSupportedArtifacts() {

        ArtifactService artifactService = new ArtifactService();
        return new Response<>(artifactService.getArtifactInfo(servletContext));
    }

    @POST
    @Path("/artifact/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response<Artifact> getArtifact(@PathParam("name") String name, ArtifactRequestData artifactRequestData) {

        try {
            ArtifactService artifactService = new ArtifactService();
            return new Response<>(artifactService.getArtifact(name, servletContext, artifactRequestData));
        } catch (ClientException ex) {
            return new Response<>(ex.getMessage());
        } catch (ServiceException e) {
            return new Response<>("Error occurred while calling artifact-service.");
        }
    }
}
