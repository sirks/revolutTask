package com.revolut.task.api.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.revolut.task.api.dto.AccountDto;
import com.revolut.task.api.repository.Accounts;
import com.revolut.task.tables.pojos.Account;

import java.util.Optional;
import java.util.logging.Logger;

import static com.revolut.task.utils.ExceptionUtils.buildException;
import static java.lang.String.format;

@Singleton
public class SimpleAccountService implements AccountService {
    private final static Logger LOG = Logger.getLogger("SimpleAccountService");

    private final Accounts accounts;

    @Inject
    public SimpleAccountService(Accounts accounts) {
        this.accounts = accounts;
    }

    public Optional<AccountDto> getById(Integer accountId) {
        return accounts.getById(accountId).map(AccountDto::new);
    }

    public Integer create(AccountDto account) {
        validate(account);
        Account pojo = account.pojo();
        pojo.setId(null);
        return accounts.create(pojo);
    }

    private void validate(AccountDto account) {
        if (account.iban.length() < 1 || account.iban.length() > 28) {
//            TODO should intercept and serialize the exception
            throw buildException(LOG, format("Incorrect iban length %s", account.iban));
        }
        accounts.getByIban(account.iban)
                .ifPresent(it -> {
                    throw buildException(LOG, format("Iban already exists %s", account.iban));
                });
        //TODO other validations
    }
}
