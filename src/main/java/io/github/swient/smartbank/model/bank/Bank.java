package io.github.swient.smartbank.model.bank;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.github.swient.smartbank.model.account.User;
import io.github.swient.smartbank.model.account.Account;
import io.github.swient.smartbank.model.card.BankCard;
import io.github.swient.smartbank.model.card.DebitCard;

public class Bank {
    private final String name;
    private final Map<String, Account> issuedAccounts = new HashMap<>();
    private final Map<String, BankCard> issuedBankCards = new HashMap<>();
    private final Map<String, User> issuedUsers = new HashMap<>();

    public Bank(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public BankCard openAccount(User user, String pinCode) {
        String accountNumber = generateAccountNumber();
        Account account = new Account(accountNumber);
        issuedAccounts.put(accountNumber, account);
        user.addAccount(accountNumber, account);
        BankCard bankCard = new DebitCard(generateCardNumber(), pinCode, account);
        issuedBankCards.put(bankCard.getCardNumber(), bankCard);
        user.addBankCard(bankCard);
        return bankCard;
    }

    public Map<String, Account> getIssuedAccounts() {
        return issuedAccounts;
    }

    public Map<String, BankCard> getIssuedBankCards() {
        return issuedBankCards;
    }

    public Map<String, User> getIssuedUsers() {
        return issuedUsers;
    }

    public boolean hasUser(String userName) {
        return issuedUsers.containsKey(userName);
    }

    public void addUser(String userName, User user) {
        issuedUsers.put(userName, user);
    }

    public User getUser(String userName) {
        return issuedUsers.get(userName);
    }

    // 產生14碼純數字帳戶
    public static String generateAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 14; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    // 產生16碼純數字卡號
    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
