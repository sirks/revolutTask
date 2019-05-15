package com.revolut.task.api.resources;

import com.google.inject.Inject;
import com.revolut.task.api.dto.AccountDto;
import com.revolut.task.api.services.AccountService;
import com.sun.jersey.api.NotFoundException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    @Inject
    private AccountService accountService;

    @GET
    @Path("/{accountId}")
    public AccountDto getBy(@PathParam("accountId") Integer accountId) {
        return accountService.getById(accountId).orElseThrow(NotFoundException::new);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String create(AccountDto account) {
        return accountService.create(account).toString();
    }
}