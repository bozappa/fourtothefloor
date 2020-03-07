package com.example.fourtothefloor.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.example.fourtothefloor.R;
import com.example.fourtothefloor.adapter.ProfileViewPagerAdapter;
import com.example.fourtothefloor.model.User;
import com.example.fourtothefloor.rest.ApiClient;
import com.example.fourtothefloor.rest.services.UserInterface;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    @BindView(R.id.profile_cover)
    AppCompatImageView profileCover;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.profile_option_btn)
    Button profileOptionBtn;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.ViewPager_profile)
    ViewPager ViewPagerProfile;

    ProfileViewPagerAdapter profileViewPagerAdapter;

    /*
    0 = Profile is loading
    1 = Users are "friends"
    2 = Friend request is sent
    3 = Friend request received
    4 = Users are not friends
    5 = Own profile
     */
    int current_state = 0;
    String profileUrl = "", coverUrl = "";

    ProgressDialog progressDialog;
    int imageUploadType = 0;

    String uid = "0";
    File compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //For hiding status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_profile);

        uid = getIntent().getStringExtra("uid");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        ButterKnife.bind(this);

        profileViewPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager(), 1);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_white);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });

        ViewPagerProfile.setAdapter(profileViewPagerAdapter);

        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equalsIgnoreCase(uid)) {
            // when uid matches then own profile is loaded
            current_state = 5;
            profileOptionBtn.setText(R.string.edit_profile_btn);
            loadProfile();
        } else {
            // other profiles are loaded
        }
        profileOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_state == 5) {
                    CharSequence options[] = new CharSequence[]{"Change cover photo", "Change Profile picture",
                            "View cover photo", "View Profile picture"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setOnDismissListener(ProfileActivity.this);
                    builder.setTitle("Choose Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            if (position == 0) {
                                // Change the cover photo
                                imageUploadType = 1;
                                ImagePicker.create(ProfileActivity.this)
                                        .folderMode(true)
                                        .single().toolbarFolderTitle("Choose a folder")
                                        .toolbarImageTitle("Select an image")
                                        .start();
                            } else if (position == 1) {
                                // Change the profile picture
                                imageUploadType = 0;
                                ImagePicker.create(ProfileActivity.this)
                                        .folderMode(true)
                                        .single().toolbarFolderTitle("Choose a folder")
                                        .toolbarImageTitle("Select an image")
                                        .start();
                            } else if (position == 2) {
                                // View cover profile
                                viewFullImage(profileCover,coverUrl);
                            } else {
                                // View profile picture
                                viewFullImage(profileImage,profileUrl);
                            }
                        }
                    });
                    builder.show();
                }
            }
        });
    }

    // To view the profile image/cover in fullscreen
    private void viewFullImage(View view, String link) {
        Intent intent = new Intent(ProfileActivity.this, FullImageActivity.class);
        intent.putExtra("imageUrl", link);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View,String>(view, "shared");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }

    }

    private void loadProfile() {
        UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
        Map<String, String> params = new HashMap<>();
        params.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        Call<User> call = userInterface.loadownprofile(params);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressDialog.dismiss();
                if (response.body() != null) {
                    profileUrl = response.body().getProfileUrl();
                    coverUrl = response.body().getCoverUrl();
                    collapsingToolbar.setTitle(response.body().getName());

                    if (!profileUrl.isEmpty()) {
                        Picasso.with(ProfileActivity.this).load(profileUrl).into(profileImage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(ProfileActivity.this).load(profileUrl).into(profileImage);
                            }
                        });

                        if (!coverUrl.isEmpty()) {
                            Picasso.with(ProfileActivity.this).load(coverUrl).into(profileCover, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(ProfileActivity.this).load(coverUrl).into(profileCover);
                                }
                            });
                        }

                        addImageCoverClick();

                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Something went wrong, try again later", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Something went wrong, try again later", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void addImageCoverClick() {
        profileCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFullImage(profileCover, coverUrl);
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFullImage(profileImage, profileUrl);
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image selectedImage = ImagePicker.getFirstImageOrNull(data);

            try {
                compressedImageFile = new Compressor(this)
                        .setQuality(75)
                        .compressToFile(new File(selectedImage.getPath()));

                uploadFile(compressedImageFile);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadFile(final File compressedImageFile) {
        progressDialog.setTitle("Loading");
        progressDialog.show();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("postUserId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        builder.addFormDataPart("imageUploadType", imageUploadType + "");
        builder.addFormDataPart("file", compressedImageFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"),
                compressedImageFile));

        MultipartBody multipartBody = builder.build();

        UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
        Call<Integer> call = userInterface.uploadImage(multipartBody);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                progressDialog.dismiss();
                if (response.body() != null && response.body() == 1) {
                    if (imageUploadType == 0) {
                        Picasso.with(ProfileActivity.this).load(compressedImageFile).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.default_image_placeholder).into(profileImage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(ProfileActivity.this).load(compressedImageFile).networkPolicy(NetworkPolicy.OFFLINE)
                                        .placeholder(R.drawable.default_image_placeholder).into(profileImage);
                            }
                        });
                        Toast.makeText(ProfileActivity.this, "Profile picture changed successfully", Toast.LENGTH_LONG).show();
                    } else {
                        Picasso.with(ProfileActivity.this).load(compressedImageFile).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.default_image_placeholder).into(profileCover, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(ProfileActivity.this).load(compressedImageFile).networkPolicy(NetworkPolicy.OFFLINE)
                                        .placeholder(R.drawable.default_image_placeholder).into(profileCover);
                            }
                        });
                        Toast.makeText(ProfileActivity.this, "Cover photo changed successfully", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(ProfileActivity.this, "Something went wrong, try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Something went wrong, try again.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}

