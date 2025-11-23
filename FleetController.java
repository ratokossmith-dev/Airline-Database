package com.example.airlinesystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class FleetController {

    @FXML private TableView<Fleet> tblFleet;
    @FXML private TableColumn<Fleet, Integer> colId;
    @FXML private TableColumn<Fleet, String> colAircraft;
    @FXML private TableColumn<Fleet, Integer> colClub;
    @FXML private TableColumn<Fleet, Integer> colEco;
    @FXML private TableColumn<Fleet, String> colEngine;
    @FXML private TableColumn<Fleet, String> colSpeed;
    @FXML private TableColumn<Fleet, String> colLength;
    @FXML private TableColumn<Fleet, String> colWing;

    @FXML private TextField txtAircraft;
    @FXML private TextField txtClub;
    @FXML private TextField txtEco;
    @FXML private TextField txtEngine;
    @FXML private TextField txtSpeed;
    @FXML private TextField txtLength;
    @FXML private TextField txtWing;

    private Connection conn;
    private ObservableList<Fleet> fleets = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        conn = DBConnection.connect();

        colId.setCellValueFactory(new PropertyValueFactory<>("fleetId"));
        colAircraft.setCellValueFactory(new PropertyValueFactory<>("noAircraft"));
        colClub.setCellValueFactory(new PropertyValueFactory<>("clubPreCapacity"));
        colEco.setCellValueFactory(new PropertyValueFactory<>("ecoCapacity"));
        colEngine.setCellValueFactory(new PropertyValueFactory<>("engineType"));
        colSpeed.setCellValueFactory(new PropertyValueFactory<>("cruiseSpeed"));
        colLength.setCellValueFactory(new PropertyValueFactory<>("airLength"));
        colWing.setCellValueFactory(new PropertyValueFactory<>("wingSpan"));

        tblFleet.setItems(fleets);
        loadFleets();

        // Automatically populate fields when selecting a fleet
        tblFleet.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if(newSel != null){
                txtAircraft.setText(newSel.getNoAircraft());
                txtClub.setText(String.valueOf(newSel.getClubPreCapacity()));
                txtEco.setText(String.valueOf(newSel.getEcoCapacity()));
                txtEngine.setText(newSel.getEngineType());
                txtSpeed.setText(newSel.getCruiseSpeed());
                txtLength.setText(newSel.getAirLength());
                txtWing.setText(newSel.getWingSpan());
            }
        });
    }

    // --------------------------
    // Load Fleets from DB
    // --------------------------
    private void loadFleets() {
        try {
            ObservableList<Fleet> loaded = FXCollections.observableArrayList();
            String query = "SELECT * FROM airline.fleet ORDER BY fleet_id";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                loaded.add(new Fleet(
                        rs.getInt("fleet_id"),
                        rs.getString("no_aircraft"),
                        rs.getInt("club_pre_capacity"),
                        rs.getInt("eco_capacity"),
                        rs.getString("engine_type"),
                        rs.getString("cruise_speed"),
                        rs.getString("air_length"),
                        rs.getString("wing_span")
                ));
            }
            fleets.setAll(loaded);          // Replace old list
            tblFleet.refresh();             // Force TableView to redraw
        } catch(SQLException e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading fleets: " + e.getMessage()).showAndWait();
        }
    }

    // --------------------------
    // Add Fleet
    // --------------------------
    @FXML
    void onAdd() {
        try {
            String insert = "INSERT INTO airline.fleet(no_aircraft, club_pre_capacity, eco_capacity, engine_type, cruise_speed, air_length, wing_span) VALUES(?,?,?,?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(insert);
            pst.setString(1, txtAircraft.getText());
            pst.setInt(2, Integer.parseInt(txtClub.getText()));
            pst.setInt(3, Integer.parseInt(txtEco.getText()));
            pst.setString(4, txtEngine.getText());
            pst.setString(5, txtSpeed.getText());
            pst.setString(6, txtLength.getText());
            pst.setString(7, txtWing.getText());
            pst.executeUpdate();

            loadFleets();  // auto-refresh TableView
            clearFields();
        } catch(SQLException e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error adding fleet: " + e.getMessage()).showAndWait();
        }
    }

    // --------------------------
    // Update Fleet
    // --------------------------
    @FXML
    void onUpdate() {
        Fleet selected = tblFleet.getSelectionModel().getSelectedItem();
        if(selected == null) return;

        try {
            String update = "UPDATE airline.fleet SET no_aircraft=?, club_pre_capacity=?, eco_capacity=?, engine_type=?, cruise_speed=?, air_length=?, wing_span=? WHERE fleet_id=?";
            PreparedStatement pst = conn.prepareStatement(update);
            pst.setString(1, txtAircraft.getText());
            pst.setInt(2, Integer.parseInt(txtClub.getText()));
            pst.setInt(3, Integer.parseInt(txtEco.getText()));
            pst.setString(4, txtEngine.getText());
            pst.setString(5, txtSpeed.getText());
            pst.setString(6, txtLength.getText());
            pst.setString(7, txtWing.getText());
            pst.setInt(8, selected.getFleetId());
            pst.executeUpdate();

            loadFleets();  // auto-refresh TableView
            clearFields();
        } catch(SQLException e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error updating fleet: " + e.getMessage()).showAndWait();
        }
    }

    // --------------------------
    // Delete Fleet
    // --------------------------
    @FXML
    void onDelete() {
        Fleet selected = tblFleet.getSelectionModel().getSelectedItem();
        if(selected == null) return;

        try {
            String delete = "DELETE FROM airline.fleet WHERE fleet_id=?";
            PreparedStatement pst = conn.prepareStatement(delete);
            pst.setInt(1, selected.getFleetId());
            pst.executeUpdate();

            loadFleets();  // auto-refresh TableView
            clearFields();
        } catch(SQLException e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error deleting fleet: " + e.getMessage()).showAndWait();
        }
    }

    // --------------------------
    // Clear Input Fields
    // --------------------------
    private void clearFields() {
        txtAircraft.clear();
        txtClub.clear();
        txtEco.clear();
        txtEngine.clear();
        txtSpeed.clear();
        txtLength.clear();
        txtWing.clear();
    }
}
