package io.github.swient.smartbank.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import io.github.swient.smartbank.model.account.User;
import io.github.swient.smartbank.model.account.Account;
import io.github.swient.smartbank.model.card.BankCard;
import io.github.swient.smartbank.service.UserService;

import java.util.Map;

public class AdminController {
    @FXML
    private TreeView<String> userTreeView;
    @FXML
    private Label bankLabel;
    @FXML
    private Label logoutMsg;

    private String bankName;

    public void setLoginUser(String bankName) {
        this.bankName = bankName;
        bankLabel.setText(bankName);
        loadUserTree();
        setupCopyContextMenu();
    }

    private void loadUserTree() {
        TreeItem<String> root = new TreeItem<>("所有使用者");
        Map<String, User> userMap = UserService.getInstance().getBankUserMap(bankName);
        for (User user : userMap.values()) {
            if ("admin".equals(user.getUserName())) continue;
            TreeItem<String> userItem = new TreeItem<>(user.getFullName() + " (" + user.getUserName() + ")");
            for (Account account : user.getAccounts().values()) {
                TreeItem<String> accountItem = new TreeItem<>("帳戶: " + account.getAccountNumber());
                for (BankCard card : user.getBankCards().values()) {
                    if (card.getAccount().getAccountNumber().equals(account.getAccountNumber())) {
                        TreeItem<String> cardItem = new TreeItem<>("卡號: " + card.getCardNumber());
                        accountItem.getChildren().add(cardItem);
                    }
                }
                userItem.getChildren().add(accountItem);
            }
            root.getChildren().add(userItem);
        }
        userTreeView.setRoot(root);
        userTreeView.setShowRoot(true);
    }

    private void setupCopyContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem copyItem = new MenuItem("複製");
        copyItem.setOnAction(e -> {
            TreeItem<String> selected = userTreeView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String text = selected.getValue();
                if (text.startsWith("帳戶: ") || text.startsWith("卡號: ")) {
                    ClipboardContent content = new ClipboardContent();
                    content.putString(text.replace("帳戶: ","").replace("卡號: ",""));
                    Clipboard.getSystemClipboard().setContent(content);
                }
            }
        });
        contextMenu.getItems().add(copyItem);
        userTreeView.setContextMenu(contextMenu);
    }

    @FXML
    private void onLogoutClick() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/io/github/swient/smartbank/view/login.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
            javafx.stage.Stage stage = (javafx.stage.Stage) userTreeView.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            logoutMsg.setText("登出失敗");
        }
    }
}
