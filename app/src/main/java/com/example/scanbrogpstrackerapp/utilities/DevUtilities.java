package com.example.scanbrogpstrackerapp.utilities;

import android.content.Context;
import com.example.scanbrogpstrackerapp.domain.usecases.SignInToAPI;

public class DevUtilities {

    static final Boolean DEBUG = false;
    static final String CLIENT_ID = "test-app";

    public static Boolean isDebugMode() { return DEBUG;}

    // The SignInToAPI usecase is also used here, not only in the SignIn activity itself.
    // This demonstrates how useful it is to encapsulate logic in separate modules. :)
    public static void autoLogin(Context context){
        SignInToAPI signInToAPI = new SignInToAPI();
        signInToAPI.execute(context, CLIENT_ID);
    }
}
