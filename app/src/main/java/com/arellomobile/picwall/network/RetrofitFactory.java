package com.arellomobile.picwall.network;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    private static RestAPI restApi;


    @NonNull
    public static RestAPI getApiService(){
        if(restApi==null){
            restApi = getRetrofit().create(RestAPI.class);
        }
        return restApi;
    }

    @NonNull
    public static Retrofit getRetrofit() {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss") // "created_at":"2014-06-13T21:35:36.253Z"
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RestAPI.BaseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit;
    }
}
