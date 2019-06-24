package de.tubs.campusjagd.nfc;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.tubs.campusjagd.R;
import de.tubs.campusjagd.model.Challenge;
import de.tubs.campusjagd.model.GPS;
import de.tubs.campusjagd.model.Resources;
import de.tubs.campusjagd.model.Room;


public class ReceiverActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {

    private NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setting up the nfcAdapter to use for the data transfer
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //Create a PendingIntent object so the Android system can populate it with the
        //details of the tag when it is scanned.
        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        getSupportActionBar().setTitle(R.string.receive_challenge);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.fragment_challenge_transfer);

        //checking, if nfc is available or not 
        if (nfcAdapter == null) {
            Toast.makeText(this, R.string.nfc_not_available, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    /**
     * open up a dialogbox, if nfc and android beam are not activated
     */
    private void checkIfNfcActivated() {
        if (!nfcAdapter.isEnabled() || !nfcAdapter.isNdefPushEnabled()) {
            AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
            alertbox.setTitle(R.string.alertbox_title);
            alertbox.setMessage(R.string.alertbox_message);
            alertbox.setPositiveButton(R.string.alertbox_positive, new DialogInterface.OnClickListener() {
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
            alertbox.setNegativeButton(R.string.alertbox_negative, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertbox.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //The foreground dispatch system allows an activity to intercept an intent and claim
        // priority over other activities that handle the same intent
        //possible to set filters and techlists, might not be necessary
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);

        checkIfNfcActivated();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present

        String text = new String(msg.getRecords()[0].getPayload());

        setContentView(R.layout.challenge_received);
        TextView textView = (TextView) findViewById(R.id.tvChallengeReceived);

        //Split the received message at the "splitter"
        //the first entry of the message is the name of the challenge
        String[] textValues = text.split("/");
        String challengeName = textValues[0];

        textView.setText(challengeName);

        //set up a new list of rooms to instantiate a challenge object later
        List<Room> roomList = new ArrayList<>();
        for (int i = 1; i < textValues.length; i++) {
            //split the message at the "splitter" of the room values
            String[] roomValues = textValues[i].split("~");
            String roomName = roomValues[0];
            GPS gps = GPS.stringToGPS(roomValues[1]);
            int points = Integer.parseInt(roomValues[2]);
            String timestamp = roomValues[3];
            boolean found = false;

            //if room is not already known add it with not-found-flag
            Cursor roomData = Resources.getInstance(this).getSpecificRoom(roomName);
            //the cursor will always contain a name column, but if the room is already known the cursor
            //should contain 5 columns
            if (roomData.getCount() > 1) {
                while (roomData.moveToNext()) {
                    //the receiver could already know of a room, which is contained in the new, received
                    //challenge. Therefor a check is to be conducted, if the room has already been
                    //found or not
                    if (roomData.getString(4).equals("true")) {
                        found = true;
                    } else {
                        found = false;
                    }
                }
            } else {
                //if the receiver doesnt know of the room the found variable will be set to false
                found = false;

            }

            //add the room to the database. if it is already known nothing happens, if not it has to be
            //added otherwise the room will never show, if the challenge is selected
            Room room = new Room(null, gps, roomName, points, timestamp, found);
            Resources.getInstance(this).addRoom(room);
            //after adding the room check if there is a room by that name on the server,
            //if that's the case it will be locally deleted and reentered
            Resources.getInstance(this).getAndSaveSpecificRoomServer(roomName);
            roomList.add(room);
        }

        Challenge challenge = new Challenge(challengeName, roomList);
        Resources.getInstance(this).addChallenge(challenge);
        Resources.getInstance(this).getAndSaveSpecificChallengeServer(challengeName);
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        return null;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
