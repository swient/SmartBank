package io.github.swient.smartbank.model.account;

import java.util.HashMap;
import java.util.Map;

import io.github.swient.smartbank.model.card.BankCard;

public class User {
    private final String fullName;
    private final String userName;
    private final String password;
    private Account account;
    private final Map<String, BankCard> bankCards = new HashMap<>();

    public User(String fullName, String userName, String password) {
        this.fullName = fullName;
        this.userName = userName;
        this.password = password;
        this.account = null;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Account getAccount() {
        return account;
    }

    public void addBankCard(BankCard bankCard) {
        bankCards.put(bankCard.getCardNumber(), bankCard);
    }

    public Map<String, BankCard> getBankCards() {
        return bankCards;
    }
}
