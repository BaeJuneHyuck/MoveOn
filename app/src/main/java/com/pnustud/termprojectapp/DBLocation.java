package com.pnustud.termprojectapp;

public class DBLocation {
    private  String name;
    private  Double latitude;
    private  Double longitude;
    private  int toilet;
    private int type;
    private int report;

    public DBLocation() {}

    public DBLocation(DBLocation dl) {
        this.name = dl.name;
        this.latitude = dl.latitude;
        this.longitude = dl.longitude;
        this.toilet = dl.toilet;
        this.type = dl.type;
        this.report = dl.report;
    }

    public DBLocation(String name, Double latitude, Double longitude, int toilet, int type, int report) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.toilet = toilet;
        this.type = type;
        this.report = report;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getToilet() {
        return toilet;
    }

    public void setToilet(int toilet) {
        this.toilet = toilet;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getReport() {
        return report;
    }

    public void setReport(int report) {
        this.report = report;
    }
}
