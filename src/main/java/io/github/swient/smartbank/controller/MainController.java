package io.github.swient.smartbank.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import io.github.swient.smartbank.model.account.User;
import io.github.swient.smartbank.model.account.Account;
import io.github.swient.smartbank.model.bank.Bank;
import io.github.swient.smartbank.model.bank.ATM;
import io.github.swient.smartbank.service.UserService;
import io.github.swient.smartbank.service.BankService;

public class MainController {
    private String loginBank = null;
    private String loginUser = null;
    private final ObservableList<String> toUsers = FXCollections.observableArrayList();
    private final ObservableList<String> banks = FXCollections.observableArrayList();

    private static final UserService userService = UserService.getInstance();
    private static final BankService bankService = BankService.getInstance();

    @FXML
    private Label bankLabel;
    @FXML
    private Label userLabel;
    @FXML
    private ComboBox<String> accountCombo;
    @FXML
    private ComboBox<String> toBankCombo;
    @FXML
    private ComboBox<String> toUserCombo;
    @FXML
    private ComboBox<String> toAccountCombo;
    @FXML
    private TextField amountField;
    @FXML
    private TextArea outputArea;

    @FXML
    public void initialize() {
        banks.clear();
        banks.addAll(bankService.getBankMap().keySet());
        toBankCombo.setItems(banks);
        toBankCombo.setOnAction(_ -> {
            refreshToUsers();
            toUserCombo.setItems(toUsers);
            updateToAccountCombo();
        });
        toUserCombo.setOnAction(_ -> updateToAccountCombo());
        refreshToUsers();
        toUserCombo.setItems(toUsers);
        updateAccountCombo();
        updateToAccountCombo();
    }

    private void refreshToUsers() {
        toUsers.clear();
        String bankName = toBankCombo.getValue();
        if (bankName == null) return;
        for (User user : userService.getBankUserMap(bankName).values()) {
            toUsers.add(user.getUserName());
        }
    }

    private void updateAccountCombo() {
        String userName = loginUser;
        String bankName = loginBank;
        accountCombo.getItems().clear();
        if (userName != null && bankName != null) {
            User user = userService.getUser(bankName, userName);
            if (user != null && user.getAccount() != null) {
                accountCombo.getItems().add(user.getAccount().getAccountNumber());
            }
        }
    }

    private void updateToAccountCombo() {
        String toUserName = toUserCombo.getValue();
        String toBankName = toBankCombo.getValue();
        toAccountCombo.getItems().clear();
        if (toUserName != null && toBankName != null) {
            User toUser = userService.getUser(toBankName, toUserName);
            if (toUser != null && toUser.getAccount() != null) {
                toAccountCombo.getItems().add(toUser.getAccount().getAccountNumber());
            }
        }
    }

    // 由登入頁呼叫，設定登入資訊
    protected void setLoginUser(String bank, String user) {
        this.loginBank = bank;
        this.loginUser = user;
        if (bankLabel != null) bankLabel.setText(bank);
        if (userLabel != null) userLabel.setText(user);
        updateAccountCombo();
    }

    @FXML
    protected void onDepositClick() {
        String userName = loginUser;
        String bankName = loginBank;
        String accountNumber = accountCombo.getValue();
        String amountStr = amountField.getText();
        if (userName == null || bankName == null || accountNumber == null || amountStr.isEmpty()) {
            outputArea.appendText("請選擇使用者、銀行、帳戶並輸入金額\n");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            outputArea.appendText("金額格式錯誤\n");
            return;
        }
        User user = userService.getUser(bankName, userName);
        Bank bank = bankService.getOrCreateBank(bankName);
        Account account = user.getAccount();
        if (bank == null || account == null || !account.getAccountNumber().equals(accountNumber)) {
            outputArea.appendText("資料錯誤\n");
            return;
        }
        ATM atm = new ATM(account);
        boolean result = atm.deposit(amount);
        if (result) {
            outputArea.appendText("存款成功！帳戶餘額：" + account.getBalance() + "\n");
        } else {
            outputArea.appendText("存款失敗，請確認資料\n");
        }
    }

