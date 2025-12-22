package io.github.swient.smartbank.service;

import java.util.HashMap;
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
            Map<String, User> userMap = instance.bankUserMap.computeIfAbsent(bankName, _ -> new HashMap<>());
            if (!userMap.containsKey("admin")) {
                userMap.put("admin", new User("管理員", "admin", instance.hashPassword("admin")));
            }
        }
    }

    public static UserService getInstance() {
        return instance;
    }

    private final Map<String, Map<String, User>> bankUserMap = new HashMap<>();

    public BankCard registerUser(String bankName, String fullName, String userName, String password, String pinCode) {
        Map<String, User> userMap = bankUserMap.computeIfAbsent(bankName, _ -> new HashMap<>());
        if (userMap.containsKey(userName)) return null;
        User user = new User(fullName, userName, hashPassword(password));
        userMap.put(userName, user);
        Bank bank = bankService.getBank(bankName);
        return bank.openAccount(user, hashPassword(pinCode));
    }

    public BankCard registerAccount(String bankName, String userName, String pinCode) {
        User user = getUser(bankName, userName);
        Bank bank = bankService.getBank(bankName);
        if (user == null || bank == null) return null;
        return bank.openAccount(user, hashPassword(pinCode));
    }

    public User getUser(String bankName, String userName) {
        Map<String, User> userMap = bankUserMap.get(bankName);
        if (userMap == null) return null;
        return userMap.get(userName);
    }

    public boolean validateNetBankLogin(String bankName, String userName, String password) {
        Map<String, User> userMap = bankUserMap.get(bankName);
        if (userMap == null) return false;
        User user = userMap.get(userName);
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

    public Map<String, User> getBankUserMap(String bank) {
        return bankUserMap.getOrDefault(bank, new HashMap<>());
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
