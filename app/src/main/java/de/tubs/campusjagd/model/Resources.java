package de.tubs.campusjagd.model;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import de.tubs.campusjagd.Database.DataBaseHelperUser;
import de.tubs.campusjagd.Database.DatabaseHelperChallenge;
import de.tubs.campusjagd.Database.DatabaseHelperRoom;
import de.tubs.campusjagd.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Resources {

    private static Resources mInstance;
    private static DatabaseHelperRoom mDatabaseHelperRoom;
    private static DatabaseHelperChallenge mDatabaseHelperChallenge;
    private static DataBaseHelperUser mDatabaseHelperUser;
    private OkHttpClient okHttpClient;

    String hostnameServer = "134.169.47.155";
    String challenge_url = "/api/challenge";
    String room_url = "/api/room";
    String user_url = "/api/users";

    public static Resources getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Resources(context);
        }
        return mInstance;
    }

    private Resources(Context context) {

        mDatabaseHelperRoom = new DatabaseHelperRoom(context);
        mDatabaseHelperChallenge = new DatabaseHelperChallenge(context);
        mDatabaseHelperUser = new DataBaseHelperUser(context);

        setupClient(context);

        mDatabaseHelperUser.addUsername("Test");

        Room room1 = new Room(null, new GPS(52.272899, 10.525311), "Raum 161", 2, System.currentTimeMillis(), true);
        Room room3 = new Room(null, new GPS(), "Raum 74", 10, System.currentTimeMillis(), false);
        Room room2 = new Room(null, new GPS(52.274760, 10.526023), "Mensa", 1, System.currentTimeMillis(), true);
        Room room4 = new Room(null, new GPS(), "Raum 111", 2, System.currentTimeMillis(), false);
        Room room5 = new Room(null, new GPS(52.272678, 10.526844), "Pk 2.2", 4, System.currentTimeMillis(), true);
        Room room6 = new Room(null, new GPS(), "Raum 262", 6, System.currentTimeMillis(), false);
        //Room room7 = new Room(null, new GPS(), "Audimax", 6, System.currentTimeMillis(), true);

        mDatabaseHelperRoom.addRoom(room1);
        mDatabaseHelperRoom.addRoom(room2);
        mDatabaseHelperRoom.addRoom(room3);
        mDatabaseHelperRoom.addRoom(room4);
        mDatabaseHelperRoom.addRoom(room5);
        mDatabaseHelperRoom.addRoom(room6);
        //mDatabaseHelperRoom.addRoom(room7);


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

    /**
     * setups the client to use the server.pem or the server.cer as a trustanchor for connecting to
     * the server. Furthermore because the host is unverified the hostname is whitelistet to allow access
     * @param context
     */
    void setupClient(Context context){
        SSLContext sslContext;
        TrustManager[] trustManagers;
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            InputStream certInputStream = context.getAssets().open("server.pem");
            BufferedInputStream bis = new BufferedInputStream(certInputStream);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            while (bis.available() > 0) {
                Certificate cert = certificateFactory.generateCertificate(bis);
                keyStore.setCertificateEntry(hostnameServer, cert);
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
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                if (hostname.equals(hostnameServer)) {
                    return true;
                }
                return false;
            }
        });
        okHttpClient = builder.build();
    }

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
        getTopTenPlayers();
        Cursor data = mDatabaseHelperRoom.getAllRooms();

        //Calling the function with a cursor, that contains multiple entries
        return instantiateRoomList(data);
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

    private List<Room> instantiateRoomList(Cursor data) {
        List<Room> roomList = new ArrayList<>();

        while (data.moveToNext()) {
            //get the value from the database in column 0 to 4 (equal to column-naming-scheme at the
            //top of the databasehelperroom class. Meaning COL0 == COL_NAME, COL1 == COL_ and so on)
            //then add it to the arraylist
            String name = data.getString(0);
            GPS gps = GPS.stringToGPS(data.getString(1));
            int points = data.getInt(2);
            long timestamp = Long.valueOf(data.getString(3));
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

    public void saveChallenge(Challenge challenge) {
        mDatabaseHelperChallenge.addChallenge(challenge);
    }

    public void saveRoom(Room room) {
        mDatabaseHelperRoom.addRoom(room);
    }

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
        Cursor data = mDatabaseHelperRoom.getSpecificRoom(roomName);
        Room room = new Room(null, new GPS(latitude, longitute), roomName, Integer.parseInt(points), Long.parseLong(timestamp), true);
        //if the room is not already known in the database, then it is added, if it is known it will
        // be marked as found
        if (data.getCount() == 0) {
            //romm does not already exist
            mDatabaseHelperRoom.addRoom(room);
        } else {
            //room already exists
            handleRoomFound(room);
        }
    }

    public void handleRoomFound(Room room) {
        mDatabaseHelperRoom.updateRoomFound(room);
    }

    public String getUserName() {
        Cursor data = mDatabaseHelperUser.getUsername();
        //get the value from the database in column 0
        String username = "";
        while (data.moveToNext()) {
            username = data.getString(0);
        }
        return username;
    }

    public boolean isUsernamePossible(String username) {
        //TODO check on the Server, whether the username to be set is already taken or not
        return true;
    }

    public void updateUsername(String username) {
        if (isUsernamePossible(username)) {

            Cursor data = mDatabaseHelperUser.getUsername();
            //get the value from the database in column 0
            String oldUsername = "";
            while (data.moveToNext()) {
                oldUsername = data.getString(0);
            }
            mDatabaseHelperUser.updateUsername(oldUsername, username);
            //TODO update username on the server, if possible
        }
    }

    //TODO methods for posting rooms to the server
    //  posting self made challenges
    //  getting all challenges
    //  getting all specific room

    public List<Player> getTopTenPlayers() {
        List<Player> playerTopTen = new ArrayList<>();
        String url = "http://" + hostnameServer + user_url;
        Request request = new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonResponse = response.body().string();
                    //TODO use response
                }
            }
        });

        return playerTopTen;
    }
}
