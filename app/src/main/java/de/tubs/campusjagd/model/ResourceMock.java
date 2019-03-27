package de.tubs.campusjagd.model;

import java.util.ArrayList;
import java.util.List;

public class ResourceMock {

    private List<Challenge> allChallenges;


    public ResourceMock() {

        Room room1 = new Room(null, new GPS(), "Raum 161", 2, System.currentTimeMillis(), true);
        Room room2 = new Room(null, new GPS(), "Raum 31", 1, System.currentTimeMillis(), false);
        Room room3 = new Room(null, new GPS(), "Raum 74", 10, System.currentTimeMillis(), true);
        Room room4 = new Room(null, new GPS(), "Raum 111", 2, System.currentTimeMillis(), false);
        Room room5 = new Room(null, new GPS(), "Raum 142", 4, System.currentTimeMillis(),true);
        Room room6 = new Room(null, new GPS(), "Raum 262", 6, System.currentTimeMillis(), false);

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
}
