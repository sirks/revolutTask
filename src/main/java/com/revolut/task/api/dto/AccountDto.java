package com.revolut.task.api.dto;

import com.revolut.task.enums.AccountCurrency;
import com.revolut.task.tables.pojos.Account;

import java.math.BigDecimal;

public class AccountDto extends Dto {
    public String iban;
    public AccountCurrency currency;
    public BigDecimal balance;

    public AccountDto() {
    }

    public AccountDto(Account account) {
        id = account.getId();
        iban = account.getIban();
        currency = account.getCurrency();
        balance = account.getBalance();
    }

    public Account pojo() {
        return new Account(id, iban, currency, balance);
    }
}
