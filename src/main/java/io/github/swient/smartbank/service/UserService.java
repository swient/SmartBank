package io.github.swient.smartbank.service;

import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.github.swient.smartbank.model.account.User;

public class UserService {
    private static final UserService instance = new UserService();

    public static UserService getInstance() {
        return instance;
    }

    private final Map<String, Map<String, User>> bankUserMap = new HashMap<>();

    public boolean registerUser(String bankName, String fullName, String userName, String password) {
        Map<String, User> userMap = bankUserMap.computeIfAbsent(bankName, _ -> new HashMap<>());
        if (userMap.containsKey(userName)) return false;
        User user = new User(fullName, userName, hashPassword(password));
        userMap.put(userName, user);
        return true;
    }

    public User getUser(String bankName, String userName) {
        Map<String, User> userMap = bankUserMap.get(bankName);
        if (userMap == null) return null;
        return userMap.get(userName);
    }

    public boolean validateLogin(String bankName, String userName, String password) {
        Map<String, User> userMap = bankUserMap.get(bankName);
        if (userMap == null) return false;
        User user = userMap.get(userName);
        if (user == null) return false;
        String hashed = hashPassword(password);
        return user.getPassword().equals(hashed);
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
