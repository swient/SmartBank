package io.github.swient.smartbank.model.card;

import io.github.swient.smartbank.model.account.Account;

public abstract class BankCard {
    private final String cardNumber;
    private final String pinCode;
    private final Account account;

    public BankCard(String cardNumber, String pinCode, Account account) {
        this.cardNumber = cardNumber;
        this.pinCode = pinCode;
        this.account = account;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPinCode() {
        return  pinCode;
    }

    public Account getAccount() {
        return account;
    }

    public abstract String getCardType();
}
