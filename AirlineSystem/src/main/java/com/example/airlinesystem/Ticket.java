package com.example.airlinesystem;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class Ticket {

    private final SimpleStringProperty pnr;
    private final SimpleStringProperty customerName;
    private final SimpleStringProperty flight;
    private final SimpleStringProperty seatClass;
    private final SimpleStringProperty seat;
    private final SimpleDoubleProperty fare;
    private final SimpleStringProperty status;

    // Constructor
    public Ticket(String pnr, String customerName, String flight,
                  String seatClass, String seat, double fare, String status) {
        this.pnr = new SimpleStringProperty(pnr);
        this.customerName = new SimpleStringProperty(customerName);
        this.flight = new SimpleStringProperty(flight);
        this.seatClass = new SimpleStringProperty(seatClass);
        this.seat = new SimpleStringProperty(seat);
        this.fare = new SimpleDoubleProperty(fare);
        this.status = new SimpleStringProperty(status);
    }

    // ===== Getters =====
    public String getPnr() { return pnr.get(); }
    public String getCustomerName() { return customerName.get(); }
    public String getFlight() { return flight.get(); }
    public String getSeatClass() { return seatClass.get(); }
    public String getSeat() { return seat.get(); }
    public double getFare() { return fare.get(); }
    public String getStatus() { return status.get(); }

    // ===== Property getters for TableView binding =====
    public SimpleStringProperty pnrProperty() { return pnr; }
    public SimpleStringProperty customerNameProperty() { return customerName; }
    public SimpleStringProperty flightProperty() { return flight; }
    public SimpleStringProperty seatClassProperty() { return seatClass; }
    public SimpleStringProperty seatProperty() { return seat; }
    public SimpleDoubleProperty fareProperty() { return fare; }
    public SimpleStringProperty statusProperty() { return status; }

    // Optional: setters if needed for updates
    public void setPnr(String pnr) { this.pnr.set(pnr); }
    public void setCustomerName(String name) { this.customerName.set(name); }
    public void setFlight(String flight) { this.flight.set(flight); }
    public void setSeatClass(String seatClass) { this.seatClass.set(seatClass); }
    public void setSeat(String seat) { this.seat.set(seat); }
    public void setFare(double fare) { this.fare.set(fare); }
    public void setStatus(String status) { this.status.set(status); }
}