    @FXML
    protected void onWithdrawClick() {
        String userName = loginUser;
        String bankName = loginBank;
        String accountNumber = accountCombo.getValue();
        String amountStr = amountField.getText();
        if (userName == null || bankName == null || accountNumber == null || amountStr.isEmpty()) {
            outputArea.appendText("請選擇使用者、銀行、帳戶並輸入金額\n");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            outputArea.appendText("金額格式錯誤\n");
            return;
        }
        User user = userService.getUser(bankName, userName);
        Bank bank = bankService.getOrCreateBank(bankName);
        Account account = user.getAccount();
        if (bank == null || account == null || !account.getAccountNumber().equals(accountNumber)) {
            outputArea.appendText("資料錯誤\n");
            return;
        }
        ATM atm = new ATM(account);
        boolean result = atm.withdraw(amount);
        if (result) {
            outputArea.appendText("提款成功！帳戶餘額：" + account.getBalance() + "\n");
        } else {
            outputArea.appendText("提款失敗，請確認餘額或資料\n");
        }
    }

    @FXML
    private void onBalanceClick() {
        String userName = loginUser;
        String bankName = loginBank;
        String accountNumber = accountCombo.getValue();
        if (userName == null || bankName == null || accountNumber == null) {
            outputArea.appendText("請選擇使用者、銀行、帳戶\n");
            return;
        }
        User user = userService.getUser(bankName, userName);
        if (user == null || user.getAccount() == null || !user.getAccount().getAccountNumber().equals(accountNumber)) {
            outputArea.appendText("查無帳戶資料\n");
            return;
        }
        Account account = user.getAccount();
        outputArea.appendText("帳戶餘額：" + account.getBalance() + "\n");
    }

    @FXML
    protected void onTransferClick() {
        String fromUserName = loginUser;
        String fromBankName = loginBank;
        String fromAccountNumber = accountCombo.getValue();
        String toUserName = toUserCombo.getValue();
        String toBankName = toBankCombo.getValue();
        String toAccountNumber = toAccountCombo.getValue();
        String amountStr = amountField.getText();
        if (fromUserName == null || fromBankName == null || fromAccountNumber == null ||
            toUserName == null || toBankName == null || toAccountNumber == null || amountStr.isEmpty()) {
            outputArea.appendText("請選擇來源與目標使用者、銀行、帳戶並輸入金額\n");
            return;
        }
        if (fromBankName.equals(toBankName) && fromUserName.equals(toUserName) && fromAccountNumber.equals(toAccountNumber)) {
            outputArea.appendText("不能轉帳給自己同一帳戶\n");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            outputArea.appendText("金額格式錯誤\n");
            return;
        }
        User fromUser = userService.getUser(fromBankName, fromUserName);
        Bank fromBank = bankService.getOrCreateBank(fromBankName);
        Account fromAccount = fromUser.getAccount();
        if (fromAccount == null || !fromAccount.getAccountNumber().equals(fromAccountNumber)) {
            fromAccount = null;
        }
        User toUser = userService.getUser(toBankName, toUserName);
        Bank toBank = bankService.getOrCreateBank(toBankName);
        Account toAccount = toUser.getAccount();
        if (toAccount == null || !toAccount.getAccountNumber().equals(toAccountNumber)) {
            toAccount = null;
        }
        if (fromBank == null || fromAccount == null || toBank == null || toAccount == null) {
            outputArea.appendText("資料錯誤或對方尚未開戶\n");
            return;
        }
        ATM fromATM = new ATM(fromAccount);
        boolean result = fromATM.transfer(toAccount, amount);
        if (result) {
            outputArea.appendText("轉帳成功！來源帳戶餘額：" + fromAccount.getBalance() + "，目標帳戶餘額：" + toAccount.getBalance() + "\n");
        } else {
            outputArea.appendText("轉帳失敗，請確認餘額或資料\n");
        }
    }

    @FXML
    private void onLogoutClick() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/io/github/swient/smartbank/view/login.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
            javafx.stage.Stage stage = (javafx.stage.Stage) outputArea.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            outputArea.setText("登出失敗");
        }
    }
}
