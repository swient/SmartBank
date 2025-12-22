package io.github.swient.smartbank.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import io.github.swient.smartbank.model.card.BankCard;
import io.github.swient.smartbank.service.UserService;
import io.github.swient.smartbank.service.BankService;

public class RegisterController {
    private static final UserService userService = UserService.getInstance();
    private static final BankService bankService = BankService.getInstance();

    @FXML
    private TextField fullNameField;
    @FXML
    private TextField userNameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField pinCodeField;
    @FXML
    private ComboBox<String> bankCombo;
    @FXML
    private Label registerMsg;

    @FXML
    public void initialize() {
        bankCombo.getItems().clear();
        bankCombo.getItems().addAll(bankService.getBankMap().keySet());
    }

    @FXML
    protected void onRegisterClick() {
        String fullName = fullNameField.getText();
        String userName = userNameField.getText();
        String password = passwordField.getText();
        String pinCode = pinCodeField.getText();
        String bankName = bankCombo.getValue();
        if (fullName.isEmpty() || userName.isEmpty() || password.isEmpty() || pinCode.isEmpty() || bankName == null) {
            registerMsg.setText("請輸入帳號、姓名、密碼、PIN 碼並選擇銀行");
            return;
        }
        if (!pinCode.matches("\\d{6}")) {
            registerMsg.setText("PIN 碼必須為 6 位數字");
            return;
        }
        BankCard bankCard = userService.registerUser(bankName, fullName, userName, password, pinCode);
        if (bankCard == null) {
            registerMsg.setText("該銀行已存在相同帳號");
            return;
        }
        registerMsg.setText("開戶成功！\n帳戶：" + bankCard.getAccount().getAccountNumber() + "\n卡號：" + bankCard.getCardNumber());
        registerMsg.setTextFill(javafx.scene.paint.Color.GREEN);
    }

    @FXML
    protected void onLoginClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/io/github/swient/smartbank/view/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) fullNameField.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            registerMsg.setText("返回登入頁面失敗");
        }
    }
}
