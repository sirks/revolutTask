package com.revolut.task.api.services

import com.revolut.task.api.dto.TransactionDto
import com.revolut.task.api.helpers.TransactionLock
import com.revolut.task.api.repository.Accounts
import com.revolut.task.api.repository.Transactions
import com.revolut.task.tables.pojos.Account
import com.revolut.task.tables.pojos.Transaction
import spock.lang.Specification

class SimpleTransactionServiceTest extends Specification {
    TransactionLock transactionLock = Mock()
    Transactions transactions = Mock()
    Accounts accounts = Mock()
    TransactionService subject = new SimpleTransactionService(transactionLock, transactions, accounts)
    TransactionDto transactionDto

    void setup() {
        transactionDto = Mock()
        transactionDto.fromAccountId = 1
        transactionDto.toAccountId = 2
        transactionDto.amount = 1
    }

    def "should return empty optional if not found"() {
        when:
        transactions.getById(_) >> Optional.empty()

        then:
        subject.getById(1).equals(Optional.empty())
    }

    def "should return transactiondto if found"() {
        given:
        int id = 1
        Transaction transaction = Mock()
        transaction.getId() >> id
        transactions.getById(_) >> Optional.of(transaction)

        expect:
        subject.getById(id).get().id == id
    }

    def "should not send money to same account"() {
        given:
        transactionDto.toAccountId = transactionDto.fromAccountId

        when:
        subject.create(transactionDto)

        then:
        0 * _
        thrown RuntimeException
    }

    def "should not allow non positive amount"() {
        given:
        transactionDto.amount = amount

        when:
        subject.create(transactionDto)

        then:
        thrown RuntimeException

        where:
        amount | _
        0      | _
        -1     | _
    }

    def "should fail if accounts not found"() {
        given:
        accounts.getById(transactionDto.fromAccountId) >> Optional.empty()

        when:
        subject.create(transactionDto)

        then:
        thrown RuntimeException
    }

    def "should fail if accounts locked"() {
        given:
        transactionLock.tryLockAccounts(transactionDto) >> { throw new RuntimeException() }

        when:
        subject.create(transactionDto)

        then:
        thrown RuntimeException
    }

    def "should fail on not enough balance"() {
        given:
        transactionDto.amount = 99
        Account fromAccount = Mock()
        fromAccount.getBalance() >> transactionDto.amount - 1

        accounts.getById(transactionDto.fromAccountId) >> Optional.of(fromAccount)

        when:
        subject.create(transactionDto)

        then:
        1 * transactionLock.tryLockAccounts(transactionDto)
        thrown RuntimeException
    }

    def "should create transaction and update balances"() {
        given:
        transactionDto.amount = 99
        def fromAccountBalance = transactionDto.amount + 1
        def toAccountBalance = 1

        Account fromAccount = Mock()
        fromAccount.getBalance() >> fromAccountBalance

        Account toAccount = Mock()
        toAccount.getBalance() >> toAccountBalance
        Transaction transactionPojo = Mock()
        transactionPojo.getAmount() >> transactionDto.amount
        transactionDto.pojo() >> transactionPojo
        accounts.getById(transactionDto.fromAccountId) >> Optional.of(fromAccount)
        accounts.getById(transactionDto.toAccountId) >> Optional.of(toAccount)

        when:
        subject.create(transactionDto)

        then:
        1 * transactionLock.tryLockAccounts(transactionDto)
        1 * transactions.create(transactionPojo)

        1 * fromAccount.setBalance(fromAccountBalance - transactionDto.amount)
        1 * toAccount.setBalance(toAccountBalance + transactionDto.amount)

        1 * accounts.update(fromAccount)
        1 * accounts.update(toAccount)

        1 * transactionLock.unlockAccounts(transactionDto)

//        then:
//        0 * _
    }
}
