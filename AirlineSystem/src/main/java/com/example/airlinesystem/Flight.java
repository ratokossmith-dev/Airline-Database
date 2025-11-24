package com.example.airlinesystem;

import javafx.beans.property.*;

public class Flight {

    private final StringProperty flightCode;
    private final StringProperty flightName;
    private final StringProperty classCode;
    private final IntegerProperty tExeSeatNo;
    private final IntegerProperty tEcoSeatNo;
    private final IntegerProperty fleetId;

    public Flight(String flightCode, String flightName, String classCode,
                  int tExeSeatNo, int tEcoSeatNo, int fleetId) {
        this.flightCode = new SimpleStringProperty(flightCode);
        this.flightName = new SimpleStringProperty(flightName);
        this.classCode = new SimpleStringProperty(classCode);
        this.tExeSeatNo = new SimpleIntegerProperty(tExeSeatNo);
        this.tEcoSeatNo = new SimpleIntegerProperty(tEcoSeatNo);
        this.fleetId = new SimpleIntegerProperty(fleetId);
    }

    // Property getters for TableView
    public StringProperty flightCodeProperty() { return flightCode; }
    public StringProperty flightNameProperty() { return flightName; }
    public StringProperty classCodeProperty() { return classCode; }
    public IntegerProperty tExeSeatNoProperty() { return tExeSeatNo; }
    public IntegerProperty tEcoSeatNoProperty() { return tEcoSeatNo; }
    public IntegerProperty fleetIdProperty() { return fleetId; }

    // Standard getters
    public String getFlightCode() { return flightCode.get(); }
    public String getFlightName() { return flightName.get(); }
    public String getClassCode() { return classCode.get(); }
    public int getTExeSeatNo() { return tExeSeatNo.get(); }
    public int getTEcoSeatNo() { return tEcoSeatNo.get(); }
    public int getFleetId() { return fleetId.get(); }

    // Optional setters
    public void setFlightName(String name) { this.flightName.set(name); }
    public void setClassCode(String code) { this.classCode.set(code); }
    public void setTExeSeatNo(int seats) { this.tExeSeatNo.set(seats); }
    public void setTEcoSeatNo(int seats) { this.tEcoSeatNo.set(seats); }
    public void setFleetId(int id) { this.fleetId.set(id); }
}
