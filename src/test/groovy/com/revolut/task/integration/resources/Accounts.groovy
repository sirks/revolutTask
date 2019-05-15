package com.revolut.task.integration.resources

import com.revolut.task.api.dto.AccountDto
import com.revolut.task.integration.AccountHelper
import com.revolut.task.integration.MockServer
import spock.lang.Shared
import spock.lang.Specification

import static java.math.BigDecimal.ZERO

class Accounts extends Specification {

    @Shared
    AccountHelper accountHelper

    def setupSpec() {
        MockServer.start()
        accountHelper = new AccountHelper()
    }

    def cleanupSpec() {
        accountHelper.stop()
    }

    def "should create and get account"() {
        given:
        def account = new AccountDto(AccountHelper.nextAccountNumber(), ZERO)

        when:
        def returnAccount = accountHelper.create(account)
                .thenComposeAsync({ accountId -> accountHelper.getBy(accountId) })
                .get()

        then:
        returnAccount.iban == account.iban
    }
}
