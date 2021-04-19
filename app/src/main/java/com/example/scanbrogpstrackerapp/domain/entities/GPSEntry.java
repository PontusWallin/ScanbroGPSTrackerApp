package com.example.scanbrogpstrackerapp.domain.entities;

import com.example.scanbrogpstrackerapp.utilities.DateUtilities;

public class GPSEntry {

    // GPSEntries can be created with two different constructors.
    // The first one is for creating new entries from scratch.
    // The second is for fetching exsisting entries from the local database.

    // This constructor is used when we are creating new GPSEntries from scratch.
    // It takes in location data and an Id from the GPSManager
    // and sets it's created date to the current date, in a date format which the API accepts.
    public GPSEntry(int id, double latitude, double longitude) {
        this.id = id;
        this.createdDateTime = DateUtilities.getCurrentDateTimeWithExpectedFormat();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // This constructor is used by the database manager.
    // In this case the created date is fetched from the database directly in to the constructor.
    public GPSEntry(int id, String createdDateTime, double latitude, double longitude) {
        this.id = id;
        this.createdDateTime = createdDateTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int id;
    public String createdDateTime;
    public double latitude;
    public double longitude;
}
