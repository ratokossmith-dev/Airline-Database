package com.example.airlinesystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.Random;

public class ReservationController {

    @FXML private TextField txtName, txtFatherName, txtAddress, txtPhone, txtProfession, txtFare;
    @FXML private ComboBox<String> cmbGender, cmbConcession, cmbFlights, cmbClass, cmbSeatPref, cmbSecurity;
    @FXML private DatePicker dpDOB, dpTravelDate;
    @FXML private TableView<Ticket> tblReservations;
    @FXML private TableColumn<Ticket, String> colPNR, colCustName, colFlight, colClass, colSeat, colFare, colStatus;

    private Connection conn;
    private int currentUserId; // FK to users.id
    private final ObservableList<Ticket> ticketList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        conn = DBConnection.connect();

        loadFlights();
        loadSeatClasses();
        loadGenders();
        loadConcessionTypes();
        loadSeatPreferences();
        loadSecurityTypes();

        // Bind TableView columns
        colPNR.setCellValueFactory(data -> data.getValue().pnrProperty());
        colCustName.setCellValueFactory(data -> data.getValue().customerNameProperty());
        colFlight.setCellValueFactory(data -> data.getValue().flightProperty());
        colClass.setCellValueFactory(data -> data.getValue().seatClassProperty());
        colSeat.setCellValueFactory(data -> data.getValue().seatProperty());
        colFare.setCellValueFactory(data -> data.getValue().fareProperty().asString("%.2f"));
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty());

        tblReservations.setItems(ticketList);

        // Update fare dynamically
        cmbFlights.setOnAction(e -> loadFare());
        cmbClass.setOnAction(e -> loadFare());
        cmbConcession.setOnAction(e -> loadFare());
        dpTravelDate.setOnAction(e -> loadFare());
    }

    public void setCurrentUser(int userId) {
        this.currentUserId = userId;
        loadTickets();
    }

    // ===== ComboBox Data =====
    private void loadGenders() { cmbGender.setItems(FXCollections.observableArrayList("Male","Female","Other")); }
    private void loadConcessionTypes() { cmbConcession.setItems(FXCollections.observableArrayList("None","Student","Senior citizen","Cancer patient")); }
    private void loadSeatPreferences() { cmbSeatPref.setItems(FXCollections.observableArrayList("Window","Aisle","Middle")); }
    private void loadSeatClasses() { cmbClass.setItems(FXCollections.observableArrayList("Economy","Executive")); }
    private void loadSecurityTypes() { cmbSecurity.setItems(FXCollections.observableArrayList("National ID","Passport","Driver's License")); }

    private void loadFlights() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT flight_code FROM airline.flight ORDER BY flight_code";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while(rs.next()) list.add(rs.getString("flight_code").trim());
        } catch(Exception e) { e.printStackTrace(); }
        cmbFlights.setItems(list);
    }

    // ===== Load Fare =====
    private void loadFare() {
        if(cmbFlights.getValue() == null || cmbClass.getValue() == null || dpTravelDate.getValue() == null) {
            txtFare.clear();
            txtFare.setUserData(null);
            return;
        }

        String flightCode = cmbFlights.getValue().trim();
        String classCode = cmbClass.getValue().trim();
        LocalDate travelDate = dpTravelDate.getValue();

        String sql = "SELECT fare_id, fare FROM airline.fare " +
                "WHERE flight_code=? AND class_code=? AND d_date=? ORDER BY fare ASC LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, flightCode);
            ps.setString(2, classCode);
            ps.setDate(3, Date.valueOf(travelDate));

            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                double fare = rs.getDouble("fare");
                if(cmbConcession.getValue() != null && !cmbConcession.getValue().equals("None")) {
                    fare = applyConcession(fare, cmbConcession.getValue());
                }
                txtFare.setText(String.format("%.2f", fare));
                txtFare.setUserData(rs.getInt("fare_id"));
            } else {
                txtFare.clear();
                txtFare.setUserData(null);
                new Alert(Alert.AlertType.WARNING, "Selected flight does not operate on the chosen travel date!").show();
            }
        } catch(Exception e) {
            e.printStackTrace();
            txtFare.clear();
            txtFare.setUserData(null);
        }
    }

    private double applyConcession(double fare, String type) {
        return switch (type) {
            case "Student" -> fare * 0.8;
            case "Senior citizen" -> fare * 0.85;
            case "Cancer patient" -> fare * 0.5;
            default -> fare;
        };
    }

    // ===== Submit Reservation =====
    @FXML
    private void handleSubmitReservation() {
        loadFare();
        if (!validateInputs()) return;

        if (txtFare.getUserData() == null) {
            new Alert(Alert.AlertType.ERROR, "This flight does NOT operate on the selected travel date.\nChoose another date.").show();
            return;
        }

        String flight = cmbFlights.getValue().trim();
        String seatClass = cmbClass.getValue().trim();
        int fareId = (int) txtFare.getUserData();

        try {
            boolean seatsAvailable = checkSeatAvailability(flight, seatClass);
            int reservationId = saveReservation(flight, seatClass, fareId);

            String status = seatsAvailable ? "CONFIRMED" : "WAITING";
            if (seatsAvailable) reduceSeats(flight, seatClass);

            String pnr = generatePNR();
            saveTicket(reservationId, pnr, status);

            Ticket newTicket = new Ticket(pnr, txtName.getText(), flight, seatClass, cmbSeatPref.getValue(),
                    Double.parseDouble(txtFare.getText()), status);
            ticketList.add(0, newTicket);

            new Alert(Alert.AlertType.INFORMATION, "Reservation Successful!\nPNR: " + pnr + "\nStatus: " + status).show();

        } catch(Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to reserve ticket.").show();
        }
    }

    private boolean checkSeatAvailability(String flight, String seatClass) throws SQLException {
        String sql = "SELECT t_exe_seatno, t_eco_seatno FROM airline.flight WHERE flight_code=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, flight);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                int exeSeats = rs.getInt("t_exe_seatno");
                int ecoSeats = rs.getInt("t_eco_seatno");
                boolean available = switch (seatClass) {
                    case "Economy" -> ecoSeats > 0;
                    case "Executive" -> exeSeats > 0;
                    default -> false;
                };
                if (!available) new Alert(Alert.AlertType.INFORMATION,
                        "All " + seatClass + " seats on flight " + flight +
                                " are fully booked.\nYour ticket will be placed on the WAITING LIST.").show();
                return available;
            }
        }
        return false;
    }

    private int saveReservation(String flight, String seatClass, int fareId) throws SQLException {
        String sql = """
                INSERT INTO airline.reservation(
                full_name, father_name, gender, dob, address, telephone, profession,
                concession_type, travel_date, flight_code, fare_id,
                seat_class, seat_preference, fare, user_id, security_type)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING reservation_id
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, txtName.getText());
            ps.setString(2, txtFatherName.getText());
            ps.setString(3, cmbGender.getValue());
            ps.setDate(4, Date.valueOf(dpDOB.getValue()));
            ps.setString(5, txtAddress.getText());
            ps.setString(6, txtPhone.getText());
            ps.setString(7, txtProfession.getText());
            ps.setString(8, cmbConcession.getValue());
            ps.setDate(9, Date.valueOf(dpTravelDate.getValue()));
            ps.setString(10, flight);
            ps.setInt(11, fareId);
            ps.setString(12, seatClass);
            ps.setString(13, cmbSeatPref.getValue());
            ps.setDouble(14, Double.parseDouble(txtFare.getText()));
            ps.setInt(15, currentUserId); // FK to users.id
            ps.setString(16, cmbSecurity.getValue());

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }

    private void reduceSeats(String flight, String seatClass) throws SQLException {
        String sql = switch (seatClass) {
            case "Economy" -> "UPDATE airline.flight SET t_eco_seatno = t_eco_seatno - 1 WHERE flight_code=?";
            case "Executive" -> "UPDATE airline.flight SET t_exe_seatno = t_exe_seatno - 1 WHERE flight_code=?";
            default -> null;
        };
        if(sql == null) return;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, flight);
            ps.executeUpdate();
        }
    }

    private void saveTicket(int reservationId, String pnr, String status) throws SQLException {
        String sql = "INSERT INTO airline.ticket(reservation_id, pnr, ticket_status) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            ps.setString(2, pnr);
            ps.setString(3, status);
            ps.executeUpdate();
        }
    }

    private String generatePNR() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<6;i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

    private boolean validateInputs() {
        if(txtName.getText().isEmpty() || cmbGender.getValue() == null || dpDOB.getValue() == null ||
                cmbFlights.getValue() == null || cmbClass.getValue() == null || dpTravelDate.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Please fill in all required fields!").show();
            return false;
        }
        if(txtFare.getUserData() == null) {
            new Alert(Alert.AlertType.WARNING, "Fare not calculated!").show();
            return false;
        }
        if(cmbSecurity.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a Security Type!").show();
            return false;
        }
        if(dpDOB.getValue().isAfter(LocalDate.now().minusYears(1))) {
            new Alert(Alert.AlertType.WARNING, "Date of Birth indicates age < 1 year.").show();
            return false;
        }
        String phone = txtPhone.getText().trim();
        if(!phone.matches("\\+\\d{6,15}")) {
            new Alert(Alert.AlertType.WARNING, "Invalid phone number! Must start with country code and contain only digits.").show();
            return false;
        }
        return true;
    }

    private void loadTickets() {
        ticketList.clear();
        String sql = """
                SELECT t.pnr, r.full_name, r.flight_code, r.seat_class,
                       r.seat_preference, r.fare, t.ticket_status
                FROM airline.ticket t
                JOIN airline.reservation r ON t.reservation_id = r.reservation_id
                WHERE r.user_id = ?
                ORDER BY t.ticket_id DESC
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                ticketList.add(new Ticket(
                        rs.getString("pnr"),
                        rs.getString("full_name"),
                        rs.getString("flight_code"),
                        rs.getString("seat_class"),
                        rs.getString("seat_preference"),
                        rs.getDouble("fare"),
                        rs.getString("ticket_status")
                ));
            }
        } catch(Exception e) { e.printStackTrace(); }
    }
}
