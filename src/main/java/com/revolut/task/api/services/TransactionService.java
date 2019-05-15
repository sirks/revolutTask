package com.revolut.task.api.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.revolut.task.api.dto.TransactionDto;
import com.revolut.task.config.DbConfig;
import com.revolut.task.tables.daos.AccountDao;
import com.revolut.task.tables.daos.TransactionDao;
import com.revolut.task.tables.pojos.Account;
import com.revolut.task.tables.pojos.Transaction;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.logging.Logger;

import static java.lang.String.format;


@Singleton
public class TransactionService {
    private final static Logger LOG = Logger.getLogger("TransactionService");

    private final TransactionDao transactionDao;
    private final AccountDao accountDao;

    @Inject
    public TransactionService(DbConfig dbConfig, TransactionDao transactionDao, AccountDao accountDao) {
        this.transactionDao = transactionDao;
        this.transactionDao.setConfiguration(dbConfig.configuration());
        this.accountDao = accountDao;
        this.accountDao.setConfiguration(dbConfig.configuration());
    }

    public Optional<TransactionDto> getById(Integer transactionId) {
        return Optional.ofNullable(transactionDao.fetchOneById(transactionId)).map(TransactionDto::new);
    }

    public Integer create(TransactionDto transaction) {
        //TODO make this atomic. figure out jooq transactions and locking
        //does not work with daos, have to go all sql... nice...
        validate(transaction);
        Account fromAccount = accountDao.fetchOneById(transaction.fromAccountId);
        Account toAccount = accountDao.fetchOneById(transaction.toAccountId);
        validate(transaction, fromAccount, toAccount);

        Transaction pojo = transaction.pojo();
        pojo.setId(null);

        transactionDao.insert(pojo);
        fromAccount.setBalance(fromAccount.getBalance().subtract(pojo.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(pojo.getAmount()));
        accountDao.update(fromAccount);
        accountDao.update(toAccount);
        return pojo.getId();
    }

    private void validate(TransactionDto transaction) {
        if (accountNumbersEqual(transaction) || tooSmallAmount(transaction)) {
            String message = format("incorrect transaction %s", transaction);
            LOG.warning(message);
            throw new RuntimeException(message);
        }
    }

    private void validate(TransactionDto transaction, Account fromAccount, Account toAccount) {
        if (
                fromAccount == null ||
                toAccount == null ||
                notEnoughBalance(transaction, fromAccount)
        ) {
            String message = format("incorrect transaction or accounts %s,%s,%s", transaction, fromAccount, toAccount);
            LOG.warning(message);
            throw new RuntimeException(message);
        }
    }

    @SuppressWarnings({"all"})
    private boolean accountNumbersEqual(TransactionDto transaction) {
        return transaction.fromAccountId == transaction.toAccountId;
    }

    private boolean tooSmallAmount(TransactionDto transaction) {
        return transaction.amount.compareTo(BigDecimal.ZERO) <= 0;
    }

    private boolean notEnoughBalance(TransactionDto transaction, Account account) {
        return account.getBalance().compareTo(transaction.amount) < 0;
    }
}
