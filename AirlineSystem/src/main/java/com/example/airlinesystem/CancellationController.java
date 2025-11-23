package com.example.airlinesystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CancellationController {

    @FXML private TableView<Cancellation> tblTickets;
    @FXML private TableColumn<Cancellation, String> colPNR;
    @FXML private TableColumn<Cancellation, String> colClass;
    @FXML private TableColumn<Cancellation, String> colTravelDate;
    @FXML private TableColumn<Cancellation, Long> colDaysLeft;
    @FXML private TableColumn<Cancellation, Long> colHoursLeft;
    @FXML private TableColumn<Cancellation, Double> colBasicFare;
    @FXML private TableColumn<Cancellation, Double> colCancelAmount;

    @FXML private Button btnCancelTicket;
    @FXML private Button btnSearch;
    @FXML private Button btnShowAll;
    @FXML private TextField txtSearchPNR;

    private Connection conn;
    private int currentUserId;

    @FXML
    public void initialize() {
        conn = DBConnection.connect();

        colPNR.setCellValueFactory(data -> data.getValue().pnrProperty());
        colClass.setCellValueFactory(data -> data.getValue().seatClassProperty());
        colTravelDate.setCellValueFactory(data -> data.getValue().travelDateProperty());
        colDaysLeft.setCellValueFactory(data -> data.getValue().daysLeftProperty().asObject());
        colHoursLeft.setCellValueFactory(data -> data.getValue().hoursLeftProperty().asObject());
        colBasicFare.setCellValueFactory(data -> data.getValue().basicFareProperty().asObject());
        colCancelAmount.setCellValueFactory(data -> data.getValue().cancelAmountProperty().asObject());

        btnCancelTicket.setOnAction(e -> handleCancelTicket());
        btnSearch.setOnAction(e -> searchTicket());
        btnShowAll.setOnAction(e -> loadCustomerTickets());
    }

    public void setCurrentUser(int userId) {
        this.currentUserId = userId;
        loadCustomerTickets();
    }

    private void loadCustomerTickets() {
        if (currentUserId <= 0) return;

        ObservableList<Cancellation> list = FXCollections.observableArrayList();

        String sql = """
            SELECT t.ticket_id, t.pnr, t.ticket_status,
                   r.seat_class, r.travel_date, r.fare, r.flight_code
            FROM airline.ticket t
            JOIN airline.reservation r ON t.reservation_id = r.reservation_id
            JOIN airline.flight f ON r.flight_code = f.flight_code
            WHERE r.user_id = ?  -- using user_id
            ORDER BY t.ticket_id DESC
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if ("CANCELLED".equalsIgnoreCase(rs.getString("ticket_status")))
                    continue;

                String pnr = rs.getString("pnr");
                String seatClass = rs.getString("seat_class");
                LocalDate travelDate = rs.getDate("travel_date").toLocalDate();
                double fare = rs.getDouble("fare");

                LocalDateTime now = LocalDateTime.now();
                LocalDateTime travelDateTime = travelDate.atStartOfDay();

                long daysLeft = Duration.between(now, travelDateTime).toDays();
                long hoursLeft = Duration.between(now, travelDateTime).toHours();
                double cancelAmount = fare * 0.05;

                list.add(new Cancellation(pnr, seatClass, travelDate.toString(),
                        daysLeft, hoursLeft, fare, cancelAmount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tblTickets.setItems(list);
    }

    private void handleCancelTicket() {
        Cancellation selected = tblTickets.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a ticket to cancel.").show();
            return;
        }

        try {
            String pnr = selected.pnrProperty().get();

            String updateTicket = "UPDATE airline.ticket SET ticket_status = 'CANCELLED' WHERE pnr = ? " +
                    "AND reservation_id IN (SELECT reservation_id FROM airline.reservation WHERE user_id = ?)";
            try (PreparedStatement ps = conn.prepareStatement(updateTicket)) {
                ps.setString(1, pnr);
                ps.setInt(2, currentUserId);
                ps.executeUpdate();
            }

            String flightCode = getFlightCodeByPNR(pnr);

            if (flightCode != null) {
                String flightClass = selected.seatClassProperty().get();
                String updateFlight = switch (flightClass) {
                    case "Economy" -> "UPDATE airline.flight SET t_eco_seatno = t_eco_seatno + 1 WHERE flight_code = ?";
                    case "Executive" -> "UPDATE airline.flight SET t_exe_seatno = t_exe_seatno + 1 WHERE flight_code = ?";
                    default -> null;
                };

                if (updateFlight != null) {
                    try (PreparedStatement ps = conn.prepareStatement(updateFlight)) {
                        ps.setString(1, flightCode);
                        ps.executeUpdate();
                    }
                }
            }

            new Alert(Alert.AlertType.INFORMATION,
                    "Ticket " + pnr + " cancelled successfully.\n" +
                            "Cancellation fee: " + selected.cancelAmountProperty().get()).show();

            loadCustomerTickets();

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to cancel ticket.").show();
        }
    }

    private void searchTicket() {
        String pnrSearch = txtSearchPNR.getText().trim();
        if (pnrSearch.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Enter a PNR to search.").show();
            return;
        }

        if (currentUserId <= 0) return;

        ObservableList<Cancellation> list = FXCollections.observableArrayList();

        String sql = """
            SELECT t.ticket_id, t.pnr, t.ticket_status,
                   r.seat_class, r.travel_date, r.fare, r.flight_code
            FROM airline.ticket t
            JOIN airline.reservation r ON t.reservation_id = r.reservation_id
            JOIN airline.flight f ON r.flight_code = f.flight_code
            WHERE t.pnr = ? AND r.user_id = ?  -- using user_id
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pnrSearch);
            ps.setInt(2, currentUserId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if ("CANCELLED".equalsIgnoreCase(rs.getString("ticket_status")))
                    continue;

                String pnr = rs.getString("pnr");
                String seatClass = rs.getString("seat_class");
                LocalDate travelDate = rs.getDate("travel_date").toLocalDate();
                double fare = rs.getDouble("fare");

                LocalDateTime now = LocalDateTime.now();
                long daysLeft = Duration.between(now, travelDate.atStartOfDay()).toDays();
                long hoursLeft = Duration.between(now, travelDate.atStartOfDay()).toHours();
                double cancelAmount = fare * 0.05;

                list.add(new Cancellation(pnr, seatClass, travelDate.toString(),
                        daysLeft, hoursLeft, fare, cancelAmount));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        tblTickets.setItems(list);
    }

    private String getFlightCodeByPNR(String pnr) throws SQLException {
        String sql =
                "SELECT r.flight_code FROM airline.reservation r " +
                        "JOIN airline.ticket t ON r.reservation_id = t.reservation_id " +
                        "WHERE t.pnr = ? AND r.user_id = ?";  // using user_id

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pnr);
            ps.setInt(2, currentUserId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("flight_code");
        }
        return null;
    }
}
