package io.github.swient.smartbank.service;

import java.util.HashMap;
import java.util.Map;

import io.github.swient.smartbank.model.bank.Bank;

public class BankService {
    private static final BankService instance = new BankService();

    public static BankService getInstance() {
        return instance;
    }

    // 預設建立兩家銀行
    private final Map<String, Bank> bankMap = new HashMap<>();
    {
        bankMap.put("A銀行", new Bank("A銀行"));
        bankMap.put("B銀行", new Bank("B銀行"));
    }

    public Bank getOrCreateBank(String bankName) {
        return bankMap.computeIfAbsent(bankName, Bank::new);
    }

    public Bank getBank(String bankName) {
        return bankMap.get(bankName);
    }

    public Map<String, Bank> getBankMap() {
        return bankMap;
    }
}
