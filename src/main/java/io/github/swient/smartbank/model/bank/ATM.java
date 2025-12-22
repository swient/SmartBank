package io.github.swient.smartbank.model.bank;

import io.github.swient.smartbank.model.account.Account;
import io.github.swient.smartbank.model.account.User;
import io.github.swient.smartbank.model.card.BankCard;

public class ATM {
    private final Account account;

    public ATM(Account account) {
        this.account = account;
    }

    public boolean deposit(double amount) {
        return account.deposit(amount);
    }

    public boolean withdraw(double amount) {
        return account.withdraw(amount);
    }

    public boolean transfer(Account toAccount, double amount) {
        if (account.withdraw(amount)) {
            return toAccount.deposit(amount);
        }
        return false;
    }

    public double getBalance() {
        return account.getBalance();
    }

    public static BankCard createAccount(User user, Bank bank) {
        return bank.openAccount(user);
    }
}
