package io.github.swient.smartbank.service;

import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.github.swient.smartbank.model.account.User;
import io.github.swient.smartbank.model.bank.Bank;
import io.github.swient.smartbank.model.card.BankCard;

public class UserService {
    private static final UserService instance = new UserService();
    private static final BankService bankService = BankService.getInstance();
    // 每個銀行自動註冊管理員帳號
    static {
        for (String bankName : bankService.getBankMap().keySet()) {
            Bank bank = bankService.getBank(bankName);
            if (!bank.hasUser("admin")) {
                bank.addUser("admin", new User("管理員", "admin", instance.hashPassword("admin")));
            }
        }
    }

    public static UserService getInstance() { return instance; }


    public BankCard registerUser(String bankName, String fullName, String userName, String password, String pinCode) {
        Bank bank = bankService.getBank(bankName);
        if (bank == null || bank.hasUser(userName)) return null;
        User user = new User(fullName, userName, hashPassword(password));
        bank.addUser(userName, user);
        return bank.openAccount(user, hashPassword(pinCode));
    }

    public BankCard registerAccount(String bankName, String userName, String pinCode) {
        Bank bank = bankService.getBank(bankName);
        if (bank == null) return null;
        User user = bank.getUser(userName);
        if (user == null) return null;
        return bank.openAccount(user, hashPassword(pinCode));
    }

    public User getUser(String bankName, String userName) {
        Bank bank = bankService.getBank(bankName);
        if (bank == null) return null;
        return bank.getUser(userName);
    }

    public boolean validateNetBankLogin(String bankName, String userName, String password) {
        Bank bank = bankService.getBank(bankName);
        if (bank == null) return false;
        User user = bank.getUser(userName);
        if (user == null) return false;
        String hashed = hashPassword(password);
        return user.getPassword().equals(hashed);
    }

    public boolean validateATMLogin(String bankName, String cardNumber, String pinCode) {
        Bank bank = bankService.getBank(bankName);
        BankCard bankCard = bank.getIssuedBankCards().get(cardNumber);
        if (bankCard == null) return false;
        String hashed = hashPassword(pinCode);
        return bankCard.getPinCode().equals(hashed);
    }

    public Map<String, User> getBankUserMap(String bankName) {
        Bank bank = bankService.getBank(bankName);
        if (bank == null) return null;
        return bank.getIssuedUsers();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
