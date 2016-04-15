package com.arellomobile.picwall.network;
import com.arellomobile.picwall.network.model.DesktopprResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestAPI {
    String BaseUrl = "https://api.desktoppr.co/";
    @GET("1/wallpapers")
    Call<DesktopprResponse> getPage(@Query("page") int page);

    @GET("1/wallpapers/random")
    Call<DesktopprResponse> getPageRandom();
}
