package com.revolut.task.api.repository;

import com.revolut.task.tables.pojos.Account;

import java.util.Optional;

public interface Accounts extends Repositoty<Account> {
    Optional<Account> getByIban(String iban);

    void update(Account pojo);
}
