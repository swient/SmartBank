package io.github.swient.smartbank.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import io.github.swient.smartbank.service.UserService;
import io.github.swient.smartbank.service.BankService;
import io.github.swient.smartbank.model.account.User;
import io.github.swient.smartbank.model.account.Account;
import io.github.swient.smartbank.model.bank.ATM;
import io.github.swient.smartbank.model.bank.Bank;
import io.github.swient.smartbank.model.card.BankCard;

public class ATMController {
    private String loginBank = null;
    private String loginCard = null;
    private final ObservableList<String> toUsers = FXCollections.observableArrayList();
    private final ObservableList<String> banks = FXCollections.observableArrayList();

    private static final UserService userService = UserService.getInstance();
    private static final BankService bankService = BankService.getInstance();

    @FXML
    private Label bankLabel;
    @FXML
    private Label cardLabel;
    @FXML
    private Label accountLabel;
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

    private void updateToAccountCombo() {
        String toUserName = toUserCombo.getValue();
        String toBankName = toBankCombo.getValue();
        toAccountCombo.getItems().clear();
        if (toUserName != null && toBankName != null) {
            User toUser = userService.getUser(toBankName, toUserName);
            if (toUser != null) {
                for (Account account : toUser.getAccounts().values()) {
                    toAccountCombo.getItems().add(account.getAccountNumber());
                }
            }
        }
    }

    // 由登入頁呼叫，設定登入資訊
    protected void setLoginUser(String bankName, String cardNumber) {
        this.loginBank = bankName;
        this.loginCard = cardNumber;
        if (bankLabel != null) bankLabel.setText(bankName);
        if (cardLabel != null) cardLabel.setText(cardNumber);
        Bank bank = bankService.getBank(bankName);
        BankCard bankCard = bank.getIssuedBankCards().get(cardNumber);
        Account account = bankCard.getAccount();
        if (accountLabel != null) accountLabel.setText(account.getAccountNumber());
        updateToAccountCombo();
    }

    @FXML
    protected void onDepositClick() {
        String cardNumber = loginCard;
        String bankName = loginBank;
        String amountStr = amountField.getText();
        if (cardNumber == null || bankName == null || amountStr.isEmpty()) {
            outputArea.appendText("請選擇卡號、銀行並輸入金額\n");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            outputArea.appendText("金額格式錯誤\n");
            return;
        }
        Bank bank = bankService.getBank(bankName);
        BankCard bankCard = bank.getIssuedBankCards().get(cardNumber);
        Account account = bankCard.getAccount();
        if (account == null) {
            outputArea.appendText("查無帳戶資料\n");
            return;
        }
        ATM atm = new ATM(account);
        boolean result = atm.deposit(amount);
        if (result) {
            outputArea.appendText("存款成功！帳戶餘額：" + atm.getBalance() + "\n");
        } else {
            outputArea.appendText("存款失敗，請確認資料\n");
        }
    }

    @FXML
    protected void onWithdrawClick() {
        String cardNumber = loginCard;
        String bankName = loginBank;
        String amountStr = amountField.getText();
        if (cardNumber == null || bankName == null || amountStr.isEmpty()) {
            outputArea.appendText("請選擇卡號、銀行並輸入金額\n");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            outputArea.appendText("金額格式錯誤\n");
            return;
        }
        Bank bank = bankService.getBank(bankName);
        BankCard bankCard = bank.getIssuedBankCards().get(cardNumber);
        Account account = bankCard.getAccount();
        if (account == null) {
            outputArea.appendText("查無帳戶資料\n");
            return;
        }
        ATM atm = new ATM(account);
        boolean result = atm.withdraw(amount);
        if (result) {
            outputArea.appendText("提款成功！帳戶餘額：" + atm.getBalance() + "\n");
        } else {
            outputArea.appendText("提款失敗，請確認餘額或資料\n");
        }
    }

    @FXML
    private void onBalanceClick() {
        String cardNumber = loginCard;
        String bankName = loginBank;
        if (cardNumber == null || bankName == null) {
            outputArea.appendText("請選擇卡號、銀行\n");
            return;
        }
        Bank bank = bankService.getBank(bankName);
        BankCard bankCard = bank.getIssuedBankCards().get(cardNumber);
        Account account = bankCard.getAccount();
        if (account == null) {
            outputArea.appendText("查無帳戶資料\n");
            return;
        }
        ATM atm = new ATM(account);
        outputArea.appendText("帳戶餘額：" + atm.getBalance() + "\n");
    }

    @FXML
    protected void onTransferClick() {
        String fromCardNumber = loginCard;
        String fromBankName = loginBank;
        String toUserName = toUserCombo.getValue();
        String toBankName = toBankCombo.getValue();
        String toAccountNumber = toAccountCombo.getValue();
        String amountStr = amountField.getText();
        if (fromCardNumber == null || fromBankName == null) {
            outputArea.appendText("請先登入");
            return;
        }
        if (toUserName == null || toBankName == null || toAccountNumber == null || amountStr.isEmpty()) {
            outputArea.appendText("請選擇目標使用者、銀行、帳戶並輸入金額\n");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            outputArea.appendText("金額格式錯誤\n");
            return;
        }
        Bank fromBank = bankService.getBank(fromBankName);
        BankCard fromBankCard = fromBank.getIssuedBankCards().get(fromCardNumber);
        Account fromAccount = fromBankCard.getAccount();
        if (fromAccount == null) {
            outputArea.appendText("查無來源帳戶資料\n");
            return;
        }
        User toUser = userService.getUser(toBankName, toUserName);
        Account toAccount = toUser.getAccount(toAccountNumber);
        if (toAccount == null || !toAccount.getAccountNumber().equals(toAccountNumber)) {
            outputArea.appendText("查無目標帳戶資料\n");
            return;
        }
        if (fromAccount.equals(toAccount)) {
            outputArea.appendText("不能轉帳給自己同一帳戶\n");
            return;
        }
        ATM fromATM = new ATM(fromAccount);
        boolean result = fromATM.transfer(toAccount, amount);
        if (result) {
            outputArea.appendText("轉帳成功！來源帳戶餘額：" + fromATM.getBalance() + "，目標帳戶餘額：" + new ATM(toAccount).getBalance() + "\n");
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
