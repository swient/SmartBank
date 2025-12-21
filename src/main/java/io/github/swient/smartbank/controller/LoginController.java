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
    private TextField userNameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> bankCombo;
    @FXML
    private Label loginMsg;

    @FXML
    public void initialize() {
        bankCombo.getItems().clear();
        bankCombo.getItems().addAll(bankService.getBankMap().keySet());
    }

    @FXML
    protected void onLoginClick() {
        String userName = userNameField.getText();
        String password = passwordField.getText();
        String bank = bankCombo.getValue();
        if (userName.isEmpty() || password.isEmpty() || bank == null) {
            loginMsg.setText("請輸入帳號、密碼並選擇銀行");
            return;
        }
        boolean valid = userService.validateLogin(bank, userName, password);
        if (valid) {
            loginMsg.setText("登入成功");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/io/github/swient/smartbank/view/main.fxml"));
                Scene scene = new Scene(loader.load());
                // 傳遞登入資訊給主頁控制器
                MainController mainController = loader.getController();
                mainController.setLoginUser(bank, userName);
                Stage stage = (Stage) userNameField.getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException e) {
                loginMsg.setText("頁面載入失敗");
            }
        } else {
            loginMsg.setText("帳號或密碼錯誤");
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
