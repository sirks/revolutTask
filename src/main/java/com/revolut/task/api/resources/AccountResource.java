package com.revolut.task.api.resources;

import com.google.inject.Inject;
import com.revolut.task.api.services.AccountService;
import com.revolut.task.tables.pojos.Account;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    @Inject
    private AccountService accountService;

    public AccountResource() {
        System.out.println("AccountResource created");
    }

    @GET
    @Path("/{accountId}")
    public Account getById(@PathParam("accountId") Integer accountId) {
        return accountService.getById(accountId);
    }
//
//    @GET
//    @Path("/view_freemarker")
//    @UnitOfWork
//    @Produces(MediaType.TEXT_HTML)
//    public PersonView getPersonViewFreemarker(@PathParam("personId") LongParam personId) {
//        return new PersonView(PersonView.Template.FREEMARKER, findSafely(personId.get()));
//    }
//
//    @GET
//    @Path("/view_mustache")
//    @UnitOfWork
//    @Produces(MediaType.TEXT_HTML)
//    public PersonView getPersonViewMustache(@PathParam("personId") LongParam personId) {
//        return new PersonView(PersonView.Template.MUSTACHE, findSafely(personId.get()));
//    }
//
//    private Person findSafely(long personId) {
//        return peopleDAO.findById(personId).orElseThrow(() -> new NotFoundException("No such user."));
//    }
}