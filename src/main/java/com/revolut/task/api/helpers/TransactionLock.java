package com.revolut.task.api.helpers;

import com.revolut.task.api.dto.TransactionDto;

public interface TransactionLock {

    void tryLockAccounts(TransactionDto transaction) throws RuntimeException;

    void unlockAccounts(TransactionDto transaction);
}
