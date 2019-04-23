package de.tubs.campusjagd.model;

public class GPS {

    @Override
    public String toString() {
        return "long: " + longitude + "; lat: " + latitude;
    }

    public float longitude = -1;
    public float latitude = -1;
    public float altitude = -1;

    public GPS() {}

    public GPS(float latitude, float longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public GPS(float latitude, float longitude, float altitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

    public GPS(double latitude, double longitude) {
        this.longitude = (float) longitude;
        this.latitude = (float) latitude;
    }

    public GPS(double latitude, double longitude, double altitude) {
        this.longitude = (float) longitude;
        this.latitude = (float) latitude;
        this.altitude = (float) altitude;
    }
}
