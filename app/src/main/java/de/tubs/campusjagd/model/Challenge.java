package de.tubs.campusjagd.model;

import java.util.List;

public class Challenge {

    private String name;
    private List<Room> roomList;

    public Challenge(String name, List<Room> roomList) {
        this.name = name;
        this.roomList = roomList;
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
}
