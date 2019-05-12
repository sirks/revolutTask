package com.revolut.task.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.lang.String.format;

public class Transaction {
    private final Account sender;
    private final Account receiver;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;


    public Transaction(Account sender, Account receiver, BigDecimal amount) {
        validate(sender, receiver, amount);
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    private static void validate(Account sender, Account receiver, BigDecimal amount) {
        if(sender.iban().equals(receiver.iban())){
            throw new RuntimeException(format("Cannot send money to its own account %s", sender.iban()));
        }
        //TODO Implement real transaction validation
    }
}
