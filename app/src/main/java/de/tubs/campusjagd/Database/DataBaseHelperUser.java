package de.tubs.campusjagd.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelperUser extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelperUser";

    private static final String TABLE_NAME = "user_table";
    private static final String COL_NAME = "name";
    private static final String COL_SCORE = "score";

    public DataBaseHelperUser(Context context){
        super(context, TABLE_NAME, null, 1);
    }

    /**
     * Creating the table, to hold the data, by calling a query called createTable
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " ( " +
                COL_NAME + " TEXT PRIMARY KEY, " + COL_SCORE + " INTEGER) ";
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
     * @param username
     * @return
     */
    public boolean addUser(String username){
        //Create and/or open a database that will be used for reading and writing
        SQLiteDatabase db = this.getWritableDatabase();
        //ContentValues is used to store a set of values that the ContentResolver can process
        ContentValues contentValues = new ContentValues();

        //it contains the name of the column and the value the column for  this item is supposed to have
        contentValues.put(COL_NAME, username);
        contentValues.put(COL_SCORE, 0);

        Log.d(TAG, "addData: Adding " + username + " with score " + 0 + " to " + TABLE_NAME);

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
     * The table will only contain the username of the owner of the phone, therefore there is only one
     * entry present in the database. Bacause of this we can retunr all the contents of the db
     * @return
     */
    public Cursor getUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * sets the new username at the place of the old one
     * @param oldUsername
     * @param username
     */
    public void updateUsername(String oldUsername, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL_NAME + " = '" + username +"' WHERE "
                + COL_NAME + " = '" + oldUsername + "'";
        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting to: " + true);
        db.execSQL(query);
    }

    public void setScore(String username, int points) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL_SCORE + " = '" + points + "' WHERE "
                + COL_NAME + " = '" + username + "'";
        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting to: " + true);
        db.execSQL(query);
    }
}
