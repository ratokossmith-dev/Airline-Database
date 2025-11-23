package com.example.airlinesystem;

public class Fleet {
    private final Integer fleetId;
    private final String noAircraft;
    private final Integer clubPreCapacity;
    private final Integer ecoCapacity;
    private final String engineType;
    private final String cruiseSpeed;
    private final String airLength;
    private final String wingSpan;

    public Fleet(Integer fleetId, String noAircraft, Integer clubPreCapacity, Integer ecoCapacity,
                 String engineType, String cruiseSpeed, String airLength, String wingSpan) {
        this.fleetId = fleetId;
        this.noAircraft = noAircraft;
        this.clubPreCapacity = clubPreCapacity;
        this.ecoCapacity = ecoCapacity;
        this.engineType = engineType;
        this.cruiseSpeed = cruiseSpeed;
        this.airLength = airLength;
        this.wingSpan = wingSpan;
    }

    public Integer getFleetId() { return fleetId; }
    public String getNoAircraft() { return noAircraft; }
    public Integer getClubPreCapacity() { return clubPreCapacity; }
    public Integer getEcoCapacity() { return ecoCapacity; }
    public String getEngineType() { return engineType; }
    public String getCruiseSpeed() { return cruiseSpeed; }
    public String getAirLength() { return airLength; }
    public String getWingSpan() { return wingSpan; }
}
