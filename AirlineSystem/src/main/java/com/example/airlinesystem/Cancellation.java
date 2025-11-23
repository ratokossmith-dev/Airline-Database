package com.example.airlinesystem;

import javafx.beans.property.*;

public class Cancellation {

    private final SimpleStringProperty pnr;
    private final SimpleStringProperty seatClass;
    private final SimpleStringProperty travelDate;
    private final SimpleLongProperty daysLeft;
    private final SimpleLongProperty hoursLeft;
    private final SimpleDoubleProperty basicFare;
    private final SimpleDoubleProperty cancelAmount;

    public Cancellation(String pnr, String seatClass, String travelDate,
                              long daysLeft, long hoursLeft, double basicFare, double cancelAmount) {
        this.pnr = new SimpleStringProperty(pnr);
        this.seatClass = new SimpleStringProperty(seatClass);
        this.travelDate = new SimpleStringProperty(travelDate);
        this.daysLeft = new SimpleLongProperty(daysLeft);
        this.hoursLeft = new SimpleLongProperty(hoursLeft);
        this.basicFare = new SimpleDoubleProperty(basicFare);
        this.cancelAmount = new SimpleDoubleProperty(cancelAmount);
    }

    public StringProperty pnrProperty() { return pnr; }
    public StringProperty seatClassProperty() { return seatClass; }
    public StringProperty travelDateProperty() { return travelDate; }
    public LongProperty daysLeftProperty() { return daysLeft; }
    public LongProperty hoursLeftProperty() { return hoursLeft; }
    public DoubleProperty basicFareProperty() { return basicFare; }
    public DoubleProperty cancelAmountProperty() { return cancelAmount; }
}
