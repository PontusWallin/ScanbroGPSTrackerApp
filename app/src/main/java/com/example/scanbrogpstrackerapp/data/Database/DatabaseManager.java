package com.example.scanbrogpstrackerapp.data.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.scanbrogpstrackerapp.domain.entities.GPSEntry;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager extends SQLiteOpenHelper {

    // Database name and version
    public static final String DATABASE_NAME = "ScanbroGPSEntries";
    public static final int DATABASE_VERSION = 1;

    // Table name
    public static final String TABLE_GPS_ENTRIES = "GPSEntries";

    // Column names
    public static final String KEY_ID = "Id";
    public static final String KEY_CREATED_TIME = "createdDateTime";
    public static final String KEY_LATITUDE =  "latitude";
    public static final String KEY_LONGITUDE = "longitude";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_GPSENTRY_TABLE = " CREATE TABLE " + TABLE_GPS_ENTRIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_CREATED_TIME + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT"
                + ")";

        db.execSQL(CREATE_GPSENTRY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GPS_ENTRIES);
        // Create table again
        onCreate(db);
    }

    // This method is called whenever we have succesfully sent GPS data to the API.
    // It ensures that we don't send the same data twice, by clearing the table.
    public void eraseData() {
        this.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + TABLE_GPS_ENTRIES);
        onCreate(this.getWritableDatabase());
    }

    // This method is called each time a new GPS Location value is fetched from our GPS Manager.
    // It stores the GPS value inside the local GPSEntry database.
    public void addGPSEntry(GPSEntry gpsEntry) {

        // Fetching database
        SQLiteDatabase db = this.getWritableDatabase();

        // Adding values
        ContentValues values = new ContentValues();
        values.put(KEY_ID, gpsEntry.id);
        values.put(KEY_CREATED_TIME, gpsEntry.createdDateTime);
        values.put(KEY_LATITUDE, gpsEntry.latitude);
        values.put(KEY_LONGITUDE, gpsEntry.longitude);

        // Inserting Row and closing database connection
        db.insert(TABLE_GPS_ENTRIES, null, values);
        db.close();
    }

    // This method is called when we are ready to send all the GPS entries to the API.
    public List<GPSEntry> getAllGPSEntries() {

        // Fetching database
        SQLiteDatabase db = this.getWritableDatabase();

        // Setting upp Query
        String selectQuery = "SELECT * FROM " + TABLE_GPS_ENTRIES;

        // Setting up Cursor.
        Cursor cursor = db.rawQuery(selectQuery, null);

        List<GPSEntry> gpsEntries = new ArrayList<>();

        // Iterate through the cursor/query results ..
        if(cursor != null) {
            if(cursor.getCount() == 0)
                return gpsEntries;
            cursor.moveToFirst();
            do {
                // .. create a new GPS entry for each result ..
                GPSEntry gpsEntry = new GPSEntry(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        Double.parseDouble(cursor.getString(2)),
                        Double.parseDouble(cursor.getString(3))
                );
                // .. and add that entry to a GPSEntry list ..
                gpsEntries.add(gpsEntry);
            } while (cursor.moveToNext());
        }

        // .. and finally we return that list!
        return gpsEntries;
    }
}
