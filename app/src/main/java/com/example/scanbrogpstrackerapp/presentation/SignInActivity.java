package com.example.scanbrogpstrackerapp.presentation;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.example.scanbrogpstrackerapp.R;
import com.example.scanbrogpstrackerapp.domain.usecases.SignInToAPI;
import com.example.scanbrogpstrackerapp.utilities.DevUtilities;

import java.lang.ref.WeakReference;

public class SignInActivity extends Activity {

    SignInToAPI signInToAPI;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInToAPI = new SignInToAPI();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // This code makes it easier to debug the app by auto logging in.
        // Debug mode can be disabled.
        if (DevUtilities.isDebugMode()) {
            DevUtilities.autoLogin(getBaseContext());
        }


        EditText editTextClientName = findViewById(R.id.editTextClientName);

        Button signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(v -> signIn(editTextClientName.getText().toString()));
    }

    public void signIn(String clientId){
        signInToAPI.execute(getBaseContext(), clientId);
    }
}
