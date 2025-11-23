module com.example.airlinesystem {
    requires javafx.controls;
    requires javafx.fxml;

    //requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.airlinesystem to javafx.fxml;
    exports com.example.airlinesystem;
}