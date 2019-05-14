package com.revolut.task.api.dto;

import com.revolut.task.tables.pojos.Transaction;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TransactionDto extends Dto {
    public BigDecimal amount;
    public Integer fromAccountId;
    public Integer toAccountId;
    public Timestamp timestamp;

    public TransactionDto() {
    }

    public TransactionDto(Transaction transaction) {
        id = transaction.getId();
        amount = transaction.getAmount();
        fromAccountId = transaction.getFromAccountId();
        toAccountId = transaction.getToAccountId();
        timestamp = transaction.getTimestamp();
    }

    public Transaction pojo() {
        return new Transaction(id, amount, fromAccountId, toAccountId, timestamp);
    }

    @Override
    public String toString() {
        return "TransactionDto{" +
                "amount=" + amount +
                ", fromAccountId=" + fromAccountId +
                ", toAccountId=" + toAccountId +
                ", timestamp=" + timestamp +
                ", id=" + id +
                '}';
    }
}
