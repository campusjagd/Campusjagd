package de.tubs.campusjagd.model;

import java.util.Date;
import java.util.List;

public class Challenge {

    private String name;
    private List<Room> roomList;
    private String timestamp;
    private boolean timedChallenge;

    public Challenge(String name, List<Room> roomList, String timestamp, boolean timedChallenge) {
        this.name = name;
        this.roomList = roomList;
        this.timestamp = timestamp;
        this.timedChallenge = timedChallenge;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getEndTimeStamp() {
        Date date1 = new Date();
        Date date2 = new Date(date1.getTime() + 1000000);

        return new Date(date2.getTime() - date1.getTime()).toString();
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimedChallenge(boolean timedChallenge) {
        this.timedChallenge = timedChallenge;
    }

    public boolean isTimedChallenge() {
        return timedChallenge;
    }
}
