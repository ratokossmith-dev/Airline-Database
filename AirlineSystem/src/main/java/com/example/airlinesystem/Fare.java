package com.example.airlinesystem;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;

public class Fare {
    private int fareId;
    private String routeCode;
    private String sPlace;
    private String via;
    private String dPlace;
    private LocalDateTime dDateTime;  // combines d_date + d_time
    private LocalDateTime aDateTime;  // combines a_date + a_time
    private String flightCode;
    private String classCode;
    private BigDecimal fare;

    public Fare() {}

    public Fare(int fareId, String routeCode, String sPlace, String via, String dPlace,
                LocalDateTime dDateTime, LocalDateTime aDateTime, String flightCode, String classCode, BigDecimal fare) {
        this.fareId = fareId;
        this.routeCode = routeCode;
        this.sPlace = sPlace;
        this.via = via;
        this.dPlace = dPlace;
        this.dDateTime = dDateTime;
        this.aDateTime = aDateTime;
        this.flightCode = flightCode;
        this.classCode = classCode;
        this.fare = fare;
    }

    // Getters & Setters
    public int getFareId() { return fareId; }
    public void setFareId(int fareId) { this.fareId = fareId; }

    public String getRouteCode() { return routeCode; }
    public void setRouteCode(String routeCode) { this.routeCode = routeCode; }

    public String getSPlace() { return sPlace; }
    public void setSPlace(String sPlace) { this.sPlace = sPlace; }

    public String getVia() { return via; }
    public void setVia(String via) { this.via = via; }

    public String getDPlace() { return dPlace; }
    public void setDPlace(String dPlace) { this.dPlace = dPlace; }

    public LocalDateTime getDDateTime() { return dDateTime; }
    public void setDDateTime(LocalDateTime dDateTime) { this.dDateTime = dDateTime; }

    public LocalDateTime getADateTime() { return aDateTime; }
    public void setADateTime(LocalDateTime aDateTime) { this.aDateTime = aDateTime; }

    public String getFlightCode() { return flightCode; }
    public void setFlightCode(String flightCode) { this.flightCode = flightCode; }

    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }

    public BigDecimal getFare() { return fare; }
    public void setFare(BigDecimal fare) { this.fare = fare; }

    // Helper methods to get SQL-compatible Date and Time
    public Date getDepartureDate() { return Date.valueOf(dDateTime.toLocalDate()); }
    public Time getDepartureTime() { return Time.valueOf(dDateTime.toLocalTime()); }
    public Date getArrivalDate() { return Date.valueOf(aDateTime.toLocalDate()); }
    public Time getArrivalTime() { return Time.valueOf(aDateTime.toLocalTime()); }
}
