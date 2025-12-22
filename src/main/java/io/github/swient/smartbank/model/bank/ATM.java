package io.github.swient.smartbank.model.bank;

import io.github.swient.smartbank.model.account.Account;
import io.github.swient.smartbank.model.account.Transaction;

public class ATM {
    private final Account account;

    public ATM(Account account) {
        this.account = account;
    }

    public boolean deposit(double amount) {
        boolean result = account.deposit(amount);
        if (result) {
            account.getTransactions().add(new Transaction(Transaction.Type.DEPOSIT, amount, account.getBalance()));
        }
        return result;
    }

    public boolean withdraw(double amount) {
        boolean result = account.withdraw(amount);
        if (result) {
            account.getTransactions().add(new Transaction(Transaction.Type.WITHDRAW, amount, account.getBalance()));
        }
        return result;
    }

    public boolean transfer(Account toAccount, double amount) {
        if (account.withdraw(amount)) {
            toAccount.deposit(amount);
            // 記錄轉出方
            account.getTransactions().add(new Transaction(Transaction.Type.TRANSFER_OUT, amount, account.getBalance(), toAccount.getAccountNumber()));
            // 記錄轉入方
            toAccount.getTransactions().add(new Transaction(Transaction.Type.TRANSFER_IN, amount, toAccount.getBalance(), account.getAccountNumber()));
            return true;
        }
        return false;
    }

    public double getBalance() {
        return account.getBalance();
    }
}
