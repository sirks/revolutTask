package com.revolut.task.api.services

import com.revolut.task.api.dto.TransactionDto
import spock.lang.Specification

class TransactionLockTest extends Specification {
    TransactionLock subject = new TransactionLock()

    def "should not lock account twice"() {
        given:
        TransactionDto transactionDto = Mock()
        transactionDto.fromAccountId = 1
        transactionDto.toAccountId = 2

        when:
        subject.tryLockAccounts(transactionDto)

        then:
        subject.currentTransactions.size() == 2

        when:
        subject.tryLockAccounts(transactionDto)

        then:
        thrown RuntimeException
    }

    def "should lock account after unlock"() {
        given:
        TransactionDto transactionDto = Mock()
        transactionDto.fromAccountId = 1
        transactionDto.toAccountId = 2

        when:
        subject.tryLockAccounts(transactionDto)
        subject.unlockAccounts(transactionDto)

        then:
        subject.currentTransactions.size() == 0

        when:
        subject.tryLockAccounts(transactionDto)

        then:
        subject.currentTransactions.size() == 2
    }}
