package de.tubs.campusjagd.model;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import de.tubs.campusjagd.Database.DatabaseHelperChallenge;
import de.tubs.campusjagd.Database.DatabaseHelperRoom;

public class Resources {

    private static Resources mInstance;
    private static DatabaseHelperRoom mDatabaseHelperRoom;
    private static DatabaseHelperChallenge mDatabaseHelperChallenge;

    public static Resources getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Resources(context);
        }
        return mInstance;
    }

    private Resources(Context context) {

        mDatabaseHelperRoom = new DatabaseHelperRoom(context);
        mDatabaseHelperChallenge = new DatabaseHelperChallenge(context);

        Room room1 = new Room(null, new GPS(52.272899, 10.525311), "Raum 161", 2, System.currentTimeMillis(), true);
        Room room3 = new Room(null, new GPS(), "Raum 74", 10, System.currentTimeMillis(), false);
        Room room2 = new Room(null, new GPS(52.274760, 10.526023), "Mensa", 1, System.currentTimeMillis(), true);
        Room room4 = new Room(null, new GPS(), "Raum 111", 2, System.currentTimeMillis(), false);
        Room room5 = new Room(null, new GPS(52.272678, 10.526844), "Pk 2.2", 4, System.currentTimeMillis(),true);
        Room room6 = new Room(null, new GPS(), "Raum 262", 6, System.currentTimeMillis(), false);

        mDatabaseHelperRoom.addRoom(room1);
        mDatabaseHelperRoom.addRoom(room2);
        mDatabaseHelperRoom.addRoom(room3);
        mDatabaseHelperRoom.addRoom(room4);
        mDatabaseHelperRoom.addRoom(room5);
        mDatabaseHelperRoom.addRoom(room6);

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

        mDatabaseHelperChallenge.addChallenge(challenge1);
        mDatabaseHelperChallenge.addChallenge(challenge2);
        mDatabaseHelperChallenge.addChallenge(challenge3);

    }

    public List<Challenge> getAllChallenges() {
        List<Challenge> challengeList = new ArrayList<>();

        Cursor data = mDatabaseHelperChallenge.getAllChallenges();

        while(data.moveToNext()){
            //get the value from the database in column 0 to 4 (equal to column-naming-scheme at the
            //top of the databasehelperroom class. Meaning COL0 == COL_NAME, COL1 == COL_ and so on)
            //then add it to the arraylist
            String name = data.getString(0);
            String roomsAsString = data.getString(1);
           String[] roomValues = roomsAsString.split(";");
           List<Room> roomList = new ArrayList<>();
           for (String roomName : roomValues){
               Cursor roomData = mDatabaseHelperRoom.getSpecificRoom(roomName);
               //calling the method with a cursor containing only 1 entry
               roomList.addAll(instantiateRoomList(roomData));
           }

            Challenge challenge = new Challenge(name, roomList);
            challengeList.add(challenge);
        }
        return challengeList;
    }

    public List<Room> getAllRooms() {
        Cursor data = mDatabaseHelperRoom.getAllRooms();

        //Calling the function with a cursor, that contains multiple entries
        return instantiateRoomList(data);
    }

    private List<Room> instantiateRoomList(Cursor data){
        List<Room> roomList = new ArrayList<>();

        while(data.moveToNext()){
            //get the value from the database in column 0 to 4 (equal to column-naming-scheme at the
            //top of the databasehelperroom class. Meaning COL0 == COL_NAME, COL1 == COL_ and so on)
            //then add it to the arraylist
            String name = data.getString(0);
            GPS gps = GPS.stringToGPS(data.getString(1));
            int points = data.getInt(2);
            long timestamp = Long.valueOf(data.getString(3));
            boolean roomFound;
            if (data.getString(4).equals("true")){
                roomFound = true;
            }else{
                roomFound = false;
            }

            Room room = new Room(null, gps, name, points, timestamp, roomFound);
            roomList.add(room);
        }
        return roomList;
    }

    public void saveChallenge(Challenge challenge){
        mDatabaseHelperChallenge.addChallenge(challenge);
    }

    public void saveRoom(Room room) {
        mDatabaseHelperRoom.addRoom(room);
    }

    public void handleBarcodeRead(String barcodeValue) { }
}
