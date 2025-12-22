package io.github.swient.smartbank;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BankApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BankApplication.class.getResource("view/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 420, 350);
        stage.setTitle("SmartBank");
        stage.setScene(scene);
        stage.show();
    }
}
