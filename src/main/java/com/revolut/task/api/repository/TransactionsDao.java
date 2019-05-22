package com.revolut.task.api.repository;

import com.google.inject.Inject;
import com.revolut.task.tables.daos.TransactionDao;
import com.revolut.task.tables.pojos.Transaction;
import org.jooq.Configuration;

import java.util.Optional;

public class TransactionsDao implements Transactions{
    private final TransactionDao dao;

    @Inject
    public TransactionsDao(Configuration configuration, TransactionDao dao) {
        this.dao = dao;
        this.dao.setConfiguration(configuration);
    }

    @Override
    public Optional<Transaction> getById(Integer id) {
        return Optional.ofNullable(dao.fetchOneById(id));
    }

    @Override
    public Integer create(Transaction pojo) {
        dao.insert(pojo);
        return pojo.getId();
    }
}
