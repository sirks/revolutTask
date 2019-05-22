package com.revolut.task.api.repository

import com.revolut.task.tables.daos.AccountDao
import com.revolut.task.tables.pojos.Account
import org.jooq.Configuration
import spock.lang.Specification


class AccountsDaoTest extends Specification {
    AccountDao dao = Mock()
    AccountsDao subject = new AccountsDao(Mock(Configuration), dao)

    def "should get optional account if id exists"() {
        given:
        def id = 1
        Account pojo = Mock()
        dao.fetchOneById(id) >> pojo

        expect:
        subject.getById(id) == Optional.of(pojo)
    }

    def "should get empty optional if id not exists"() {
        given:
        dao.fetchOneById(_) >> null

        expect:
        subject.getById(1) == Optional.empty()
    }

    def "should get optional account if iban exists"() {
        given:
        def iban = "1234"
        Account pojo = Mock()
        dao.fetchOneByIban(iban) >> pojo

        expect:
        subject.getByIban(iban) == Optional.of(pojo)
    }

    def "should get empty optional if iban not exists"() {
        given:
        dao.fetchOneByIban(_) >> null

        expect:
        subject.getByIban("1234") == Optional.empty()
    }

    def "should insert into db"() {
        given:
        def id = 1
        def pojo = Mock(Account)
        pojo.getId() >> id

        when:
        def returnId = subject.create(pojo)

        then:
        1 * dao.insert(pojo)
        returnId == id
    }

    def "should update account"(){
        given:
        def pojo = Mock(Account)

        when:
        subject.update(pojo)

        then:
        1 * dao.update(pojo)
    }
}
