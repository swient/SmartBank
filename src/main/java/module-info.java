module io.github.swient.smartbank {
    requires javafx.controls;
    requires javafx.fxml;


    opens io.github.swient.smartbank to javafx.fxml;
    exports io.github.swient.smartbank;
}