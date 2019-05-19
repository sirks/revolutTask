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
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Stream;

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
        /** TODO make this atomic. Probably should use some message queue or streaming service like kafka.
         ** jooq transactions doesn't work with jooq daos. have to go all sql... nice...
         ** jooq generated daos does not support async methods... so 2010...
         **/

        validate(transaction);

        Iterator<Account> accounts = Stream.of(
                CompletableFuture.supplyAsync(() -> accountDao.fetchOneById(transaction.fromAccountId)),
                CompletableFuture.supplyAsync(() -> accountDao.fetchOneById(transaction.toAccountId))
        )
                .map(CompletableFuture::join)
                .iterator();

        Account fromAccount = accounts.next();
        Account toAccount = accounts.next();
        validate(transaction, fromAccount, toAccount);

        Transaction pojo = transaction.pojo();
        pojo.setId(null);

        transactionDao.insert(pojo);
        fromAccount.setBalance(fromAccount.getBalance().subtract(pojo.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(pojo.getAmount()));

        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> accountDao.update(fromAccount)),
                CompletableFuture.runAsync(() -> accountDao.update(toAccount))
        ).join();
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
