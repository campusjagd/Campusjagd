package de.tubs.campusjagd.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Resources {

    private static Resources mInstance;

    public static Resources getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Resources(context);
        }
        return mInstance;
    }

    private List<Challenge> allChallenges;
    private List<Room> allRooms;

    private Resources(Context context) {

        Room room1 = new Room(null, new GPS(52.272899, 10.525311), "Raum 161", 2, System.currentTimeMillis(), true);
        Room room3 = new Room(null, new GPS(), "Raum 74", 10, System.currentTimeMillis(), false);
        Room room2 = new Room(null, new GPS(52.274760, 10.526023), "Mensa", 1, System.currentTimeMillis(), true);
        Room room4 = new Room(null, new GPS(), "Raum 111", 2, System.currentTimeMillis(), false);
        Room room5 = new Room(null, new GPS(52.272678, 10.526844), "Pk 2.2", 4, System.currentTimeMillis(),true);
        Room room6 = new Room(null, new GPS(), "Raum 262", 6, System.currentTimeMillis(), false);

        allRooms = new ArrayList<>();
        allRooms.add(room1);
        allRooms.add(room2);
        allRooms.add(room3);
        allRooms.add(room4);
        allRooms.add(room5);
        allRooms.add(room6);

        ArrayList<Room> roomlist1 = new ArrayList<>();
        roomlist1.add(room1);
        roomlist1.add(room2);
        roomlist1.add(room3);
        roomlist1.add(room4);

        ArrayList<Room> roomlist2 = new ArrayList<>();
        roomlist2.add(room5);
        roomlist2.add(room6);
        roomlist2.add(room1);

        ArrayList<Room> roomlist3 = new ArrayList<>();
        roomlist3.add(room5);
        roomlist3.add(room6);
        roomlist3.add(room1);


        Challenge challenge1 = new Challenge("MockChallenge 1", roomlist1);
        Challenge challenge2 = new Challenge("MockChallenge 2", roomlist2);
        Challenge challenge3 = new Challenge("MockChallenge 3", roomlist3);

        allChallenges = new ArrayList<>();
        allChallenges.add(challenge1);
        allChallenges.add(challenge2);
        allChallenges.add(challenge3);
    }

    public List<Challenge> getAllChallenges() {
        return allChallenges;
    }

    public List<Room> getAllRooms() {
        return allRooms;
    }

    public void saveChallenge(Challenge challenge){
        allChallenges.add(challenge);
    }

    public void saveRoom(Room room) {
        allRooms.add(room);
    }

    public void handleBarcodeRead(String barcodeValue) { }
}
