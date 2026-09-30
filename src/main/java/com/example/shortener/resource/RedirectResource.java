package com.example.shortener.resource;

import com.example.shortener.service.ClickTrackingService;
import com.example.shortener.service.ShortUrlService;
import java.net.URI;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/")
public class RedirectResource {
    @Inject
    private ShortUrlService service;

    @EJB
    private ClickTrackingService clickTrackingService;

    @GET
    @Path("{shortCode}")
    public Response redirect(@PathParam("shortCode") String shortCode) {
        String originalUrl = service.getRedirectUrl(shortCode);
        clickTrackingService.registerClick(shortCode);
        return Response.status(Response.Status.FOUND).location(URI.create(originalUrl)).build();
    }
}