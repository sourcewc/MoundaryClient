package com.team.moundary.map;


public class POI {
    String id;
    String name;
    String telNo;
    String frontLat;
    String frontLon;
    String noorLat;
    String noorLon;

    String upperAddrName;
    String middleAddrName;
    String lowerAddrName;
    String firstNo;
    String secondNo;

    @Override
    public String toString() {
        return name;
    }

    public String getAddress() {
        return upperAddrName + " " + middleAddrName + " " + lowerAddrName;
    }

    double lat = -1;
    double lng = -1;

    public double getLatitude() {
        if (lat == -1) {
            lat = (Double.parseDouble(frontLat) + Double.parseDouble(noorLat)) / 2;
        }
        return lat;
    }

    public double getLongitude() {
        if (lng == -1) {
            lng = (Double.parseDouble(frontLon) + Double.parseDouble(noorLon)) / 2;

        }
        return lng;
    }
}
