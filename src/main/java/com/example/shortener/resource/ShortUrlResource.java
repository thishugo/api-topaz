package com.example.shortener.resource;

import com.example.shortener.dto.CreateShortUrlRequest;
import com.example.shortener.dto.ShortUrlResponse;
import com.example.shortener.dto.ShortUrlStatsResponse;
import com.example.shortener.service.ShortUrlService;
import java.net.URI;
import java.util.List;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Path("api/v1/urls")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ShortUrlResource {
    @Context
    private UriInfo uriInfo;

    @Inject
    private ShortUrlService service;

    @POST
    public Response create(@Valid CreateShortUrlRequest request) {
        ShortUrlResponse response = service.create(request);
        URI location = uriInfo.getBaseUriBuilder().path(response.getShortCode()).build();
        return Response.created(location).entity(response).build();
    }

    @GET
    @Path("recent")
    public List<ShortUrlResponse> recent() {
        return service.findRecent();
    }

    @GET
    @Path("{shortCode}/stats")
    public ShortUrlStatsResponse stats(@PathParam("shortCode") String shortCode) {
        return service.getStats(shortCode);
    }
}