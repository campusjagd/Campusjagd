package de.tubs.campusjagd.model;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import de.tubs.campusjagd.Database.DataBaseHelperUser;
import de.tubs.campusjagd.Database.DatabaseHelperChallenge;
import de.tubs.campusjagd.Database.DatabaseHelperRoom;
import de.tubs.campusjagd.Database.DatabaseHelperTopTenPlayers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Resources {

    private static Resources mInstance;
    private static DatabaseHelperRoom mDatabaseHelperRoom;
    private static DatabaseHelperChallenge mDatabaseHelperChallenge;
    private static DataBaseHelperUser mDatabaseHelperUser;
    private static DatabaseHelperTopTenPlayers mDatabaseHelperTopTenPlayers;
    private OkHttpClient okHttpClient;

    private final String hostnameServer = "https://134.169.47.155";

    private final String hostnameServerSetup = "134.169.47.155";

    private final String specific_get_challenge_url = "/api/challenge/";
    private final String post_challenge_url = "/api/challenge";

    private final String specific_get_room_url = "/api/room/";
    private final String post_room_url = "/api/room";

    private final String challenge_url = "/api/challenges";

    private final String room_url = "/api/rooms";

    private final String user_url = "/api/users";

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static Resources getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Resources(context);
        }
        return mInstance;
    }

    private Resources(Context context) {

        //needs to bee at the top, before adding any rooms because it is needed whenn adding a room to the server
        setupClient(context);

        mDatabaseHelperRoom = new DatabaseHelperRoom(context);
        mDatabaseHelperChallenge = new DatabaseHelperChallenge(context);
        mDatabaseHelperUser = new DataBaseHelperUser(context);
        mDatabaseHelperTopTenPlayers = new DatabaseHelperTopTenPlayers(context);

        mDatabaseHelperUser.addUser("Test");

        Room room1 = new Room(null, new GPS(52.272899, 10.525311), "Raum 161", 2, Calendar.getInstance().getTime().toString(), true);
        Room room3 = new Room(null, new GPS(), "Raum 74", 10, Calendar.getInstance().getTime().toString(), false);
        Room room2 = new Room(null, new GPS(52.274760, 10.526023), "Mensa", 1, Calendar.getInstance().getTime().toString(), true);
        Room room4 = new Room(null, new GPS(), "Raum 111", 2, Calendar.getInstance().getTime().toString(), false);
        Room room5 = new Room(null, new GPS(52.272678, 10.526844), "Pk 2.2", 4, Calendar.getInstance().getTime().toString(), true);
        Room room6 = new Room(null, new GPS(), "Raum 262", 6, Calendar.getInstance().getTime().toString(), false);
        //Room room7 = new Room(null, new GPS(), "Audimax", 6, System.currentTimeMillis(), true);
        //Room iz105 = new Room(null, new GPS(52.273352, 10.524979), "IZ105", 2, "Thu, 20 Jun 2019 07:33:24 GMT", false);

        addRoom(room1);
        addRoom(room2);
        addRoom(room3);
        addRoom(room4);
        addRoom(room5);
        addRoom(room6);
        //addRoom(room7);

        final String TABLE_NAME = "room_table";
        final String COL_NAME = "name";
        final String COL_GPS = "gps";
        final String COL_POINTS = "points";
        final String COL_TIMESTAMP = "timestamp";
        final String COL_ROOMFOUND = "roomFound";

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

        addChallenge(challenge1);
        addChallenge(challenge2);
        addChallenge(challenge3);
    }

    /**
     * setups the client to use the server.pem or the server.cer as a trustanchor for connecting to
     * the server. Furthermore because the host is unverified the hostname is whitelistet to allow access
     *
     * @param context
     */
    private void setupClient(Context context) {
        SSLContext sslContext;
        TrustManager[] trustManagers;
        try {
            //setting the key from the server certificate as a trustanchor
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            InputStream certInputStream = context.getAssets().open("server.pem");
            BufferedInputStream bis = new BufferedInputStream(certInputStream);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            while (bis.available() > 0) {
                Certificate cert = certificateFactory.generateCertificate(bis);
                keyStore.setCertificateEntry(hostnameServerSetup, cert);
            }
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            trustManagers = trustManagerFactory.getTrustManagers();
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManagers, null);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0]);
        //explicitly allow the hostname of our server
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                if (hostname.equals(hostnameServerSetup)) {
                    return true;
                }
                return false;
            }
        });
        okHttpClient = builder.build();
        okHttpClient.dispatcher().setMaxRequestsPerHost(50);
    }

    //=============================================================================================
    //Challenge Methods

    /**
     * adds challenge locally and on the server
     *
     * @param challenge
     */
    public void addChallenge(Challenge challenge) {
        mDatabaseHelperChallenge.addChallenge(challenge);
        addChallengeServer(challenge);
    }

    /**
     * adds challenge locally
     *
     * @param challenge
     */
    public void saveChallenge(Challenge challenge) {
        addChallenge(challenge);
    }

    /**
     * updates a challenge that is already on the device
     *
     * @param challenge
     */
    public void updateChallenge(Challenge challenge) {
        mDatabaseHelperChallenge.updateChallenge(challenge);
    }

    /**
     * gets all the challenges stored locally on the device
     *
     * @return
     */
    public List<Challenge> getAllChallenges() {
        List<Challenge> challengeList = new ArrayList<>();

        Cursor data = mDatabaseHelperChallenge.getAllChallenges();

        while (data.moveToNext()) {
            //get the value from the database in column 0 to 1 (equal to column-naming-scheme at the
            //top of the databasehelperroom class. Meaning COL0 == COL_NAME, COL1 == COL_ROOMS)
            //then add it to the arraylist
            String name = data.getString(0);
            String roomsAsString = data.getString(1);
            String[] roomValues = roomsAsString.split(";");
            List<Room> roomList = new ArrayList<>();
            for (String roomName : roomValues) {
                Cursor roomData = getSpecificRoom(roomName);
                //calling the method with a cursor containing only 1 entry
                roomList.addAll(instantiateRoomList(roomData));
            }

            Challenge challenge = new Challenge(name, roomList);
            challengeList.add(challenge);
        }
        return challengeList;
    }

    /**
     * gets specific challenge stored locally on the device
     *
     * @param challengeName
     * @return
     */
    public Cursor getSpecificChallenge(String challengeName) {
        return mDatabaseHelperRoom.getSpecificRoom(challengeName);
    }

    /**
     * deletes a challenge
     *
     * @param challenge
     */
    public void deleteChallenge(Challenge challenge) {
        mDatabaseHelperChallenge.deleteChallenge(challenge.getName());
    }

    /**
     * deletes a challenge
     *
     * @param challengeName
     */
    public void deleteChallenge(String challengeName) {
        mDatabaseHelperChallenge.deleteChallenge(challengeName);
    }

    //=============================================================================================
    //Room Methods

    /**
     * adds a room locally and on the server
     *
     * @param room
     */
    public void addRoom(Room room) {
        sanitizeRoomName(room);
        mDatabaseHelperRoom.addRoom(room);
        addRoomServer(room);
    }

    /**
     * adds room locally
     *
     * @param room
     */
    public void saveRoom(Room room) {
        addRoom(room);
    }

    /**
     * makes all letters in the room name uppercase and removes spaces
     *
     * @param room
     */
    private void sanitizeRoomName(Room room) {
        String roomName = room.getName();
        roomName = roomName.replace(" ", "");
        roomName = roomName.toUpperCase();
        room.setName(roomName);
    }

    /**
     * updates a room that is already saved on the device
     *
     * @param room
     */
    public void updateRoom(Room room) {
        mDatabaseHelperRoom.updateRoom(room);
    }

    /**
     * gets all rooms stored locally on the device
     *
     * @return
     */
    public List<Room> getAllRooms() {
        Cursor data = mDatabaseHelperRoom.getAllRooms();

        //Calling the function with a cursor, that contains multiple entries
        return instantiateRoomList(data);
    }

    /**
     * gets specific room stored locally on the device
     *
     * @param roomName
     * @return
     */
    public Cursor getSpecificRoom(String roomName) {
        return mDatabaseHelperRoom.getSpecificRoom(roomName);
    }

    /**
     * deletes a room
     *
     * @param room
     */
    public void deleteRoom(Room room) {
        mDatabaseHelperRoom.deleteRoom(room.getName());
    }

    /**
     * deletes a room
     *
     * @param roomName
     */
    public void deleteRoom(String roomName) {
        mDatabaseHelperRoom.deleteRoom(roomName);
    }

    /**
     * Filters out all rooms which are not already found yet
     *
     * @return List of all rooms with isRoomFound == false
     */
    public List<Room> getAllRoomsNotFoundYet() {
        List<Room> allRooms = getAllRooms();
        List<Room> notFoundYet = new ArrayList<>();

        for (Room room : allRooms) {
            if (!room.isRoomFound()) {
                notFoundYet.add(room);
            }
        }

        //Alternatively
        /*
        Cursor data = mDatabaseHelperRoom.getRoomsNotFound();
        List<Room> notFoundYet = instantiateRoomList(data);
        */

        return notFoundYet;
    }

    /**
     * instantiates a list of rooms to use in instantiating an object of the type challenge
     *
     * @param data
     * @return
     */
    private List<Room> instantiateRoomList(Cursor data) {

        List<Room> roomList = new ArrayList<>();

        while (data.moveToNext()) {
            //get the value from the database in column 0 to 4 (equal to column-naming-scheme at the
            //top of the databasehelperroom class. Meaning COL0 == COL_NAME, COL1 == COL_ and so on)
            //then add it to the arraylist
            String name = data.getString(0);
            GPS gps = GPS.stringToGPS(data.getString(1));
            int points = data.getInt(2);
            String timestamp = data.getString(3);
            boolean roomFound;
            if (data.getString(4).equals("true")) {
                roomFound = true;
            } else {
                roomFound = false;
            }

            Room room = new Room(null, gps, name, points, timestamp, roomFound);
            roomList.add(room);
        }
        return roomList;
    }

    /**
     * when a barcode is read the contents of the barcode are copmared to the rooms the devoie
     * knows of, if there is a match a room is marked as found
     *
     * @param barcodeValue
     */
    public void handleBarcodeRead(String barcodeValue) {
        //barcode looks like:     name;long;lat;points;timestamp;
        String[] values = barcodeValue.split(";");
        String roomName = values[0];
        String long_barcode = values[1];
        double longitute = Double.parseDouble(long_barcode.split(":")[1]);
        String lat_barcode = values[2];
        double latitude = Double.parseDouble(lat_barcode.split(":")[1]);
        String points = values[3];
        String timestamp = values[4];
        Cursor data = getSpecificRoom(roomName);
        Room room = new Room(null, new GPS(latitude, longitute), roomName, Integer.parseInt(points), timestamp, true);
        //if the room is not already known in the database, then it is added, if it is known it will
        // be marked as found
        if (data.getCount() == 0) {
            //room does not already exist
            addRoom(room);
        } else {
            //room already exists
            handleRoomFound(room);
        }
    }

    /**
     * sets the room found to true
     *
     * @param room
     */
    public void handleRoomFound(Room room) {
        //when a room is found the variable found is set to true in the db
        mDatabaseHelperRoom.updateRoomFound(room);
    }

    //=============================================================================================
    //Username Methods

    /**
     * gets username from the local user
     *
     * @return
     */
    public String getUserName() {
        Cursor data = mDatabaseHelperUser.getUser();
        //get the value from the database in column 0
        String username = "";
        while (data.moveToNext()) {
            username = data.getString(0);
        }
        return username;
    }

    /**
     * checks whether the new chosen username is already taken by another player or not
     *
     * @param newUsername
     * @return
     */
    public boolean isUsernamePossible(String newUsername) {
        List<Player> players = getAllPlayersServer();
        //if there is a player registered on the server then the new name is not possible to set, because
        //it is already taken
        for (Player player: players){
            if (player.getName().equals(newUsername)){
                return false;
            }
        }
        return true;
    }

    /**
     * updates the username on the device and on the server
     *
     * @param username
     */
    public void updateUsername(String username) {
        Cursor data = mDatabaseHelperUser.getUser();
        //get the value from the database in column 0. This is the current username
        String oldUsername = "";
        while (data.moveToNext()) {
            oldUsername = data.getString(0);
        }
        //with the current username set the new username in the database
        mDatabaseHelperUser.updateUsername(oldUsername, username);

        //TODO wait for put route to be ready and change the json field names
        //putNewUsernameServer(oldUsername, username, getUserScore());
    }

    /**
     * sets the user score in the local db
     * @param points
     */
    public void setUserScore(int points) {
        mDatabaseHelperUser.setScore(getUserName(), points);
    }

    /**
     * gets the score of the user from the local db
     * @return
     */
    public int getUserScore() {
        Cursor data = mDatabaseHelperUser.getUser();
        //get the value from the database in column 0
        int score = 0;
        while (data.moveToNext()) {
            score = data.getInt(1);
        }
        return score;
    }

    //=============================================================================================
    //TopTen Player Methods

    /**
     * asdd player to the database
     *
     * @param player
     */
    private void addTopTenPlayer(Player player) {
        mDatabaseHelperTopTenPlayers.addPlayer(player.getName(), player.getPoints());
    }

    /**
     * updates a players score
     *
     * @param player
     */
    private void updateTopTenPlayer(Player player) {
        mDatabaseHelperTopTenPlayers.updatePlayer(player.getName(), player.getPoints());
    }

    /**
     * deletes a player
     *
     * @param player
     */
    private void deleteTopTenPlayer(Player player) {
        mDatabaseHelperTopTenPlayers.deletePlayer(player.getName());
    }

    /**
     * gets the top ten players in the database
     *
     * @return
     */
    public List<Player> getTopTenPlayersLocally() {

        List<Player> players = new ArrayList<>();

        Cursor data = mDatabaseHelperTopTenPlayers.getTopTenPlayers();

        while (data.moveToNext()) {
            String name = data.getString(0);
            int score = data.getInt(1);
            Player player = new Player(name, score);
            players.add(player);
        }

        //sort the list of players in descending order
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                if (p1.getPoints() < p2.getPoints())
                    return 1;
                if (p1.getPoints() > p2.getPoints())
                    return -1;
                return 0;
            }
        });

        List<Player> topTenPlayers = new ArrayList<>();

        //add the top 10 players by points to the list
        for (int i = 0; i < players.size(); i++) {
            if (i < 10) {
                topTenPlayers.add(players.get(i));
            } else {
                break;
            }
        }

        return topTenPlayers;
    }

    //=============================================================================================
    //Server Methods

    /**
     * gets all rooms from the server and saves them to the device
     *
     * @param roomName
     */
    public void getAndSaveAllRoomsServer(String roomName) {
        //building the url to call
        String url = hostnameServer + room_url;
        Request request = new Request.Builder()
                .url(url)
                .build();

        //making the request as a callback to not invoke a NetworkOnMainThreadException
        //the rooms will be saved to the database to be accessed from there
        //this is due to the asynchronous nature of the request
        //if one wants to get the new rooms the "getAllRooms" Method has to be called
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    try {
                        //parsing the json, that is contained in the response
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        JSONArray rooms = jsonObject.getJSONArray("rooms");
                        //the method "roomListFromJson" is responsible for adding and updating the new rooms
                        roomListFromJson(rooms);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * gets a specific room from the server and saves it to the device
     *
     * @param roomName
     */
    public void getAndSaveSpecificRoomServer(String roomName) {
        //building the url to call
        String url = hostnameServer + specific_get_room_url + roomName;
        Request request = new Request.Builder()
                .url(url)
                .build();

        //making the request as a callback to not invoke a NetworkOnMainThreadException
        //the rooms will be saved to the database to be accessed from there
        //this is due to the asynchronous nature of the request
        //if one wants to get the new rooms the "getAllRooms" Method has to be called
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    try {
                        //parsing the json, that is contained in the response
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        String[] gpsPostion = jsonObject.getString("gpsposition").split(",");
                        double lat = Double.parseDouble(gpsPostion[0]);
                        double longi = Double.parseDouble(gpsPostion[1]);
                        String name = jsonObject.getString("name");
                        String points = jsonObject.getString("points");
                        String timestamp = jsonObject.getString("timestamp");
                        Room room = new Room(null, new GPS(lat, longi), name, Integer.parseInt(points), timestamp, false);
                        //adding the room to the database
                        //if there is already a room by that name in the DB  this will fail without a serious error
                        addRoom(room);
                        //if the room already exists the update function is called to set the correct values
                        //for that room (the values from the server are assumed to be always correct)
                        updateRoom(room);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * gets all challenges from the server and saves them to the device
     */
    public void getAndSaveAllChallengesServer() {
        //building the url to call
        String url = hostnameServer + challenge_url;
        Request request = new Request.Builder()
                .url(url)
                .build();

        //making the request as a callback to not invoke a NetworkOnMainThreadException
        //the challenges will be saved to the database to be accessed from there
        //this is due to the asynchronous nature of the request
        //if one wants to get the new challenges the "getAllChallenges" Method has to be called
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    try {
                        //parsing the json, that is contained in the response
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        JSONArray challenges = jsonObject.getJSONArray("get_challenges");
                        for (int i = 0; i < challenges.length(); i++) {
                            JSONObject mJsonObjectProperty = challenges.getJSONObject(i);
                            String challengeName = mJsonObjectProperty.getString("name");
                            JSONArray rooms = mJsonObjectProperty.getJSONArray("rooms");
                            List<Room> roomListServer = roomListFromJson(rooms);
                            Challenge challenge = new Challenge(challengeName, roomListServer);
                            //adding the challenge to the database
                            //if there is already a challenge by that name in the DB this will fail without a serious error
                            addChallenge(challenge);
                            //if the challenge already exists the update function is called to set the correct values
                            //for that challenge (the values from the server are assumed to be always correct)
                            updateChallenge(challenge);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * gets a specific challenges from the server and saves it to the device
     *
     * @param challengeName
     */
    public void getAndSaveSpecificChallengeServer(final String challengeName) {
        //building the url to call
        String url = hostnameServer + specific_get_challenge_url + challengeName;
        Request request = new Request.Builder()
                .url(url)
                .build();

        //making the request as a callback to not invoke a NetworkOnMainThreadException
        //the challenges will be saved to the database to be accessed from there
        //this is due to the asynchronous nature of the request
        //if one wants to get the new challenges the "getAllChallenges" Method has to be called
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    try {
                        //parsing the json, that is contained in the response
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        String name = jsonObject.getString("name");
                        JSONArray rooms = jsonObject.getJSONArray("rooms");
                        List<Room> roomListServer = roomListFromJson(rooms);
                        Challenge challenge = new Challenge(name, roomListServer);
                        //adding the challenge to the database
                        //if there is already a challenge by that name in the DB this will fail without a serious error
                        addChallenge(challenge);
                        //if the challenge already exists the update function is called to set the correct values
                        //for that challenge (the values from the server are assumed to be always correct)
                        updateChallenge(challenge);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * iterates over a json array and instantiates all the rooms
     * returns a list of rooms to use in instantiating a challenge object
     *
     * @param rooms
     * @return
     * @throws JSONException
     */
    private List<Room> roomListFromJson(JSONArray rooms) throws JSONException {
        List<Room> roomListServer = new ArrayList<>();
        for (int i = 0; i < rooms.length(); i++) {
            //parsing the rooms contained in the json array
            JSONObject mJsonObjectProperty = rooms.getJSONObject(i);

            String[] gpsPostion = mJsonObjectProperty.getString("gpsposition").split(",");
            double lat = Double.parseDouble(gpsPostion[0]);
            double longi = Double.parseDouble(gpsPostion[1]);

            String roomName = mJsonObjectProperty.getString("name");

            String points = mJsonObjectProperty.getString("points");

            String timestamp = mJsonObjectProperty.getString("timestamp");

            boolean found = false;
            //if room is not already known add it with not-found-flag
            Cursor roomData = getSpecificRoom(roomName);
            //the cursor will always contain a name column, but if the room is already known the cursor
            //should contain 5 columns
            if (roomData.getCount() > 1) {
                while (roomData.moveToNext()) {
                    //the receiver could already know of a room, which is contained in the response from
                    //the server, if that's the case the correspondy foundFlag will be set
                    if (roomData.getString(4).equals("true")) {
                        found = true;
                    } else {
                        found = false;
                    }
                }
            } else {
                //if the receiver doesn't know of the room the found variable will be set to false
                found = false;

            }
            Room room = new Room(null, new GPS(lat, longi), roomName, Integer.parseInt(points), timestamp, found);
            //adding the room to the database
            //if there is already a room by that name in the DB  this will fail without a serious error
            addRoom(room);
            //if the room already exists the update function is called to set the correct values
            //for that room (the values from the server are assumed to be always correct)
            updateRoom(room);
            roomListServer.add(room);
        }
        return roomListServer;
    }

    /**
     * adds a challenge to the server, if the challenge already exists there is a response
     * with "Bad Request", that wont crash the app
     *
     * @param challenge
     */
    public void addChallengeServer(Challenge challenge) {

        //building the url to call
        String url = hostnameServer + post_challenge_url;

        JSONObject json = new JSONObject();
        try {
            json.put("name", challenge.getName());
            String rooms = "";
            List<Room> roomsList = challenge.getRoomList();
            for (int i = 0; i < roomsList.size(); i++) {
                rooms += roomsList.get(i).getName();
                if (i < roomsList.size() - 1) {
                    rooms += ",";
                }
            }
            json.put("rooms", rooms);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    Log.d("POSTCHALLENGE", "Success " + responseString);
                } else {
                    String responseString = response.body().string();
                    Log.d("POSTCHALLENGE", "Fail " + responseString);
                }
            }
        });
    }

    /**
     * adds a room to the server, if the room already exists there is a response
     * with "Bad Request", that wont crash the app
     *
     * @param room
     */
    public void addRoomServer(Room room) {
        //building the url to call
        String url = hostnameServer + post_room_url;

        //building the json to set as requestbody
        JSONObject json = new JSONObject();
        try {
            json.put("name", room.getName());
            GPS gps = room.getGps();
            String gpsString = gps.latitude + ", " + gps.longitude;
            json.put("gpsposition", gpsString);
            json.put("points", room.getPoints());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    Log.d("POSTROOM", "Success " + responseString);
                } else {
                    String responseString = response.body().string();
                    Log.d("POSTROOM", "Fail " + responseString);
                }
            }
        });
    }

    /**
     * posting the own score to the server
     * @param username
     * @param score
     */
    public void postUser(String username, int score){
        //building the url to call
        String url = hostnameServer + user_url;

        //building the json to set as requestbody
        JSONObject json = new JSONObject();
        try {
            json.put("name",username);
            json.put("points", score);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    Log.d("POSTUSER", "Success " + responseString);
                } else {
                    String responseString = response.body().string();
                    Log.d("POSTUSER", "Fail " + responseString);
                }
            }
        });
    }

    /**
     * updating the username on the server to set to the new one
     * @param oldUsername
     * @param newUsername
     */
    private void putNewUsernameServer(String oldUsername, String newUsername, int score) {
        //building the url to call
        String url = hostnameServer + user_url;

        //building the json to set as requestbody
        JSONObject json = new JSONObject();
        try {
            json.put("oldUsername",oldUsername);
            json.put("newUsername", newUsername);
            json.put("points", score);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    Log.d("PUTUSER", "Success " + responseString);
                } else {
                    String responseString = response.body().string();
                    Log.d("PUTUSER", "Fail " + responseString);
                }
            }
        });
    }

    /**
     * gets all the players listed on the server
     *
     * @return
     */
    private List<Player> getAllPlayersServer() {
        final List<Player> playerList = new ArrayList<>();

        //building the url to call for the get
        String url = hostnameServer + user_url;
        Request request = new Request.Builder()
                .url(url)
                .build();

        //making the request as a callback to not invoke a NetworkOnMainThreadException
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    try {
                        //parsing the json, that is contained in the response
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        JSONArray users = jsonObject.getJSONArray("users");
                        for (int i = 0; i < users.length(); i++) {
                            JSONObject mJsonObjectProperty = users.getJSONObject(i);

                            String name = mJsonObjectProperty.getString("name");
                            String points = mJsonObjectProperty.getString("points");

                            Player player = new Player(name, Integer.parseInt(points));
                            playerList.add(player);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return playerList;

    }

    /**
     * returns the top ten players sorted in descending order
     *
     * @return
     */
    public List<Player> getTopTenPlayersServer() {

        //get all the players from the server
        List<Player> players = getAllPlayersServer();

        //sort the list of players in descending order
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                if (p1.getPoints() < p2.getPoints())
                    return 1;
                if (p1.getPoints() > p2.getPoints())
                    return -1;
                return 0;
            }
        });

        List<Player> playersTopTen = new ArrayList<>();

        //add the top 10 players by points to the list
        for (int i = 0; i < players.size(); i++) {
            if (i < 10) {
                playersTopTen.add(players.get(i));
                //adding the player to the database
                //if there is already a player by that name in the DB this will fail without a serious error
                addTopTenPlayer(players.get(i));
                //if the player already exists the update function is called to set the correct values
                //for that player (the values from the server are assumed to be always correct)
                updateTopTenPlayer(players.get(i));
            } else {
                break;
            }
        }

        return playersTopTen;
    }
}
