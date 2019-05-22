package com.revolut.task.api.services;

import com.google.inject.Singleton;
import com.revolut.task.api.dto.TransactionDto;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.lang.String.format;

@Singleton
public class TransactionLock {
    private final static Logger LOG = Logger.getLogger("TransactionLock");

    private final Set<Integer> currentTransactions;

    public TransactionLock() {
        this.currentTransactions = new HashSet<>();
    }

    public void tryLockAccounts(TransactionDto transaction) {
        Stream.of(0, 100, 200)
                .filter(timeout -> {
                    try {
                        Thread.sleep(timeout);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return trySingleLockAccounts(transaction);
                })
                .findFirst()
                .orElseThrow(() -> {
                    String message = format("Locked accounts %s", transaction);
                    LOG.warning(message);
                    return new RuntimeException(message);
                });
    }

    synchronized private Boolean trySingleLockAccounts(TransactionDto transaction) {
        if (currentTransactions.contains(transaction.fromAccountId)
                || currentTransactions.contains(transaction.toAccountId)) {
            return false;
        }
        currentTransactions.add(transaction.fromAccountId);
        currentTransactions.add(transaction.toAccountId);
        return true;
    }

    public void unlockAccounts(TransactionDto transaction) {
        currentTransactions.remove(transaction.fromAccountId);
        currentTransactions.remove(transaction.toAccountId);
    }
}
