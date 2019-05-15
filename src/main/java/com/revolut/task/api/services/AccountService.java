package com.revolut.task.api.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.revolut.task.api.dto.AccountDto;
import com.revolut.task.config.DbConfig;
import com.revolut.task.tables.daos.AccountDao;
import com.revolut.task.tables.pojos.Account;

import java.util.Optional;
import java.util.logging.Logger;

import static java.lang.String.format;

@Singleton
public class AccountService {
    private final static Logger LOG = Logger.getLogger("AccountService");

    private final AccountDao accountDao;
    private final IbanService ibanService;

    @Inject
    public AccountService(DbConfig dbConfig, AccountDao accountDao, IbanService ibanService) {
        this.accountDao = accountDao;
        this.ibanService = ibanService;
        this.accountDao.setConfiguration(dbConfig.configuration());
    }

    public Optional<AccountDto> getById(Integer accountId) {
        return Optional.ofNullable(accountDao.fetchOneById(accountId)).map(AccountDto::new);
    }

    public Integer create(AccountDto account) {
        validate(account);
        Account pojo = account.pojo();
        pojo.setId(null);
        accountDao.insert(pojo);
        return pojo.getId();
    }

    private void validate(AccountDto account) {
        if (!ibanService.isValid(account.iban)) {
            String message = format("Incorrect iban length %s", account.iban);
            LOG.warning(message);
//            TODO should intercept and serialize the exception
            throw new RuntimeException(message);
        }
        if (accountDao.fetchOneByIban(account.iban) != null) {
            String message = format("Iban already exists %s", account.iban);
            LOG.warning(message);
//            TODO should intercept and serialize the exception
            throw new RuntimeException(message);
        }
        //TODO other validations
    }
}
