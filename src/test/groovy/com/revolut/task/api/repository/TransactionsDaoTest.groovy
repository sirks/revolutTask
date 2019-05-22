package com.revolut.task.api.repository

import com.revolut.task.tables.daos.TransactionDao
import com.revolut.task.tables.pojos.Transaction
import org.jooq.Configuration
import spock.lang.Specification

class TransactionsDaoTest extends Specification {
    TransactionDao dao = Mock()
    TransactionsDao subject = new TransactionsDao(Mock(Configuration), dao)

    def "should get optional transaction if id exists"() {
        given:
        def id = 1
        Transaction pojo = Mock()
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


    def "should insert into db"() {
        given:
        def id = 1
        def pojo = Mock(Transaction)
        pojo.getId() >> id

        when:
        def returnId = subject.create(pojo)

        then:
        1 * dao.insert(pojo)
        returnId == id
    }
}