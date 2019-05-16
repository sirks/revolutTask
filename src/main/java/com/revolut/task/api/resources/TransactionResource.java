package com.revolut.task.api.resources;

import com.google.inject.Inject;
import com.revolut.task.api.dto.TransactionDto;
import com.revolut.task.api.services.TransactionService;
import com.sun.jersey.api.NotFoundException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionResource {

    @Inject
    private TransactionService transactionService;

    @GET
    @Path("/{transactionId}")
    public TransactionDto getBy(@PathParam("transactionId") Integer transactionId) {
        return transactionService.getById(transactionId).orElseThrow(NotFoundException::new);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String create(TransactionDto account) {
        return transactionService.create(account).toString();
    }
}