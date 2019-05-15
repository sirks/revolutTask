package com.revolut.task.integration.services

import com.revolut.task.api.dto.AccountDto
import com.revolut.task.integration.AccountHelper
import com.revolut.task.integration.MockServer
import spock.lang.Shared
import spock.lang.Specification

import static com.revolut.task.enums.AccountCurrency.EUR

class Accounts extends Specification {

    @Shared
    AccountHelper accountHelper

    def setupSpec() {
        MockServer.start()
        accountHelper = new AccountHelper()
    }

    def cleanupSpec() {
        accountHelper.stop()
        MockServer.stop()
    }

    def "can create and get account"() {
        given:
        def account = new AccountDto()
        account.iban = "1234567890"
        account.currency = EUR

        when:
        def accountId = accountHelper.create(account)
        def returnAccount = accountHelper.getBy(accountId)

        then:
        returnAccount.iban == account.iban
    }
}
