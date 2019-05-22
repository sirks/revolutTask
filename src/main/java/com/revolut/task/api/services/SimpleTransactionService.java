package com.revolut.task.api.services;

import com.google.inject.Inject;
import com.revolut.task.api.dto.TransactionDto;
import com.revolut.task.api.helpers.TransactionLock;
import com.revolut.task.api.repository.Accounts;
import com.revolut.task.api.repository.Transactions;
import com.revolut.task.tables.pojos.Account;
import com.revolut.task.tables.pojos.Transaction;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static com.revolut.task.utils.ExceptionUtils.buildException;
import static java.lang.String.format;

public class SimpleTransactionService implements TransactionService {
    private final static Logger LOG = Logger.getLogger("SimpleTransactionService");

    private final TransactionLock transactionLock;
    private final Transactions transactions;
    private final Accounts accounts;

    @Inject
    public SimpleTransactionService(TransactionLock transactionLock, Transactions transactions, Accounts accounts) {
        this.transactionLock = transactionLock;
        this.transactions = transactions;
        this.accounts = accounts;
    }


    public Optional<TransactionDto> getById(Integer transactionId) {
        return transactions.getById(transactionId).map(TransactionDto::new);
    }

    public Integer create(TransactionDto transaction) {
        /* TODO make this atomic.
          jooq transactions doesn't work with jooq daos. have to go all sql...
          jooq generated daos does not support async methods...

          Should use message queue or streaming service like kafka.
          Current implementation does not support multiple server instances.
         */

        validate(transaction);
        transactionLock.tryLockAccounts(transaction);

        try {
            Iterator<Optional<Account>> accountIterator = Stream.of(
                    CompletableFuture.supplyAsync(() -> accounts.getById(transaction.fromAccountId)),
                    CompletableFuture.supplyAsync(() -> accounts.getById(transaction.toAccountId))
            )
                    .map(CompletableFuture::join)
                    .iterator();

            Supplier<RuntimeException> error =
                    () -> buildException(LOG, format("incorrect transaction %s", transaction));

            Account fromAccount = accountIterator.next()
                    .filter(it -> hasEnoughBalance(transaction, it))
                    .orElseThrow(error);
            Account toAccount = accountIterator.next()
                    .orElseThrow(error);

            Transaction pojo = transaction.pojo();
            pojo.setId(null);

            Integer id = transactions.create(pojo);
            fromAccount.setBalance(fromAccount.getBalance().subtract(pojo.getAmount()));
            toAccount.setBalance(toAccount.getBalance().add(pojo.getAmount()));

            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> accounts.update(fromAccount)),
                    CompletableFuture.runAsync(() -> accounts.update(toAccount))
            ).join();
            return id;
        } finally {
            transactionLock.unlockAccounts(transaction);
        }
    }

    private void validate(TransactionDto transaction) {
        if (accountNumbersEqual(transaction) || tooSmallAmount(transaction)) {
            String message = format("incorrect transaction %s", transaction);
            LOG.warning(message);
            throw new RuntimeException(message);
        }
    }

    private boolean accountNumbersEqual(TransactionDto transaction) {
        return transaction.fromAccountId.equals(transaction.toAccountId);
    }

    private boolean tooSmallAmount(TransactionDto transaction) {
        return transaction.amount.compareTo(BigDecimal.ZERO) <= 0;
    }

    private boolean hasEnoughBalance(TransactionDto transaction, Account account) {
        return account.getBalance().compareTo(transaction.amount) >= 0;
    }
}
