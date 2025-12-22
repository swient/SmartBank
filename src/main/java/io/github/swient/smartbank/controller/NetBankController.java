package io.github.swient.smartbank.controller;

import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import io.github.swient.smartbank.model.account.User;
import io.github.swient.smartbank.model.account.Account;
import io.github.swient.smartbank.model.account.Transaction;
import io.github.swient.smartbank.model.bank.ATM;
import io.github.swient.smartbank.model.card.BankCard;
import io.github.swient.smartbank.service.UserService;
import io.github.swient.smartbank.service.BankService;

public class NetBankController {
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
            if ("admin".equals(user.getUserName())) continue;
            toUsers.add(user.getUserName());
        }
    }

    private void updateAccountCombo() {
        String bankName = loginBank;
        String userName = loginUser;
        accountCombo.getItems().clear();
        if (bankName != null && userName != null) {
            User user = userService.getUser(bankName, userName);
            if (user != null) {
                for (Account account : user.getAccounts().values()) {
                    accountCombo.getItems().add(account.getAccountNumber());
                }
            }
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
    protected void setLoginUser(String bankName, String userName) {
        this.loginBank = bankName;
        this.loginUser = userName;
        if (bankLabel != null) bankLabel.setText(bankName);
        if (userLabel != null) userLabel.setText(userName);
        updateAccountCombo();
    }

    // 解析金額
    private Double parseAmount(String amountStr) {
        if (amountStr == null || amountStr.isEmpty()) {
            outputArea.appendText("請輸入金額\n");
            return null;
        }
        try {
            return Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            outputArea.appendText("金額格式錯誤\n");
            return null;
        }
    }

    // 取得帳戶
    private Account getAccount(String accountNumber) {
        String bankName = loginBank;
        String userName = loginUser;
        if (bankName == null || userName == null || accountNumber == null) {
            outputArea.appendText("請先登入並選擇來源帳戶\n");
            return null;
        }
        User user = userService.getUser(bankName, userName);
        if (user == null) {
            outputArea.appendText("查無來源使用者資料\n");
            return null;
        }
        Account account = user.getAccount(accountNumber);
        if (account == null || !account.getAccountNumber().equals(accountNumber)) {
            outputArea.appendText("查無來源帳戶資料或資料錯誤\n");
            return null;
        }
        return account;
    }

    @FXML
    protected void onDepositClick() {
        String accountNumber = accountCombo.getValue();
        Double amount = parseAmount(amountField.getText());
        if (amount == null) return;
        Account account = getAccount(accountNumber);
        if (account == null) return;
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
        String accountNumber = accountCombo.getValue();
        Double amount = parseAmount(amountField.getText());
        if (amount == null) return;
        Account account = getAccount(accountNumber);
        if (account == null) return;
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
        String accountNumber = accountCombo.getValue();
        Account account = getAccount(accountNumber);
        if (account == null) return;
        ATM atm = new ATM(account);
        outputArea.appendText("帳戶餘額：" + atm.getBalance() + "\n");
    }

    @FXML
    protected void onTransferClick() {
        String fromAccountNumber = accountCombo.getValue();
        String toUserName = toUserCombo.getValue();
        String toBankName = toBankCombo.getValue();
        String toAccountNumber = toAccountCombo.getValue();
        Double amount = parseAmount(amountField.getText());
        if (amount == null) return;
        Account fromAccount = getAccount(fromAccountNumber);
        if (fromAccount == null) return;
        if (toUserName == null || toBankName == null || toAccountNumber == null) {
            outputArea.appendText("請選擇目標使用者、銀行、帳戶\n");
            return;
        }
        User toUser = userService.getUser(toBankName, toUserName);
        Account toAccount = toUser.getAccount(toAccountNumber);
        if (toAccount == null || !toAccount.getAccountNumber().equals(toAccountNumber)) {
            outputArea.appendText("查無目標帳戶資料或資料錯誤\n");
            return;
        }
        if (fromAccount.equals(toAccount)) {
            outputArea.appendText("不能轉帳給自己同一帳戶\n");
            return;
        }
        ATM fromATM = new ATM(fromAccount);
        boolean result = fromATM.transfer(toAccount, amount, loginBank, toBankName);
        if (result) {
            outputArea.appendText("轉帳成功！帳戶餘額：" + fromATM.getBalance() + " (手續費: " + fromATM.getFee() + ")\n");
        } else {
            outputArea.appendText("轉帳失敗，請確認餘額或資料\n");
        }
    }

    @FXML
    private void onAddAccountClick() {
        String bankName = loginBank;
        String userName = loginUser;
        if (bankName == null || userName == null) {
            outputArea.appendText("請先登入\n");
            return;
        }

        Dialog<String> pinCodeDialog = new Dialog<>();
        pinCodeDialog.setTitle("設定 PIN 碼");
        pinCodeDialog.setHeaderText("為您的新帳戶設定 6 位數 PIN 碼");
        PasswordField pinField = new PasswordField();
        pinField.setPromptText("請輸入數字 PIN 碼");

        // 限制只能輸入數字且長度不超過 6
        pinField.textProperty().addListener((_, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                pinField.setText(newVal.replaceAll("\\D", ""));
            }
            if (newVal.length() > 6) pinField.setText(oldVal);
        });

        pinCodeDialog.getDialogPane().setContent(new VBox(10, new Label("PIN 碼："), pinField));
        ButtonType confirmButton = new ButtonType("確定", ButtonBar.ButtonData.OK_DONE);
        pinCodeDialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);
        pinCodeDialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButton) return pinField.getText();
            return null;
        });

        String pinCode = pinCodeDialog.showAndWait().orElse("");
        if (pinCode.isEmpty()) {
            outputArea.appendText("未輸入 PIN 碼，取消新增帳戶\n");
            return;
        }
        if (!pinCode.matches("\\d{6}")) {
            outputArea.appendText("PIN 碼必須為 6 位數字\n");
            return;
        }

        BankCard bankCard = userService.registerAccount(bankName, userName, pinCode);
        Account account = bankCard.getAccount();
        updateAccountCombo();
        outputArea.appendText("新增帳戶成功！\n帳戶：" + account.getAccountNumber() + "\n卡號：" + bankCard.getCardNumber() + "\n");
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

    @FXML
    private void onShowTransactionsClick() {
        String accountNumber = accountCombo.getValue();
        Account account = getAccount(accountNumber);
        if (account == null) return;
        var transactions = account.getTransactions();
        if (transactions.isEmpty()) {
            outputArea.appendText("無交易紀錄\n");
            return;
        }
        outputArea.appendText("--- 交易紀錄 ---\n");
        for (Transaction tx : transactions) {
            String formattedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(tx.getDateTime());
            outputArea.appendText(String.format("%s | %s | 金額: %.2f %s | 餘額: %.2f %s\n",
                formattedDate,
                tx.getType(),
                tx.getAmount(),
                tx.getFee() > 0 ? ("| 手續費: " + tx.getFee()) : "",
                tx.getBalanceAfter(),
                tx.getRelatedAccount() != null ? ("| 目標帳號: " + tx.getRelatedAccount()) : ""
            ));
        }
        outputArea.appendText("--- 紀錄結尾 ---\n");
    }
}
