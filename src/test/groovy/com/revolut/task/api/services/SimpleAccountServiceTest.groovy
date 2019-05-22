package com.revolut.task.api.services

import com.revolut.task.api.dto.AccountDto
import com.revolut.task.api.repository.Accounts
import com.revolut.task.tables.daos.AccountDao
import com.revolut.task.tables.pojos.Account
import spock.lang.Specification

class SimpleAccountServiceTest extends Specification {
    Accounts accounts = Mock()
    SimpleAccountService subject = new SimpleAccountService(accounts)

    AccountDto accountDto = Mock()

    void setup() {
        accountDto.iban = "123456"
    }

    def "should return empty optional if not found"() {
        when:
        accounts.getById(_) >> Optional.empty()

        then:
        subject.getById(1).equals(Optional.empty())
    }

    def "should return accountdto if found"() {
        given:
        int id = 1
        Account account = Mock()
        account.getId() >> id
        accounts.getById(_) >> Optional.of(account)

        expect:
        subject.getById(id).get().id == id
    }

    def "should fail on invalid iban"() {
        given:
        accountDto.iban = ""

        when:
        subject.create(accountDto)

        then:
        thrown RuntimeException
    }

    def "should fail if iban exists"() {
        given:
        accounts.getByIban(_) >> Optional.of(Mock(Account))

        when:
        subject.create(accountDto)

        then:
        thrown RuntimeException
    }

    def "should create account"() {
        given:
        Account account = Mock()
        accountDto.pojo() >> account
        accounts.getByIban(_) >> Optional.empty()

        when:
        subject.create(accountDto)

        then:
        1 * accounts.create(account)
    }
}
