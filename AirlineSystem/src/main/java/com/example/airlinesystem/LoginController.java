package com.example.airlinesystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML private Button btnSignup;
    @FXML private Button btnlogin;
    @FXML private PasswordField passPassword;
    @FXML private TextField textUsername;

    // Store role and id after login
    private String userRole;
    private int userId;

    @FXML
    void handleLogin(ActionEvent event) {
        String email = textUsername.getText();
        String password = passPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Login Failed", "Please enter email and password.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM userschema.users WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userRole = rs.getString("role");
                userId = rs.getInt("id"); // <-- get the user id

                showAlert(Alert.AlertType.INFORMATION, "Login Success", "Welcome " + rs.getString("first_name") + "!");

                redirectToDashboard(userRole, userId); // <-- pass id to dashboard
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid email or password.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    void handleSignup(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/airlinesystem/signup.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnSignup.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Signup");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Cannot load signup page.");
        }
    }

    /**
     * Redirects to the dashboard and passes user role and ID.
     */
    private void redirectToDashboard(String role, int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DashBoard.fxml"));
            Parent root = loader.load();

            // Pass role and userId to DashBoardController
            DashBoardController dashboardController = loader.getController();
            dashboardController.setUserRole(role);
            dashboardController.setCurrentUser(id); // <-- set the current user id for reservations

            Stage stage = (Stage) btnlogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Cannot load dashboard page.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
