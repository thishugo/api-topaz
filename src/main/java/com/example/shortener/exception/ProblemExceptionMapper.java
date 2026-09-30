package com.example.shortener.exception;

import com.example.shortener.dto.ProblemDetails;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ProblemExceptionMapper implements ExceptionMapper<BusinessException> {
    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(BusinessException exception) {
        return Response.status(exception.getStatus())
                .type("application/problem+json")
                .entity(new ProblemDetails("about:blank", exception.getTitle(), exception.getStatus(), exception.getMessage(), uriInfo.getRequestUri().toString()))
                .build();
    }
}