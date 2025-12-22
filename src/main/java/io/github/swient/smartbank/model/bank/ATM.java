package io.github.swient.smartbank.model.bank;

import io.github.swient.smartbank.model.account.Account;
import io.github.swient.smartbank.model.account.Transaction;

public class ATM {
    private final Account account;
    private double fee;

    public ATM(Account account) {
        this.account = account;
        this.fee = 0;
    }

    public boolean deposit(double amount) {
        fee = 0;
        boolean result = account.deposit(amount);
        if (result) {
            account.getTransactions().add(new Transaction(Transaction.Type.DEPOSIT, amount, account.getBalance()));
        }
        return result;
    }

    public boolean withdraw(double amount) {
        fee = 0;
        boolean result = account.withdraw(amount);
        if (result) {
            account.getTransactions().add(new Transaction(Transaction.Type.WITHDRAW, amount, account.getBalance()));
        }
        return result;
    }

    public double getFee() {
        return fee;
    }

    public boolean transfer(Account toAccount, double amount, String fromBankName, String toBankName) {
        fee = 0;
        if (!fromBankName.equals(toBankName)) {
            if (amount <= 500) {
                fee = 0;
            } else if (amount <= 1000) {
                fee = 10;
            } else {
                fee = 15;
            }
        }
        double total = amount + fee;
        if (account.withdraw(total)) {
            toAccount.deposit(amount);
            // 記錄轉出方
            account.getTransactions().add(new Transaction(Transaction.Type.TRANSFER_OUT, amount, fee, account.getBalance(), toAccount.getAccountNumber()));
            // 記錄轉入方
            toAccount.getTransactions().add(new Transaction(Transaction.Type.TRANSFER_IN, amount, 0, toAccount.getBalance(), account.getAccountNumber()));
            return true;
        }
        return false;
    }

    public double getBalance() {
        return account.getBalance();
    }
}
