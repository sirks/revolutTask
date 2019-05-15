package com.revolut.task.api.services

import com.revolut.task.api.dto.TransactionDto
import com.revolut.task.config.DbConfig
import com.revolut.task.tables.daos.AccountDao
import com.revolut.task.tables.daos.TransactionDao
import com.revolut.task.tables.pojos.Account
import com.revolut.task.tables.pojos.Transaction
import spock.lang.Specification

class TransactionServiceTest extends Specification {
    TransactionDao transactionDao = Mock()
    AccountDao accountDao = Mock()
    TransactionService subject = new TransactionService(Mock(DbConfig), transactionDao, accountDao)
    TransactionDto transactionDto

    void setup() {
        transactionDto = Mock()
        transactionDto.fromAccountId = 1
        transactionDto.toAccountId = 2
        transactionDto.amount = 1
    }

    def "should return empty optional if not found"() {
        when:
        transactionDao.fetchOneById(_) >> null

        then:
        subject.getById(1).equals(Optional.empty())
    }

    def "should return transactiondto if found"() {
        when:
        int id = 1
        Transaction transaction = Mock()
        transaction.getId() >> id
        transactionDao.fetchOneById(_) >> transaction

        then:
        subject.getById(id).get().id == id
    }

    def "should not send money to same account"() {
        when:
        transactionDto.toAccountId = transactionDto.fromAccountId
        subject.create(transactionDto)

        then:
        thrown RuntimeException
    }

    def "should not allow non positive amount"() {
        when:
        transactionDto.amount = amount
        subject.create(transactionDto)

        then:
        thrown RuntimeException

        where:
        amount | _
        0      | _
        -1     | _
    }

    def "should fail if accounts not found"() {
        when:
        accountDao.fetchOneById(transactionDto.fromAccountId) >> null
        subject.create(transactionDto)

        then:
        thrown RuntimeException
    }

    def "should fail on not enough balance"() {
        when:
        transactionDto.amount = 99

        Account fromAccount = Mock()
        fromAccount.getBalance() >> transactionDto.amount - 1

        accountDao.fetchOneById(transactionDto.fromAccountId) >> fromAccount
        accountDao.fetchOneById(transactionDto.toAccountId) >> Mock(Account)
        subject.create(transactionDto)

        then:
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

        when:
        transactionDto.pojo() >> transactionPojo
        accountDao.fetchOneById(transactionDto.fromAccountId) >> fromAccount
        accountDao.fetchOneById(transactionDto.toAccountId) >> toAccount
        subject.create(transactionDto)

        then:
        1 * transactionDao.insert(transactionPojo)

//        TODO check out why ordering not working
//        then:
        1 * fromAccount.setBalance(fromAccountBalance - transactionDto.amount)
        1 * toAccount.setBalance(toAccountBalance + transactionDto.amount)

//        then:
        1 * accountDao.update(fromAccount)
        1 * accountDao.update(toAccount)
    }
}
