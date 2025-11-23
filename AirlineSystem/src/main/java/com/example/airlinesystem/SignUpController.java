package com.example.airlinesystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpController {

    @FXML private Button btnSignup;
    @FXML private Button btnClear;
    @FXML private PasswordField passConfirmPassword;
    @FXML private PasswordField passPassword;
    @FXML private TextField textAddress;
    @FXML private DatePicker dateBirthDate;
    @FXML private TextField textEmail;
    @FXML private TextField textFirstName;
    @FXML private TextField textLastName;
    @FXML private TextField textPhoneNumber;
    @FXML private ComboBox<String> cmbRole;

    @FXML
    public void initialize() {
        // Populate role ComboBox
        cmbRole.getItems().addAll("passenger", "admin");
        cmbRole.setValue("passenger"); // default
    }

    @FXML
    void handlesignup(ActionEvent event) {

        // Basic validations
        if (textFirstName.getText().isEmpty() || textLastName.getText().isEmpty() ||
                textEmail.getText().isEmpty() || passPassword.getText().isEmpty() ||
                passConfirmPassword.getText().isEmpty() || cmbRole.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Form Incomplete", "Please fill all required fields!");
            return;
        }

        if (!passPassword.getText().equals(passConfirmPassword.getText())) {
            showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords do not match!");
            return;
        }

        if (dateBirthDate.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Form Incomplete", "Please select your birth date!");
            return;
        }

        // Save user to PostgreSQL
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO userschema.users " +
                    "(first_name, last_name, address, birth_date, email, phone_number, password, role) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, textFirstName.getText());
            stmt.setString(2, textLastName.getText());
            stmt.setString(3, textAddress.getText());
            stmt.setDate(4, Date.valueOf(dateBirthDate.getValue()));
            stmt.setString(5, textEmail.getText());
            stmt.setString(6, textPhoneNumber.getText());
            stmt.setString(7, passPassword.getText()); // TODO: hash passwords
            stmt.setString(8, cmbRole.getValue());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User registered successfully!");
                redirectToLogin();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    void handleClear(ActionEvent event) {
        textFirstName.clear();
        textLastName.clear();
        textEmail.clear();
        textAddress.clear();
        textPhoneNumber.clear();
        passPassword.clear();
        passConfirmPassword.clear();
        dateBirthDate.setValue(null);
        cmbRole.setValue("passenger");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnSignup.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Cannot load login page.");
        }
    }
}
