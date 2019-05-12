package com.revolut.task.model.utils

import spock.lang.Specification

class IbanTest extends Specification {
    def "should fail on incorrect Iban"() {
        when:
        new Iban(number)

        then:
        thrown RuntimeException

        where:
        number         | _
        "x".repeat(15) | _
        "x".repeat(29) | _
    }

    def "should create new Iban"() {
        when:
        Iban iban = new Iban(number)

        then:
        number == iban.number()

        where:
        number                 | _
        "x".repeat(16)         | _
        "x".repeat(28)         | _
        "AA123456789012345678" | _
    }

}
