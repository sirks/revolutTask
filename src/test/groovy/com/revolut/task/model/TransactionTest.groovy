package com.revolut.task.model

import com.revolut.task.model.utils.Currency
import com.revolut.task.model.utils.Iban
import spock.lang.Specification

class TransactionTest extends Specification {
    Iban iban1 = new Iban("AA123456789012345678")
    Iban iban2 = new Iban("AB123456789012345678")

    def "should fail on same iban"() {
        when:
        iban3 = new Iban(iban1.number())
        account1 = new Account(iban1, Currency.EUR)
        account2 = new Account(iban3, Currency.EUR)
        new Transaction(account1, account2, BigDecimal.valueOf(99))

        then:
        thrown RuntimeException
    }

}
