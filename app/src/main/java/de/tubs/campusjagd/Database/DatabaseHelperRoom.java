package de.tubs.campusjagd.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.tubs.campusjagd.model.Room;

public class DatabaseHelperRoom extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelperRoom";

    private static final String TABLE_NAME = "room_table";
    private static final String COL_NAME = "name";
    private static final String COL_GPS = "gps";
    private static final String COL_POINTS = "points";
    private static final String COL_TIMESTAMP = "timestamp";
    private static final String COL_ROOMFOUND = "roomFound";

    public DatabaseHelperRoom(Context context){
        super(context, TABLE_NAME, null, 1);
    }

    /**
     * Creating the table, to hold the data, by calling a query called createTable
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " ( " +
                COL_NAME + " TEXT PRIMARY KEY," + COL_GPS + " TEXT, " + COL_POINTS + " INTEGER, " +
                COL_TIMESTAMP + " TEXT, " + COL_ROOMFOUND + " TEXT) ";
        db.execSQL(createTable);
    }

    /**
     * Only called if the table needs to be upgraded
     * Here the complete Table will be discarded and the system starts over
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
     * @param room
     * @return
     */
    public boolean addRoom(Room room){
        //Create and/or open a database that will be used for reading and writing
        SQLiteDatabase db = this.getWritableDatabase();
        //ContentValues is used to store a set of values that the ContentResolver can process
        ContentValues contentValues = new ContentValues();

        //it contains the name of the column and the value the column for  this item is supposed to have
        contentValues.put(COL_NAME, room.getName());
        String s = room.getGps().toString();
        contentValues.put(COL_GPS, room.getGps().toString());
        contentValues.put(COL_POINTS, room.getPoints());
        contentValues.put(COL_TIMESTAMP, Long.toString(room.getTimestamp()));
        if (room.isRoomFound()){
            contentValues.put(COL_ROOMFOUND, "true");
        }else{
            contentValues.put(COL_ROOMFOUND, "false");
        }

        Log.d(TAG, "addData: Adding " + room.getName() +  " " + room.getGps().toString() +
                " "+ room.getPoints() +  " " + room.getTimestamp() +  " " + room.isRoomFound() +
                " to " + TABLE_NAME);

        //insert the content of the contentValue into the table, the null is optional and used for:
        //SQL doesn't allow inserting a completely empty row without naming at least one column name.
        //If your provided contentValues is empty, no column names are known and an empty row can't be inserted
        //The function returns the rowID of the newly inserted row, or -1 if an error occurred
        long result = db.insert(TABLE_NAME, null, contentValues);

        //check if data is inserted correctly
        if (result == -1){
            return false;
        }
        else{
            return true;
        }
    }

    /**
     * get the id of a certain item, if there are multiple items with the same name, the
     * first id is returned
     * @param room
     * @return
     */
    public Cursor getSpecificRoom(Room room){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_NAME + " = '" + room.getName() + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Method for finding rooms when one does not have complete Object, but only the name of the room
     * @param roomName
     * @return
     */
    public Cursor getSpecificRoom(String roomName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_NAME + " = '" + roomName + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * All entries of the tbale are returned
     * @return
     */
    public Cursor getAllRooms(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * updates an entry of the database where the ids match
     * @param room
     */
    public void updateRoomFound(Room room){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL_ROOMFOUND + " = 'true' WHERE "
                + COL_NAME + " = '" + room.getName() + "'";
        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting to: " + true);
        db.execSQL(query);
    }

    public Cursor getRoomsNotFound(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_ROOMFOUND + " != 'true'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * delete an entry, where id and name match
     * @param name
     */
    public void deleteRoom(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL_NAME + " = '" + name + "'";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: deleting: " + name);
        db.execSQL(query);
    }

}
