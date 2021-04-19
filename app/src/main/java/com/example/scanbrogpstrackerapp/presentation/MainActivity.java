package com.example.scanbrogpstrackerapp.presentation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scanbrogpstrackerapp.R;
import com.example.scanbrogpstrackerapp.domain.usecases.LogOutFromAPI;
import com.example.scanbrogpstrackerapp.domain.usecases.ToggleGPSTracking;
import com.example.scanbrogpstrackerapp.networking.NoStoredTokenException;
import com.example.scanbrogpstrackerapp.networking.RetrofitManager;

public class MainActivity extends AppCompatActivity {

    TextView messageTextView;
    ToggleGPSTracking toggleGPSTracking;
    LogOutFromAPI logOutFromAPI = new LogOutFromAPI();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // if we haven't stored the Authentication token yet, we log out.
        if(!RetrofitManager.hasToken()){
            logOut();
        }

        // initialize the GPS tracking toggle usecase.
        toggleGPSTracking = new ToggleGPSTracking(getBaseContext());

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        messageTextView = findViewById(R.id.messageTextView);
        setUpButtons();

    }

    private void setUpButtons() {
        Button trackingToggleButton = findViewById(R.id.trackingToggleButton);
        trackingToggleButton.setOnClickListener(v -> toggleTracking());

        Button logOutButton = findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(v -> logOut());
    }

    private void toggleTracking() {
        try {
            toggleGPSTracking.execute();
        } catch (NoStoredTokenException e) {
            logOut();
            Toast.makeText(this, "You are not logged in! Please log in again.", Toast.LENGTH_LONG).show();
        }
    }

    // Logout can happen 2 ways:
    // 1. User clicks the log out button.
    // 2. The app detects that there is no authentication token stored in RetrofitManager.
    private void logOut() {
        logOutFromAPI.execute(getBaseContext(), toggleGPSTracking);
    }
}