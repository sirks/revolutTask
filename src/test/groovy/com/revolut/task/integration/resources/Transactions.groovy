package com.revolut.task.integration.resources

import com.revolut.task.api.dto.AccountDto
import com.revolut.task.api.dto.TransactionDto
import com.revolut.task.integration.AccountHelper
import com.revolut.task.integration.MockServer
import com.revolut.task.integration.TransactionHelper
import spock.lang.Shared
import spock.lang.Specification

import java.util.stream.Stream

import static java.lang.Integer.valueOf
import static java.math.BigDecimal.ONE
import static java.math.BigDecimal.ZERO

class Transactions extends Specification {

    @Shared
    TransactionHelper transactionHelper
    @Shared
    AccountHelper accountHelper

    def setupSpec() {
        MockServer.start()
        transactionHelper = new TransactionHelper()
        accountHelper = new AccountHelper()
    }

    def cleanupSpec() {
        transactionHelper.stop()
    }

    def "should send transaction"() {
        given:
        def transactionAmount = BigDecimal.valueOf(10)
        def fromAccount = new AccountDto(AccountHelper.nextAccountNumber(), transactionAmount)
        def toAccount = new AccountDto(AccountHelper.nextAccountNumber(), transactionAmount)

        when:
        def fromAccountFuture = accountHelper.create(fromAccount)
        def toAccountFuture = accountHelper.create(toAccount)
        def accountIds = Stream.of(fromAccountFuture, toAccountFuture)
                .map({ it -> it.join() })
                .iterator()
        def fromAccountId = accountIds.next()
        def toAccountId = accountIds.next()

        def transaction = new TransactionDto(transactionAmount, valueOf(fromAccountId), valueOf(toAccountId))
        def returnTransaction = transactionHelper.create(transaction)
                .thenComposeAsync({ transactionId -> transactionHelper.getBy(transactionId) })
                .get()

        def returnFromAccountFuture = accountHelper.getBy(fromAccountId)
        def returnToAccountFuture = accountHelper.getBy(toAccountId)
        def returnAccounts = Stream.of(returnFromAccountFuture, returnToAccountFuture)
                .map({ it -> it.join() })
                .iterator()
        def returnFromAccount = returnAccounts.next()
        def returnToAccount = returnAccounts.next()

        then:
        returnTransaction.amount == transactionAmount
        returnFromAccount.balance == ZERO
        returnToAccount.balance == BigDecimal.valueOf(20)
    }

    def "should keep balance unaffected on failing transaction"() {
        given:
        def transactionAmount = BigDecimal.valueOf(10)
        def accountInitialBalance = transactionAmount.subtract(ONE)
        def fromAccount = new AccountDto(AccountHelper.nextAccountNumber(), accountInitialBalance)
        def toAccount = new AccountDto(AccountHelper.nextAccountNumber(), accountInitialBalance)

        when:
        def fromAccountFuture = accountHelper.create(fromAccount)
        def toAccountFuture = accountHelper.create(toAccount)
        def accountIds = Stream.of(fromAccountFuture, toAccountFuture)
                .map({ it -> it.join() })
                .iterator()
        def fromAccountId = accountIds.next()
        def toAccountId = accountIds.next()

        def transaction = new TransactionDto(transactionAmount, valueOf(fromAccountId), valueOf(toAccountId))
        def transactionId = transactionHelper.create(transaction).get()

        def returnFromAccountFuture = accountHelper.getBy(fromAccountId)
        def returnToAccountFuture = accountHelper.getBy(toAccountId)
        def returnAccounts = Stream.of(returnFromAccountFuture, returnToAccountFuture)
                .map({ it -> it.join() })
                .iterator()
        def returnFromAccount = returnAccounts.next()
        def returnToAccount = returnAccounts.next()

        then:
        transactionId == null
        returnFromAccount.balance == accountInitialBalance
        returnToAccount.balance == accountInitialBalance
    }


    def "should fill all competing concurrent transaction"() {
        given:
        def transactionAmount = BigDecimal.valueOf(10)
        def accountInitialBalance = BigDecimal.valueOf(20)
        def fromAccount = new AccountDto(AccountHelper.nextAccountNumber(), accountInitialBalance)
        def toAccount = new AccountDto(AccountHelper.nextAccountNumber(), accountInitialBalance)

        when:
//        create accounts
        def fromAccountFuture = accountHelper.create(fromAccount)
        def toAccountFuture = accountHelper.create(toAccount)
        def accountIds = Stream.of(fromAccountFuture, toAccountFuture)
                .map({ it -> it.join() })
                .iterator()
        def fromAccountId = accountIds.next()
        def toAccountId = accountIds.next()

//        create 2 concurrent transactions
        def transaction = new TransactionDto(transactionAmount, valueOf(fromAccountId), valueOf(toAccountId))
        def transaction1Future = transactionHelper.create(transaction)
        def transaction2Future = transactionHelper.create(transaction)
        def transactionIds = Stream.of(transaction1Future, transaction2Future)
                .map({ it -> it.join() })
                .iterator()
        def transactionId1 = transactionIds.next()
        def transactionId2 = transactionIds.next()

//        check what happened with accounts
        def returnFromAccountFuture = accountHelper.getBy(fromAccountId)
        def returnToAccountFuture = accountHelper.getBy(toAccountId)
        def returnAccounts = Stream.of(returnFromAccountFuture, returnToAccountFuture)
                .map({ it -> it.join() })
                .iterator()
        def returnFromAccount = returnAccounts.next()
        def returnToAccount = returnAccounts.next()

        then:
        transactionId1 != null
        transactionId2 != null
        returnFromAccount.balance == ZERO
        returnToAccount.balance == BigDecimal.valueOf(40)
    }
}
