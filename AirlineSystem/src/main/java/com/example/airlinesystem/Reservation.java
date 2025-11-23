package com.example.airlinesystem;

public class Reservation {

    private int reservationId;
    private String fullName;
    private String fatherName;
    private String gender;
    private String dob;
    private String address;
    private String telephone;
    private String profession;
    private String concessionType;

    private String travelDate;
    private String flightCode;
    private int fareId;
    private String seatClass;
    private String seatPreference;
    private double fare;

    public Reservation(int reservationId, String fullName, String flightCode,
                       String seatClass, String seatPreference, double fare) {
        this.reservationId = reservationId;
        this.fullName = fullName;
        this.flightCode = flightCode;
        this.seatClass = seatClass;
        this.seatPreference = seatPreference;
        this.fare = fare;
    }

    // GETTERS
    public int getReservationId() { return reservationId; }
    public String getFullName() { return fullName; }
    public String getFlightCode() { return flightCode; }
    public String getSeatClass() { return seatClass; }
    public String getSeatPreference() { return seatPreference; }
    public double getFare() { return fare; }
}
