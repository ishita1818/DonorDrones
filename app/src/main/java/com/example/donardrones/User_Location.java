package com.example.donardrones;

import java.util.Map;

public class User_Location {
    String address;
    double longitude;
    double latitude;

    public User_Location() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User_Location(Map<String,Object> m){
        if(m.get("address")!=null)
        address = m.get("address").toString();
        if(m.get("longitude")!=null)
        longitude = (double)m.get("longitude");
        if(m.get("latitude")!=null)
        latitude = (double)m.get("latitude");
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public User_Location(String address, Double longitude, double latitude) {

        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
