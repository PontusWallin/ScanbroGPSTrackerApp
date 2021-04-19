package com.example.scanbrogpstrackerapp.domain.usecases;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.scanbrogpstrackerapp.domain.entities.AccessToken;
import com.example.scanbrogpstrackerapp.networking.IScanBroAuthenticationAPI;
import com.example.scanbrogpstrackerapp.networking.RetrofitManager;
import com.example.scanbrogpstrackerapp.presentation.MainActivity;
import com.example.scanbrogpstrackerapp.presentation.SignInActivity;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// This usecase class encapsulates the logic for signing in to the ScanBro GPS API.
// I don't want this logic directly in the SignIn Activity.
// Maybe we want to implement Auto-login in the future, for instance?
// This way the code is more modular and easier to change and re-use in the future.
public class SignInToAPI {

    // The execute-method is the only method of this class.
    // It handles the SignIn Logic. It takes a Weak Reference to the SignIn Activity.
    public void execute(Context context, String clientId) {

        IScanBroAuthenticationAPI scanBroAuthenticationAPI = RetrofitManager.setupRetrofitForAuthentication(clientId);

        Call<AccessToken> authenticationTokenCall = scanBroAuthenticationAPI.getToken("client_credentials");
        authenticationTokenCall.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                // If we don't get a good response code we show the user
                // the error message we receive from the server ..
                if(response.code() != 200) {
                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                    return;
                }

                // .. but if we succeed in fetching the Auth token ..
                AccessToken token = response.body();

                // .. We store it for re-use ..
                RetrofitManager.setTestToken(token);

                // .. and then we continue to the main activity.
                Intent intent = new Intent(context,
                        MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

            @Override
            public void onFailure(Call<AccessToken>  call, Throwable t) {
                Toast.makeText(context, "Failure!", Toast.LENGTH_LONG).show();
            }
        });

    }
}
