package com.fxapp.bankapp.Models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class CheckingAccount extends Account{

    // Number of transactions a client is allowed to do per day
    private final IntegerProperty transactionLimit;

    public CheckingAccount(String owner, String accountNumber, double balance, int tLimit) {
        super(owner, accountNumber, balance);
        this.transactionLimit = new SimpleIntegerProperty(this, "Limit", tLimit);
    }

    public IntegerProperty getTransactionLimitProp() {
        return this.transactionLimit;
    }

    @Override
    public String toString() {
        return getAccountNumberProperty().get();
    }
}
