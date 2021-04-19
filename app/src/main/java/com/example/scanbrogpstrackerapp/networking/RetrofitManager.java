package com.example.scanbrogpstrackerapp.networking;


import com.example.scanbrogpstrackerapp.domain.entities.AccessToken;

import java.util.Base64;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// This class handles the networking, with help of the Retrofit REST Client.
// It will fetch the authentication token when we log in.
// and store it for future use.
// We can then make authenticated calls to the api, to send GPSEntries.
public class RetrofitManager {

    private RetrofitManager() {}

    private static Retrofit mRetrofit = null;

    // Token for the test API.
    // This is set from the SignInToAPI usecase.
    private static AccessToken testToken;
    public static void setTestToken(AccessToken token) { testToken = token; }

    // This method is used to check if the user is log in or not.
    // No token = user is not logged in.
    // MainAcitvity is using this info to decide if it should kick the user back to the login screen.
    public static boolean hasToken() {
        if(testToken == null) {
            return false;
        }
        return true;
    }

    //  This method is used when we log out.
    // The token is deleted, and hasToken will return false.
    public static void deleteToken() {
        testToken = null;
    }

    private static final String BASE_URL = "https://scanbro-test.azurewebsites.net/api/";

    // The two main methods here are for setting up interfaces to the API.
    // The first one sets up an interface for sending Authentication requests.
    // And stores the access token inside this class.
    // The second one sets up an interface for posting the GPS data to the API.


    // This method sets up the Authentication request interface.
    // It takes a clientId (from the login screen) and uses that together with the ClientSecret
    // to generate the authentication credentials.
    public static IScanBroAuthenticationAPI setupRetrofitForAuthentication(String clientId) {

        // I use an interceptor to place the authentication credentials in the header of the requests.
        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();
        okhttpBuilder.addInterceptor(chain -> {

            String ClientSecret = "388D45FA-B36B-4988-BA59-B187D329C207";
            String Credentials = clientId + ":" + ClientSecret;

            // Add "Basic " to beginning of the header and encode the credentials to base64.
            String basic = "Basic " + Base64.getEncoder().encodeToString(Credentials.getBytes());

            Request request = chain.request();

            // Add the Authentication data to every request.
            Request.Builder newRequest = request.newBuilder().addHeader("Authorization", basic);

            return chain.proceed(newRequest.build());
        });

        // Create a retrofit instance with the correct URL and the interceptors.
        mRetrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .client(okhttpBuilder.build())
                .addConverterFactory(GsonConverterFactory.create()).build();

        // Retrofit creates the Interface that we later use to communicate to the API.
        return mRetrofit.create(IScanBroAuthenticationAPI.class);
    }

    // This method sets up an interface to the RESTFul API.
    // In this case it sets up an interface that allows us to send GPS Entries to the API.
    // And it sets it up with an authentication token, so we can prove to the API
    // that we have permission to send data to it.
    public static IScanbroAPI setupRetrofitWithToken() throws NoStoredTokenException {

        // Throw an exception if we tried to set up this interface before we received the Auth token.
        if(testToken == null) {
            throw new NoStoredTokenException();
        }

        // This interceptor will add the Authorization token to the header of every request we
        // send through this interface.
        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();
        okhttpBuilder.addInterceptor(chain -> {

            Request request = chain.request();
            Request.Builder newRequest = request.newBuilder().header("Authorization", testToken.token_type + " " + testToken.access_token);
            return chain.proceed(newRequest.build());
        });

        // Instantiate Retrofit with correct Base URL and with the interceptor.
        mRetrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .client(okhttpBuilder.build())
                .addConverterFactory(GsonConverterFactory.create()).build();

        // Return the interface.
        return mRetrofit.create(IScanbroAPI.class);
    }
}
