package com.example.scanbrogpstrackerapp.networking;
import com.example.scanbrogpstrackerapp.domain.entities.GPSEntry;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IScanbroAPI {

    @POST("GPSEntries/bulk")
    Call<Void> submitGPSEntries(@Body List<GPSEntry> body);
}
