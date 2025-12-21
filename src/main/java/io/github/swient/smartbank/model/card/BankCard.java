package io.github.swient.smartbank.model.card;

import io.github.swient.smartbank.model.account.Account;

public abstract class BankCard {
    private final String cardNumber;
    private final Account account;

    public BankCard(String cardNumber, Account account) {
        this.cardNumber = cardNumber;
        this.account = account;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public Account getAccount() {
        return account;
    }

    public abstract String getCardType();
}
