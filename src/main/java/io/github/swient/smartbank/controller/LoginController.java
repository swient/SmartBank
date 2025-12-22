package io.github.swient.smartbank.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import io.github.swient.smartbank.service.UserService;
import io.github.swient.smartbank.service.BankService;

public class LoginController {
    private static final UserService userService = UserService.getInstance();
    private static final BankService bankService = BankService.getInstance();

    @FXML
    private Label userLabel;
    @FXML
    private TextField userNameField;
    @FXML
    private Label passwordLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> modeCombo;
    @FXML
    private ComboBox<String> bankCombo;
    @FXML
    private Button loginButton;
    @FXML
    private Label loginMsg;

    @FXML
    public void initialize() {
        bankCombo.getItems().clear();
        bankCombo.getItems().addAll(bankService.getBankMap().keySet());
        modeCombo.getItems().clear();
        modeCombo.getItems().addAll("網路銀行", "線上 ATM");
        modeCombo.getSelectionModel().select("網路銀行");
        modeCombo.setOnAction(_ -> handleModeComboChange());
        handleModeComboChange();
    }

    @FXML
    private void handleModeComboChange() {
        String mode = modeCombo.getSelectionModel().getSelectedItem();
        if (mode == null || mode.equals("網路銀行")) {
            loginButton.setOnAction(_ -> onNetBankLoginClick());
            userLabel.setText("帳號");
            passwordLabel.setText("密碼");
        } else {
            loginButton.setOnAction(_ -> onATMLoginClick());
            userLabel.setText("卡號");
            passwordLabel.setText("PIN 碼");
        }
    }

    @FXML
    protected void onNetBankLoginClick() {
        String userName = userNameField.getText();
        String password = passwordField.getText();
        String bankName = bankCombo.getValue();
        if (userName.isEmpty() || password.isEmpty() || bankName == null) {
            loginMsg.setText("請輸入帳號、密碼並選擇銀行");
            return;
        }
        // 管理員帳號判斷
        boolean valid = userService.validateNetBankLogin(bankName, userName, password);
        if ("admin".equals(userName) && valid) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/io/github/swient/smartbank/view/admin.fxml"));
                Scene scene = new Scene(loader.load());
                // 傳遞登入資訊給管理員頁控制器
                AdminController adminController = loader.getController();
                adminController.setLoginUser(bankName);
                Stage stage = (Stage) userNameField.getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException e) {
                loginMsg.setText("管理頁面載入失敗");
            }
            return;
        }
        if (valid) {
            loginMsg.setText("登入成功");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/io/github/swient/smartbank/view/netbank.fxml"));
                Scene scene = new Scene(loader.load());
                // 傳遞登入資訊給網路銀行頁控制器
                NetBankController netBankController = loader.getController();
                netBankController.setLoginUser(bankName, userName);
                Stage stage = (Stage) userNameField.getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException e) {
                loginMsg.setText("頁面載入失敗");
            }
        } else {
            loginMsg.setText("帳號或密碼錯誤");
        }
    }

    protected void onATMLoginClick() {
        String cardNumber = userNameField.getText();
        String pinCode = passwordField.getText();
        String bankName = bankCombo.getValue();
        if (cardNumber.isEmpty() || pinCode.isEmpty() || bankName == null) {
            loginMsg.setText("請輸入卡號、PIN 碼並選擇銀行");
            return;
        }
        boolean valid = userService.validateATMLogin(bankName, cardNumber, pinCode);
        if (valid) {
            loginMsg.setText("登入成功");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/io/github/swient/smartbank/view/atm.fxml"));
                Scene scene = new Scene(loader.load());
                // 傳遞登入資訊給 ATM 頁控制器
                ATMController atmController = loader.getController();
                atmController.setLoginUser(bankName, cardNumber);
                Stage stage = (Stage) userNameField.getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException e) {
                loginMsg.setText("頁面載入失敗");
            }
        } else {
            loginMsg.setText("卡號或 PIN 碼錯誤");
        }
    }

    @FXML
    protected void onRegisterClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/io/github/swient/smartbank/view/register.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) userNameField.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            loginMsg.setText("開戶頁面載入失敗");
        }
    }
}
