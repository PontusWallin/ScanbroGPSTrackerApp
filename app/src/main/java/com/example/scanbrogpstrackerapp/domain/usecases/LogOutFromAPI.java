package com.example.scanbrogpstrackerapp.domain.usecases;

import android.content.Context;
import android.content.Intent;

import com.example.scanbrogpstrackerapp.networking.NoStoredTokenException;
import com.example.scanbrogpstrackerapp.networking.RetrofitManager;
import com.example.scanbrogpstrackerapp.presentation.SignInActivity;

public class LogOutFromAPI {

    public void execute(Context context, ToggleGPSTracking toggleGPSTracking){
        RetrofitManager.deleteToken();
        Intent intent = new Intent(context,
                SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        // When we log out, we also want to disable the GPS tracking
        // and send all the GPS records to the API.
        if(toggleGPSTracking != null)
            if(toggleGPSTracking.isRecording()) {
            try {
                toggleGPSTracking.execute();
            } catch (NoStoredTokenException e) {
                // if we were already log out, we don't need to do anything.
            }
        }
    }
}
