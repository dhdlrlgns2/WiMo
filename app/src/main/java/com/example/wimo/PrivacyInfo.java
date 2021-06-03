package com.example.wimo;

public class PrivacyInfo {

    private String location;
    private String time;
    private String lat;  // @Dev 위도
    private String lon;   // @Dev 경도

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public String getTime() {
        return time;
    }


    /** @Dev
     *  lat, lon의 getter,setter 추가
     */
    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}