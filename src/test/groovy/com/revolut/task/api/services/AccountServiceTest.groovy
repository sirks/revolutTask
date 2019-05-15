package com.revolut.task.api.services

import com.revolut.task.api.dto.AccountDto
import com.revolut.task.config.DbConfig
import com.revolut.task.tables.daos.AccountDao
import com.revolut.task.tables.pojos.Account
import spock.lang.Specification

class AccountServiceTest extends Specification {
    AccountDao accountDao = Mock()
    IbanService ibanService = Mock()
    AccountService subject = new AccountService(Mock(DbConfig), accountDao, ibanService)

    void setup() {
        ibanService.isValid(_) >> true
    }

    def "should return empty optional if not found"() {
        when:
        accountDao.fetchOneById(_) >> null

        then:
        subject.getById(1).equals(Optional.empty())
    }

    def "should return accountdto if found"() {
        when:
        int id = 1
        Account account = Mock()
        account.getId() >> id
        accountDao.fetchOneById(_) >> account

        then:
        subject.getById(id).get().id == id
    }

    def "should fail on invalid iban"() {
        when:
        ibanService.isValid(_) >> false
        subject.create(Mock(AccountDto))

        then:
        thrown RuntimeException
    }

    def "should fail if iban exists"() {
        when:
        accountDao.fetchOneByIban(_) >> Mock(Account)
        subject.create(Mock(AccountDto))

        then:
        thrown RuntimeException
    }

    def "should create account"() {
        AccountDto accountDto = Mock()
        Account account = Mock()
        accountDto.pojo() >> account
        when:
        subject.create(accountDto)

        then:
        1 * accountDao.insert(account)
    }
}
