package de.tubs.campusjagd.model;

public class Room {

    private QRCode QRCode;
    private GPS gps;
    private String name;
    private int points;
    private long timestamp;
    private boolean roomFound;

    public Room(QRCode QRCode, GPS gps, String name, int points, long timestamp, boolean roomFound) {
        this.QRCode = QRCode;
        this.gps = gps;
        this.name = name;
        this.points = points;
        this.timestamp = timestamp;
        this.roomFound = roomFound;
    }

    public QRCode getQRCode() {
        return QRCode;
    }

    public void setQRCode(QRCode QRCode) {
        this.QRCode = QRCode;
    }

    public GPS getGps() {
        return gps;
    }

    public void setGps(GPS gps) {
        this.gps = gps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRoomFound() {
        return roomFound;
    }

    public void setRoomFound(boolean roomFound) {
        this.roomFound = roomFound;
    }
}
