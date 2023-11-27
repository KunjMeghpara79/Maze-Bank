package com.fxapp.bankapp.Models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class SavingsAccount extends Account {

    // Withdraw limit from the savings account
    private final DoubleProperty withdrawLimit;

    public SavingsAccount(String owner, String accountNumber, double balance, double wLimit) {
        super(owner, accountNumber, balance);
        this.withdrawLimit = new SimpleDoubleProperty(this, "Withdrawal Limit", wLimit);
    }

    public DoubleProperty getWithdrawLimitProp() {
        return this.withdrawLimit;
    }

    @Override
    public String toString() {
        return getAccountNumberProperty().get();
    }
}
