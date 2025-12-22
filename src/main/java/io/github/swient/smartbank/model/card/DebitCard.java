package io.github.swient.smartbank.model.card;

import io.github.swient.smartbank.model.account.Account;

public class DebitCard extends BankCard {
    public DebitCard(String cardNumber, String pinCode, Account account) {
        super(cardNumber, pinCode, account);
    }

    @Override
    public String getCardType() {
        return "DebitCard";
    }

    public boolean pay(double amount) {
        return getAccount().withdraw(amount);
    }
}
