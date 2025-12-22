module io.github.swient.smartbank {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens io.github.swient.smartbank.controller to javafx.fxml;
    opens io.github.swient.smartbank to javafx.graphics;
    exports io.github.swient.smartbank;
}
