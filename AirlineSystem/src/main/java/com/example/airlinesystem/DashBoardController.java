package com.example.airlinesystem;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;

public class DashBoardController {

    @FXML private Label moduleTitle;
    @FXML private StackPane centerPane;
    @FXML private ProgressBar progressBar;
    @FXML private ProgressIndicator progressIndicator;

    // Sidebar / Action Buttons
    @FXML private Button btnReservation, btnReservedSeats, btnFlightDetails, btnFare,
            btnFleetInfo, btnCancellation, btnAddReservation, btnSearchPassenger, btnViewReports;

    // Menu items
    @FXML private MenuItem menuLogout, menuAbout, menuInstructions;

    // Database connection
    Connection conn;

    // User info
    private String userRole;       // "admin" or "passenger"
    private int currentUserId;     // logged-in user id

    @FXML
    public void initialize() {
        try {
            conn = DBConnection.connect();
            System.out.println(conn != null ? "Connected to PostgreSQL Successfully!" : "Failed to connect!");
        } catch (Exception ex) {
            System.out.println("Database Error: " + ex.getMessage());
        }

        progressBar.setProgress(0);
        progressIndicator.setProgress(0);

        addFadeAnimationToCenterPane();
        setupMenuActions();
    }

    // ------------------- SETTERS -------------------
    public void setUserRole(String role) {
        this.userRole = role;
        restrictButtonsByRole();
    }

    public void setCurrentUser(int userId) {
        this.currentUserId = userId;
    }

    // ------------------- ROLE-BASED BUTTON RESTRICTION -------------------
    private void restrictButtonsByRole() {
        if (userRole == null) return;
        switch (userRole.toLowerCase()) {
            case "admin" -> enableAllButtons(true);
            case "passenger" -> {
                enableAllButtons(false);
                btnReservation.setDisable(false);
                btnCancellation.setDisable(false);
            }
            default -> enableAllButtons(false);
        }
    }

    private void enableAllButtons(boolean enabled) {
        btnReservation.setDisable(!enabled);
        btnReservedSeats.setDisable(!enabled);
        btnFlightDetails.setDisable(!enabled);
        btnFare.setDisable(!enabled);
        btnFleetInfo.setDisable(!enabled);
        btnCancellation.setDisable(!enabled);
        btnAddReservation.setDisable(!enabled);
        btnSearchPassenger.setDisable(!enabled);
        btnViewReports.setDisable(!enabled);
    }

    // ------------------- MENU HANDLERS -------------------
    private void setupMenuActions() {
        if (menuLogout != null) menuLogout.setOnAction(e -> handleLogout());
        if (menuAbout != null) menuAbout.setOnAction(e -> showAbout());
        if (menuInstructions != null) menuInstructions.setOnAction(e -> showInstructions());
    }

    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/airlinesystem/login.fxml"));
            Stage stage = (Stage) centerPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (Exception e) {
            showError("Failed to logout:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    // About popup content
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Airline System");
        alert.setHeaderText("Airline Reservation System v1.0");
        alert.setContentText("""
                Developed for airline management.
                
                Features include:
                - Flight reservations
                - Ticket cancellation
                - Passenger search
                - Reports & analytics
                """);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }

    // Instructions popup content
    private void showInstructions() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dashboard Instructions");
        alert.setHeaderText("How to use the dashboard:");
        alert.setContentText("""
                1. Select a module from the left navigation.
                2. Use buttons to add/search/view modules.
                3. File → Logout to return to login.
                4. Progress indicators show real-time updates.
                5. Only allowed modules are enabled based on your role.
                """);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }

    // Error popup
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // ------------------- DYNAMIC FXML LOADING IN CENTER -------------------
    private void loadModuleFXML(String fxmlFile, String title) {
        try {
            moduleTitle.setText(title);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent moduleUI = loader.load();

            // Pass currentUserId to controllers that need it
            Object controller = loader.getController();
            if (controller instanceof ReservationController rc) rc.setCurrentUser(currentUserId);
            else if (controller instanceof CancellationController cc) cc.setCurrentUser(currentUserId);

            centerPane.getChildren().clear();
            centerPane.getChildren().add(moduleUI);
            simulateProgress();
        } catch (Exception e) {
            showError("Failed to load " + title + " interface:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------- LOAD FXML IN NEW STAGE (POPUP WINDOW) -------------------
    private void loadStageFXML(String fxmlFile, String title, double width, double height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Pass currentUserId to controllers that need it
            Object controller = loader.getController();
            if (controller instanceof ReservationController rc) rc.setCurrentUser(currentUserId);
            else if (controller instanceof CancellationController cc) cc.setCurrentUser(currentUserId);

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, width, height));
            stage.showAndWait();
        } catch (Exception e) {
            showError("Failed to open " + title + " window:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------- SIDEBAR BUTTON HANDLERS -------------------
    @FXML public void handleReservation() { loadModuleFXML("Reservation.fxml", "Reservation Module"); }
    @FXML public void handleReservedSeats() { loadModuleFXML("ReservedSeats.fxml", "Reserved Seats Module"); }
    @FXML public void handleFlightDetails() { loadModuleFXML("Flight.fxml", "Flight Details Module"); }
    @FXML public void handleFare() { loadModuleFXML("fare.fxml", "Fare Module"); }
    @FXML public void handleFleetInfo() { loadModuleFXML("Fleet.fxml", "Fleet Info Module"); }
    @FXML public void handleCancellation() { loadModuleFXML("cancelation-view.fxml", "Cancellation Module"); }

    @FXML public void handleAddReservation() { loadStageFXML("Reservation.fxml", "Add Reservation", 600, 400); }
    @FXML public void handleSearchPassenger() { loadStageFXML("SearchPassenger.fxml", "Search Passenger", 600, 400); }
    @FXML public void handleViewReports() { loadStageFXML("Reports.fxml", "Reports", 800, 500); }

    // ------------------- PROGRESS SIMULATION -------------------
    private void simulateProgress() {
        progressBar.setProgress(0);
        progressIndicator.setProgress(0);
        new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                double progress = i / 100.0;
                try { Thread.sleep(15); } catch (Exception ignored) {}
                double finalProgress = progress;
                Platform.runLater(() -> {
                    progressBar.setProgress(finalProgress);
                    progressIndicator.setProgress(finalProgress);
                });
            }
        }).start();
    }

    // ------------------- FADE ANIMATION -------------------
    private void addFadeAnimationToCenterPane() {
        FadeTransition ft = new FadeTransition(Duration.seconds(2), centerPane);
        ft.setFromValue(1.0);
        ft.setToValue(0.4);
        ft.setCycleCount(FadeTransition.INDEFINITE);
        ft.setAutoReverse(true);
        ft.play();
    }
}
