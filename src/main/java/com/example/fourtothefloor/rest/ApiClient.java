package com.example.fourtothefloor.rest;

import android.app.Activity;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient extends Activity {
    public static final String BASE_URL = "http://192.168.64.2/fourtothefloor/public/";
    private static Retrofit retrofit = null;

    public static Retrofit getApiClient(){
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor
        .Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();

        if(retrofit == null){
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
