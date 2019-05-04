package de.tubs.campusjagd.nfc;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.tubs.campusjagd.Database.DatabaseHelperChallenge;
import de.tubs.campusjagd.Database.DatabaseHelperRoom;
import de.tubs.campusjagd.R;
import de.tubs.campusjagd.model.Challenge;
import de.tubs.campusjagd.model.GPS;
import de.tubs.campusjagd.model.Room;

import static android.nfc.NdefRecord.createMime;


public class SenderActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback{

    private NfcAdapter nfcAdapter;
    private DatabaseHelperChallenge mDatabaseHelperChallenge;
    private static DatabaseHelperRoom mDatabaseHelperRoom;
    private String challengeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_peer_to_peer);

        mDatabaseHelperRoom = new DatabaseHelperRoom(this);
        mDatabaseHelperChallenge = new DatabaseHelperChallenge(this);

        challengeName = getIntent().getStringExtra("ChallengeName");

        //setting up the nfcAdapter to use for the data transfer
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null){
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //the callback automatically calls on the createNdefMessage in order to create an NdefMessage
        // at the moment that another device is within range for NFC with content currently
        //visible to the user
        nfcAdapter.setNdefPushMessageCallback(this, this);
    }

    /**
     * open up a dialogbox, if nfc and android beam are not activated
     */
    private void checkIfNfcActivated() {
        if (!nfcAdapter.isEnabled() || !nfcAdapter.isNdefPushEnabled())
        {
            AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
            alertbox.setTitle("Info");
            alertbox.setMessage("Bitte aktivieren Sie NFC und Android Beam");
            alertbox.setPositiveButton("Aktivieren", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(intent);
                    }
                }
            });
            alertbox.setNegativeButton("Schließen", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertbox.show();
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {

        List<Challenge> challengeList = new ArrayList<>();
        List<Room> roomList = new ArrayList<>();

        //Get all the local data associated with the name of the Challenge
        Cursor data = mDatabaseHelperChallenge.getSpecificChallenge(challengeName);

        //get the value from the database in column 0 to 1 (equal to column-naming-scheme at the
        //top of the databasehelperroom class. Meaning COL0 == COL_NAME, COL1 == COL_ROOMS)
        //then add it to the arraylist
        while (data.moveToNext()) {
            String name = data.getString(0);
            String roomsAsString = data.getString(1);
            String[] roomValues = roomsAsString.split(";");
            for (String roomName : roomValues) {
                Cursor roomData = mDatabaseHelperRoom.getSpecificRoom(roomName);
                //calling the method with a cursor containing every entry for the challenge
                roomList.addAll(instantiateRoomList(roomData));
            }

            Challenge challenge = new Challenge(name, roomList);
            challengeList.add(challenge);
        }

        //add the challenge name to the text, which is to be transfered and adding a "splitter"
        // at the end to split the string on the receiving end
        String text = challengeName + "/";

        for (Room room : roomList){
            //send all the data for every room there is in the challenge, except from the
            //found value, because this could be a different one at the receivers end.
            //if the receiver doesnt know the room, the value will be set to false.
            //Furthermore "splitters" are added to split up the string at the receiving end
            text += room.getName()+"~"+room.getGps().toString()+"~"+room.getPoints()+"~"+room.getTimestamp()+"/";
        }

        //Create a ndefMessage to send via nfc
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMime(
                        "application/de.tubs.campusjagd", text.getBytes())
                        /**
                         * The Android Application Record (AAR) is commented out. When a device
                         * receives a push with an AAR in it, the application specified in the AAR
                         * is guaranteed to run. The AAR overrides the tag dispatch system.
                         * You can add it back in to guarantee that this
                         * activity starts when receiving a beamed message. For now, this code
                         * uses the tag dispatch system.
                        */
                        //,NdefRecord.createApplicationRecord("com.example.android.beam")
                });
        return msg;
    }

    /**
     * Set up a list of rooms to instantiate a challenge object
     * @param data
     * @return
     */
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


    @Override
    public void onResume() {
        super.onResume();
        checkIfNfcActivated();
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}
