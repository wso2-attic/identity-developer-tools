package org.wso2.identity.artifact.service.endpoint;

import org.wso2.identity.artifact.service.exception.ClientException;
import org.wso2.identity.artifact.service.exception.ServiceException;
import org.wso2.identity.artifact.service.artifact.Artifact;
import org.wso2.identity.artifact.service.artifact.ArtifactInfo;
import org.wso2.identity.artifact.service.model.Response;
import org.wso2.identity.artifact.service.service.ArtifactService;

import java.util.List;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
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
        try {
            return new Response<>(artifactService.getArtifactInfo(servletContext));
        } catch (ServiceException e) {
            return new Response<>("Error occurred while calling.");
        }
    }

    @GET
    @Path("/artifact/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response<Artifact> getArtifact(@PathParam("name") String name) {

        try {
            ArtifactService artifactService = new ArtifactService();
            return new Response<>(artifactService.getArtifact(name, servletContext));
        } catch (ClientException ex) {
            return new Response<>(ex.getMessage());
        } catch (ServiceException e) {
            // TODO: Handle this.
            return new Response<>("Error occurred while calling.");
        }
    }
}
