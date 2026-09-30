package com.example.shortener.exception;

import com.example.shortener.dto.ProblemDetails;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        String detail = exception.getConstraintViolations().isEmpty()
                ? "The request is invalid"
                : exception.getConstraintViolations().iterator().next().getMessage();
        return Response.status(Response.Status.BAD_REQUEST)
                .type("application/problem+json")
                .entity(new ProblemDetails("about:blank", "Bad Request", 400, detail, uriInfo.getRequestUri().toString()))
                .build();
    }
}