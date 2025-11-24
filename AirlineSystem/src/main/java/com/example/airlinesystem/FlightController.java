package com.example.airlinesystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class FlightController {

    @FXML private TableView<Flight> tblFlights;
    @FXML private TableColumn<Flight, String> colCode;
    @FXML private TableColumn<Flight, String> colName;
    @FXML private TableColumn<Flight, String> colClass;
    @FXML private TableColumn<Flight, Integer> colExeSeats;
    @FXML private TableColumn<Flight, Integer> colEcoSeats;
    @FXML private TableColumn<Flight, Integer> colFleet;

    @FXML private TextField txtCode;
    @FXML private TextField txtName;
    @FXML private TextField txtClass;
    @FXML private TextField txtExeSeats;
    @FXML private TextField txtEcoSeats;
    @FXML private ComboBox<String> comboFleet; // changed from TextField

    private Connection conn;
    private ObservableList<Flight> flights = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        conn = DBConnection.connect();

        colCode.setCellValueFactory(new PropertyValueFactory<>("flightCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("flightName"));
        colClass.setCellValueFactory(new PropertyValueFactory<>("classCode"));
        colExeSeats.setCellValueFactory(new PropertyValueFactory<>("tExeSeatNo"));
        colEcoSeats.setCellValueFactory(new PropertyValueFactory<>("tEcoSeatNo"));
        colFleet.setCellValueFactory(new PropertyValueFactory<>("fleetId"));

        tblFlights.setItems(flights);

        loadFlights();
        loadFleets(); // load fleets into ComboBox

        // Optional: when user selects a row, populate text fields and combo
        tblFlights.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtCode.setText(newSelection.getFlightCode());
                txtName.setText(newSelection.getFlightName());
                txtClass.setText(newSelection.getClassCode());
                txtExeSeats.setText(String.valueOf(newSelection.getTExeSeatNo()));
                txtEcoSeats.setText(String.valueOf(newSelection.getTEcoSeatNo()));
                comboFleet.setValue(String.valueOf(newSelection.getFleetId()));
            }
        });
    }

    private void loadFlights() {
        flights.clear();
        try {
            String query = "SELECT * FROM airline.flight ORDER BY flight_code";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                flights.add(new Flight(
                        rs.getString("flight_code"),
                        rs.getString("flight_name"),
                        rs.getString("class_code"),
                        rs.getInt("t_exe_seatno"),
                        rs.getInt("t_eco_seatno"),
                        rs.getInt("fleet_id")
                ));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadFleets() {
        comboFleet.getItems().clear();
        try {
            String query = "SELECT fleet_id, no_aircraft FROM airline.fleet ORDER BY fleet_id";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                int id = rs.getInt("fleet_id");
                String name = rs.getString("no_aircraft");
                comboFleet.getItems().add(id + " - " + name); // show fleet ID + aircraft name
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onAdd() {
        try {
            if(txtCode.getText().isEmpty() || txtName.getText().isEmpty() || txtClass.getText().isEmpty() ||
                    txtExeSeats.getText().isEmpty() || txtEcoSeats.getText().isEmpty() || comboFleet.getValue() == null) {
                new Alert(Alert.AlertType.WARNING, "Please fill in all fields!").showAndWait();
                return;
            }

            // Extract fleet_id from ComboBox
            int fleetId = Integer.parseInt(comboFleet.getValue().split(" - ")[0]);

            String insert = "INSERT INTO airline.flight(flight_code, flight_name, class_code, t_exe_seatno, t_eco_seatno, fleet_id) VALUES(?,?,?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(insert);
            pst.setString(1, txtCode.getText());
            pst.setString(2, txtName.getText());
            pst.setString(3, txtClass.getText());
            pst.setInt(4, Integer.parseInt(txtExeSeats.getText()));
            pst.setInt(5, Integer.parseInt(txtEcoSeats.getText()));
            pst.setInt(6, fleetId); // use valid fleet ID
            pst.executeUpdate();

            loadFlights(); // refresh table
            clearFields();
        } catch(SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error adding flight: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    void onUpdate() {
        Flight selected = tblFlights.getSelectionModel().getSelectedItem();
        if(selected == null) return;

        try {
            int fleetId = Integer.parseInt(comboFleet.getValue().split(" - ")[0]);

            String update = "UPDATE airline.flight SET flight_name=?, class_code=?, t_exe_seatno=?, t_eco_seatno=?, fleet_id=? WHERE flight_code=?";
            PreparedStatement pst = conn.prepareStatement(update);
            pst.setString(1, txtName.getText());
            pst.setString(2, txtClass.getText());
            pst.setInt(3, Integer.parseInt(txtExeSeats.getText()));
            pst.setInt(4, Integer.parseInt(txtEcoSeats.getText()));
            pst.setInt(5, fleetId);
            pst.setString(6, txtCode.getText());
            pst.executeUpdate();

            loadFlights();
            clearFields();
        } catch(SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error updating flight: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    void onDelete() {
        Flight selected = tblFlights.getSelectionModel().getSelectedItem();
        if(selected == null) return;

        try {
            String delete = "DELETE FROM airline.flight WHERE flight_code=?";
            PreparedStatement pst = conn.prepareStatement(delete);
            pst.setString(1, selected.getFlightCode());
            pst.executeUpdate();

            loadFlights();
            clearFields();
        } catch(SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error deleting flight: " + e.getMessage()).showAndWait();
        }
    }

    private void clearFields() {
        txtCode.clear();
        txtName.clear();
        txtClass.clear();
        txtExeSeats.clear();
        txtEcoSeats.clear();
        comboFleet.setValue(null);
    }

    @FXML
    void onNextToReservation() {
        // Implement scene change to Reservation module if needed
    }
}
