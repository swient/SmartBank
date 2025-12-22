package io.github.swient.smartbank.model.account;

import java.time.LocalDateTime;

public class Transaction {
    public enum Type {
        DEPOSIT, WITHDRAW, TRANSFER_IN, TRANSFER_OUT
    }

    private final Type type;
    private final double amount;
    private final LocalDateTime dateTime;
    private final double balanceAfter;
    private final String relatedAccount;

    public Transaction(Type type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.dateTime = LocalDateTime.now();
        this.balanceAfter = balanceAfter;
        this.relatedAccount = null;
    }

    public Transaction(Type type, double amount, double balanceAfter, String relatedAccount) {
        this.type = type;
        this.amount = amount;
        this.dateTime = LocalDateTime.now();
        this.balanceAfter = balanceAfter;
        this.relatedAccount = relatedAccount;
    }

    public Type getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String getRelatedAccount() {
        return relatedAccount;
    }
}
