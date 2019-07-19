package de.tubs.campusjagd.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

import de.tubs.campusjagd.BuildConfig;
import de.tubs.campusjagd.model.Challenge;
import de.tubs.campusjagd.model.Room;

public class DatabaseHelperChallenge extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelperChallenge";

    private static final String TABLE_NAME = "challenge_table";
    private static final String COL_NAME = "name";
    private static final String COL_ROOMS = "rooms";
    private static final String COL_TIMESTAMP = "timestamp";
    private static final String COL_ENDTIMESTAMP = "endtimestamp";
    private static final String COL_TIMEDCHALLENGE = "timedchallenge";


    public DatabaseHelperChallenge(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " ( " +
                COL_NAME + " TEXT PRIMARY KEY," + COL_ROOMS + " TEXT, " + COL_TIMESTAMP + " TEXT, " + COL_ENDTIMESTAMP + " TEXT, " + COL_TIMEDCHALLENGE + " INTEGER)" ;
        db.execSQL(createTable);
    }

    /**
     * Only called if the table needs to be upgraded
     * Here the complete Table will be discarded and the system starts over
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Function to add data to the table, it returns a bool to indicate a
     * successful insertion of the data, or the fail of that operation
     *
     * @param challenge
     * @return
     */
    public boolean addChallenge(Challenge challenge) {
        //Create and/or open a database that will be used for reading and writing
        SQLiteDatabase db = this.getWritableDatabase();
        //ContentValues is used to store a set of values that the ContentResolver can process
        ContentValues contentValues = new ContentValues();

        //it contains the name of the column and the value the column for  this item is supposed to have
        contentValues.put(COL_NAME, challenge.getName());
        List<Room> rooms = challenge.getRoomList();
        String roomListAsString = "";
        for (Room room : rooms) {
            roomListAsString += room.getName() + ";";
        }
        contentValues.put(COL_ROOMS, roomListAsString);
        contentValues.put(COL_TIMESTAMP, challenge.getTimestamp());
        contentValues.put(COL_ENDTIMESTAMP, challenge.getEndTimestamp());
        if (challenge.isTimedChallenge()){
            contentValues.put(COL_TIMEDCHALLENGE, 1);
        }else {
            contentValues.put(COL_TIMEDCHALLENGE, 0);
        }

        Log.d(TAG, "addData: Adding " + challenge.getName() + ", " + roomListAsString +
                " to " + TABLE_NAME);

        //insert the content of the contentValue into the table, the null is optional and used for:
        //SQL doesn't allow inserting a completely empty row without naming at least one column name.
        //If your provided contentValues is empty, no column names are known and an empty row can't be inserted
        //The function returns the rowID of the newly inserted row, or -1 if an error occurred
        long result = -1;
        try {
            result = db.insertOrThrow(TABLE_NAME, null, contentValues);
        } catch (SQLiteConstraintException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }

        //check if data is inserted correctly
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * get a specific challenge for which only the name is known
     *
     * @param challengeName
     * @return
     */
    public Cursor getSpecificChallenge(String challengeName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_NAME + " = '" + challengeName + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * All entries of the table are returned
     *
     * @return
     */
    public Cursor getAllChallenges() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * updates an entry of the database where the ids match
     *
     * @param challenge
     */
    public void updateChallenge(Challenge challenge) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Room> rooms = challenge.getRoomList();
        String roomListAsString = "";
        for (Room room : rooms) {
            roomListAsString += room.getName() + ";";
        }
        String query = "UPDATE " + TABLE_NAME + " SET " + COL_ROOMS + " = '" + roomListAsString + "' WHERE " + COL_NAME + " = '" + challenge.getName() + "'";
        Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }

    /**
     * delete an entry, where id and name match
     *
     * @param name
     */
    public void deleteChallenge(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL_NAME + " = '" + name + "'";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: deleting: " + name);
        db.execSQL(query);
    }

    public void makeChallengeTimed(Challenge challenge, int timed){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL_TIMEDCHALLENGE + " = '" + timed + "' WHERE " + COL_NAME + " = '" + challenge.getName() + "'";
        Log.d(TAG, "timedChallenge: query: " + query);
        db.execSQL(query);
    }

    public void updateTimestamp(Challenge challenge, String timestamp){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL_TIMESTAMP + " = '" + timestamp + "' WHERE " + COL_NAME + " = '" + challenge.getName() + "'";
        Log.d(TAG, "timestampUpdate: query: " + query);
        db.execSQL(query);
    }

    public void setEndTimestamp(Challenge challenge, String timestamp){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL_ENDTIMESTAMP + " = '" + timestamp + "' WHERE " + COL_NAME + " = '" + challenge.getName() + "'";
        Log.d(TAG, "endtimestampUpdate: query: " + query);
        db.execSQL(query);
    }
}
