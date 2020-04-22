package com.example.fourtothefloor.rest.services;

import com.example.fourtothefloor.activity.LoginActivity;
import com.example.fourtothefloor.activity.ProfileActivity;
import com.example.fourtothefloor.adapter.PostAdapter;
import com.example.fourtothefloor.fragment.bottomsheets.CommentBottomSheet;
import com.example.fourtothefloor.model.CommentModel;
import com.example.fourtothefloor.model.FriendsModel;
import com.example.fourtothefloor.model.PostModel;
import com.example.fourtothefloor.model.User;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.QueryMap;

public interface UserInterface {

    @POST("login")
    Call<Integer> signin(@Body  LoginActivity.UserInfo userInfo);

    @GET("loadownprofile")
    Call<User> loadownProfile(@QueryMap Map<String, String> params);

    @GET("loadotherprofile")
    Call<User> loadOtherProfile(@QueryMap Map<String, String> params);

    @POST("poststatus")
    Call<Integer> uploadStatus(@Body MultipartBody requestBody);

    @POST("uploadImage")
    Call<Integer> uploadImage(@Body MultipartBody requestBody);

    @GET("search")
    Call<List<User>> search(@QueryMap Map<String, String> params);

    @GET("profiletimeline")
    Call<List<PostModel>> getProfilePosts(@QueryMap Map<String, String> params);

    @GET("gettimelinepost")
    Call<List<PostModel>> getTimeline(@QueryMap Map<String, String> params);

    @GET("loadfriends")
    Call<FriendsModel> loadFriendsData(@QueryMap Map<String, String> params);

    @POST("performaction")
    Call<Integer> performAction(@Body ProfileActivity.PerformAction performAction);

    @POST("likeunlike")
    Call<Integer> likeUnlike(@Body PostAdapter.AddLike addLike);

    @POST("postcomment")
    Call<CommentModel> postComment(@Body CommentBottomSheet.AddComment addComment);

    @GET("retrievetopcomment")
    Call<CommentModel> retrieveTopComments(@QueryMap Map<String, String> params);

    @GET("retrievelowlevelcomment")
    Call<List<CommentModel.Comment>> retrieveLowLevelComment(@QueryMap Map<String, String> params);
}
