package com.revolut.task.model;

import com.revolut.task.model.utils.Iban;
import com.revolut.task.model.utils.Currency;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

public class Account {
    private final Iban iban;
    private Currency currency;
    private BigDecimal amount;

    public Account(Iban iban, Currency currency) {
        this.iban = iban;
        this.currency = currency;
        this.amount = ZERO;
    }

    public void add(BigDecimal amount) {
        this.amount = this.amount.add(amount);
    }

    public void subtract(BigDecimal amount) {
//        TODO validate
        this.amount = this.amount.subtract(amount);
    }

    public Iban iban() {
        return iban;
    }

    public Currency currency() {
        return currency;
    }

    public BigDecimal amount() {
        return amount;
    }

}
