package com.revolut.task.model.utils;

import static java.lang.String.format;

public class Iban {
    private final String number;

    public Iban(String number) {
        Iban.validate(number);
        this.number = number;
    }

    public String number() {
        return number;
    }


    private static void validate(String iban) {
        if (iban.length() < 16 || iban.length() > 28) {
            throw new RuntimeException(format("Incorrect number length %s", iban));
        }
        //TODO Implement real number validation
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Iban)) return false;
        Iban iban = (Iban) o;
        return number.equals(iban.number);
    }
}
