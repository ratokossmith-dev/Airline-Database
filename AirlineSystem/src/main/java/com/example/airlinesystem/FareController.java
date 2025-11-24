package com.example.airlinesystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class FareController {

    // TextFields
    @FXML
    private TextField txtRouteCode, txtSPlace, txtVia, txtDPlace, txtBaseFare, txtCalculatedFare;

    // ComboBoxes
    @FXML
    private ComboBox<String> cmbFlightCode, cmbDepartureTime, cmbArrivalTime;

    // DatePickers
    @FXML
    private DatePicker dpDepartureDate, dpArrivalDate;

    // All seat classes
    private final ObservableList<String> classCodes = FXCollections.observableArrayList(
            "Economy", "Executive"
    );

    // Times for departure/arrival
    private final ObservableList<String> times = FXCollections.observableArrayList(
            "00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00",
            "10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00",
            "20:00","21:00","22:00","23:00"
    );

    @FXML
    public void initialize() {
        cmbDepartureTime.setItems(times);
        cmbArrivalTime.setItems(times);
        loadFlightCodes();

        // Automatically update calculated fare display when base fare changes
        txtBaseFare.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                txtCalculatedFare.setText(newVal);
            }
        });
    }

    private void loadFlightCodes() {
        ObservableList<String> flightCodes = FXCollections.observableArrayList();
        String sql = "SELECT flight_code FROM airline.flight";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                flightCodes.add(rs.getString("flight_code"));
            }
            cmbFlightCode.setItems(flightCodes);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not load flight codes.");
            e.printStackTrace();
        }
    }

    // Concession multipliers
    private Map<String, BigDecimal> getConcessionMultipliers() {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        map.put("None", BigDecimal.ONE);
        map.put("Student", new BigDecimal("0.5"));
        map.put("Senior citizen", new BigDecimal("0.7"));
        map.put("Cancer patient", new BigDecimal("0.2"));
        return map;
    }

    // Get available seats for a given class
    private int getAvailableSeats(Connection conn, String flightCode, String classCode) throws SQLException {
        String sql = "SELECT t_exe_seatno, t_eco_seatno FROM airline.flight WHERE flight_code = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, flightCode);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int exeSeats = rs.getInt("t_exe_seatno");
                    int ecoSeats = rs.getInt("t_eco_seatno");
                    return switch (classCode) {
                        case "Economy" -> ecoSeats;
                        case "Executive" -> exeSeats;
                        default -> 0;
                    };
                }
            }
        }
        return 0;
    }

    @FXML
    private void handleSaveFare() {
        String flightCode = cmbFlightCode.getValue();
        String routeCode = txtRouteCode.getText();
        String sPlace = txtSPlace.getText();
        String via = txtVia.getText();
        String dPlace = txtDPlace.getText();

        if (flightCode == null || routeCode.isEmpty() || sPlace.isEmpty() || dPlace.isEmpty() ||
                txtBaseFare.getText().isEmpty() || dpDepartureDate.getValue() == null ||
                dpArrivalDate.getValue() == null || cmbDepartureTime.getValue() == null || cmbArrivalTime.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Data", "Please fill in all required fields!");
            return;
        }

        // ⛔ PREVENT PAST DATES
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime dDateTime = LocalDateTime.of(
                dpDepartureDate.getValue(),
                java.time.LocalTime.parse(cmbDepartureTime.getValue())
        );

        LocalDateTime aDateTime = LocalDateTime.of(
                dpArrivalDate.getValue(),
                java.time.LocalTime.parse(cmbArrivalTime.getValue())
        );

        if (dDateTime.isBefore(now)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Date", "Departure date/time cannot be in the past.");
            return;
        }

        // ⛔ ARRIVAL MUST BE AFTER DEPARTURE
        if (aDateTime.isBefore(dDateTime)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Date", "Arrival date/time cannot be earlier than departure.");
            return;
        }

        // Continue with your existing code (unchanged)
        BigDecimal baseFare;
        try {
            baseFare = new BigDecimal(txtBaseFare.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Fare", "Base fare must be a valid number.");
            return;
        }

        Map<String, BigDecimal> concessions = getConcessionMultipliers();

        try (Connection conn = DBConnection.connect()) {
            String sql = "INSERT INTO airline.fare " +
                    "(route_code, s_place, via, d_place, d_date, d_time, a_date, a_time, flight_code, class_code, fare) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            StringBuilder fareSummary = new StringBuilder();

            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                for (String seatClass : classCodes) {
                    int availableSeats = getAvailableSeats(conn, flightCode, seatClass);
                    if (availableSeats <= 0) continue;

                    for (Map.Entry<String, BigDecimal> entry : concessions.entrySet()) {
                        BigDecimal calculatedFare = baseFare.multiply(entry.getValue());

                        pst.setString(1, routeCode);
                        pst.setString(2, sPlace);
                        pst.setString(3, via);
                        pst.setString(4, dPlace);
                        pst.setDate(5, Date.valueOf(dDateTime.toLocalDate()));
                        pst.setTime(6, Time.valueOf(dDateTime.toLocalTime()));
                        pst.setDate(7, Date.valueOf(aDateTime.toLocalDate()));
                        pst.setTime(8, Time.valueOf(aDateTime.toLocalTime()));
                        pst.setString(9, flightCode);
                        pst.setString(10, seatClass);
                        pst.setBigDecimal(11, calculatedFare);

                        pst.addBatch();
                        fareSummary.append(seatClass).append(": ").append(calculatedFare).append("  ");
                    }
                }
                pst.executeBatch();
            }

            txtCalculatedFare.setText(fareSummary.toString());
            showAlert(Alert.AlertType.INFORMATION, "Success", "All fares automatically calculated and saved!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not save fares.");
            e.printStackTrace();
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
