package com.example.scanbrogpstrackerapp.data.GPS;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.scanbrogpstrackerapp.data.Database.DatabaseManager;
import com.example.scanbrogpstrackerapp.domain.entities.GPSEntry;

import java.time.ZonedDateTime;
import java.util.Date;

public class GPSManager {

    static final int MIN_TIME_MILLISECONDS = 10000;
    static int GPSEntryCounter = 1;

    Context context;
    LocationManager locationManager;
    LocationListener locationListener;
    static DatabaseManager databaseManager;

    public GPSManager(Context context) {
        this.context = context;
    }

    public void listenForLocationData() {

        if(locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        if(locationListener == null){
            locationListener = location -> storeLocationInDatabase(location);
        }

        // This If-statement will exit the execution if we don't have Location access rights.
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates("gps", MIN_TIME_MILLISECONDS, 0, locationListener);
    }

    public void storeLocationInDatabase(Location location) {
        if(databaseManager == null) {
            databaseManager = new DatabaseManager(context);
        }

        databaseManager.addGPSEntry(
                new GPSEntry(
                        GPSEntryCounter,
                        location.getLongitude(),
                        location.getLatitude()
                )
        );
        GPSEntryCounter++;
    }

    public void stopListeningForLocationData() {
        if(locationManager == null || locationListener == null) {
            Toast.makeText(context, "Failed to stop recording - Recording was never started!", Toast.LENGTH_LONG).show();
            return;
        }

        // De-initialize the location manager, so we no longer look for location updates.
        locationManager.removeUpdates(locationListener);
        locationManager = null;
    }
}
