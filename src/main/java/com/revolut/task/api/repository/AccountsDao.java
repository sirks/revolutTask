package com.revolut.task.api.repository;

import com.google.inject.Inject;
import com.revolut.task.tables.daos.AccountDao;
import com.revolut.task.tables.pojos.Account;
import org.jooq.Configuration;

import java.util.Optional;

public class AccountsDao implements Accounts{
    private final AccountDao dao;

    @Inject
    public AccountsDao(Configuration configuration, AccountDao dao) {
        this.dao = dao;
        this.dao.setConfiguration(configuration);
    }

    @Override
    public Optional<Account> getById(Integer id) {
        return Optional.ofNullable(dao.fetchOneById(id));
    }

    @Override
    public Integer create(Account pojo) {
        dao.insert(pojo);
        return pojo.getId();
    }

    @Override
    public Optional<Account> getByIban(String iban) {
        return Optional.ofNullable(dao.fetchOneByIban(iban));
    }

    @Override
    public void update(Account pojo) {
        dao.update(pojo);
    }
}
