package com.example.fourtothefloor.rest.services;

import com.example.fourtothefloor.activity.LoginActivity;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Body;

public interface UserInterface {

    @POST("app/login")
    public Call<Integer> signin(@Body LoginActivity.UserInfo userInfo);
}
