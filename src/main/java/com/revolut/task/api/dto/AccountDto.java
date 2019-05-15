package com.revolut.task.api.dto;

import com.revolut.task.tables.pojos.Account;

import java.math.BigDecimal;

public class AccountDto extends Dto {
    public String iban;
    public BigDecimal balance;

    public AccountDto() {
    }

    public AccountDto(String iban, BigDecimal balance) {
        this.iban = iban;
        this.balance = balance;
    }

    public AccountDto(Account account) {
        id = account.getId();
        iban = account.getIban();
        balance = account.getBalance();
    }

    public Account pojo() {
        return new Account(id, iban, balance);
    }
}
