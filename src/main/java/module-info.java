module com.example.algoproj {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.algoproj to javafx.fxml;
    exports com.example.algoproj;
}