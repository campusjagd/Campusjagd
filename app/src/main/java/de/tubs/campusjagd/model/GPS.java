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

    public static GPS stringToGPS(String gpsAsString){
        GPS gps;
        String[] values = gpsAsString.split(";");
        float lat = 0;
        float longi = 0;
        float alt = 0;
        if (values.length == 3){
            for (String s : values){
                String[] value = s.split(":");
                System.out.println(value[0]);
                if (value[0].equals("long")){
                    longi = Float.valueOf(value[1]);
                }else if (value[0].replace(" ", "").equals("lat")){
                    lat = Float.valueOf(value[1]);
                }else{
                    alt = Float.valueOf(value[1]);
                }
            }
            gps = new GPS(lat, longi, alt);
        }else{
            for (String s : values){
                String[] value = s.split(":");
                System.out.println(value[0]);
                if (value[0].equals("long")){
                    longi = Float.valueOf(value[1]);
                }else if (value[0].replace(" ", "").equals("lat")){
                    lat = Float.valueOf(value[1]);
                }
            }
            gps = new GPS(lat, longi);
        }

        return gps;
    }
}
