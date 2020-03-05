package com.example.fourtothefloor.rest.services;

import com.example.fourtothefloor.activity.LoginActivity;
import com.example.fourtothefloor.model.User;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.QueryMap;

public interface UserInterface {

    @POST("app/login")
    public Call<Integer> signin(@Body LoginActivity.UserInfo userInfo);

    @GET("loadownprofile")
    Call<User> loadownprofile(@QueryMap Map<String, String> params);

    @POST("poststatus")
    Call<Integer> uploadStatus(@Body MultipartBody requestBody);

    @POST("uploadImage")
    Call<Integer> uploadImage(@Body MultipartBody requestBody);
}
