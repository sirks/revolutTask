package com.revolut.task.api.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.revolut.task.config.H2Config;
import com.revolut.task.tables.daos.AccountDao;
import com.revolut.task.tables.pojos.Account;

@Singleton
public class AccountService {

    private final AccountDao accountDao;

    @Inject
    public AccountService(H2Config h2Config, AccountDao accountDao) {
        this.accountDao = accountDao;
        this.accountDao.setConfiguration(h2Config.configuration);
    }

    public Account getById(Integer accountId) {
        return accountDao.fetchOneById(accountId);
    }
}
