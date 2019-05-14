package com.revolut.task.api.services;

import com.google.inject.Singleton;

@Singleton
public class IbanService {
    public boolean isValid(String iban) {
        if (iban.length() < 1 || iban.length() > 28) {
            return false;
        }
        return true;
    }
}
