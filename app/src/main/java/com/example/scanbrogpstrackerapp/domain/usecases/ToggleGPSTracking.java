package com.example.scanbrogpstrackerapp.domain.usecases;

import android.content.Context;
import android.location.GpsStatus;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.scanbrogpstrackerapp.data.Database.DatabaseManager;
import com.example.scanbrogpstrackerapp.data.GPS.GPSManager;
import com.example.scanbrogpstrackerapp.domain.entities.GPSEntry;
import com.example.scanbrogpstrackerapp.networking.IScanbroAPI;
import com.example.scanbrogpstrackerapp.networking.NoStoredTokenException;
import com.example.scanbrogpstrackerapp.networking.RetrofitManager;
import com.example.scanbrogpstrackerapp.presentation.MainActivity;
import com.example.scanbrogpstrackerapp.utilities.DevUtilities;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// This usecase class encapsulates the logic for toggling GPSTracking on and off.
// I don't want this logic directly in the Main Activity.
// So I am putting it inside a module here, so it can be re-used in several places easily
public class ToggleGPSTracking {

    // This use case class need to hold a database manager and a gps manager.
    // It needs to interact both with GPS locations and with the database.
    DatabaseManager databaseManager;
    GPSManager gpsManager;
    List<GPSEntry> gpsEntries;

    IScanbroAPI scanbroAPI;

    private static Boolean isRecording = false;
    public boolean isRecording() {return isRecording;}
    Context context;
    public ToggleGPSTracking(Context context) {
        databaseManager = new DatabaseManager(context);
        gpsManager = new GPSManager(context);
        this.context = context;

    }

    public void execute() throws NoStoredTokenException {

        if(!isRecording) {
            isRecording = true;

            gpsManager.listenForLocationData();
            Toast.makeText(context, "Recording", Toast.LENGTH_LONG).show();

        } else {
            isRecording = false;

            // This method stops the listening for location data
            // BUT it also stores the GPSEntries to the database.
            // this is a side-effect, that should not be handled by this class..
            gpsManager.stopListeningForLocationData();

            gpsEntries = databaseManager.getAllGPSEntries();
            sendGPSEntries();

            Toast.makeText(context, "Not recording", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendGPSEntries() throws NoStoredTokenException {

        // Setting upp the interface to the API.
        if(scanbroAPI == null) {
            scanbroAPI = RetrofitManager.setupRetrofitWithToken();
        }

        // Calling
        Call<Void> authenticationTokenCall = scanbroAPI.submitGPSEntries(gpsEntries);
        authenticationTokenCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {


                // It we don't get a successful response we show the error message to the user
                // and we try to send everyting again.
                if(response.code() != 200) {
                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                    retry();
                    return;
                }

                // If we succeed in sending the GPS data.
                // We display a success message..
                Toast.makeText(context, "GPS Entries were successfully submitted!", Toast.LENGTH_LONG).show();

                // ..and clear the database.
                databaseManager.eraseData();
            }

            @Override
            public void onFailure(Call<Void>  call, Throwable t) {
                Toast.makeText(context, "Failure!", Toast.LENGTH_LONG).show();
                retry();
            }
        });
    }

    private static final int RETRY_TIME_IN_MILLISECONDS = 600000;
    // Creates a new thread, which will send the GPS entries again after a delay.
    private void retry(){

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            try {
                sendGPSEntries();
            } catch (NoStoredTokenException e) {
                e.printStackTrace();
            }
        }, 5000);
    }
}