package com.example.scanbrogpstrackerapp.networking;



import com.example.scanbrogpstrackerapp.domain.entities.AccessToken;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IScanBroAuthenticationAPI {

    @FormUrlEncoded()
    @POST("/connect/token")
    Call<AccessToken> getToken(@Field("grant_type") String grantType);
}
