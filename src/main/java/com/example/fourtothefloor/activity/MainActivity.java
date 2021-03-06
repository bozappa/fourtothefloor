package com.example.fourtothefloor.activity;

/*
References:
https://github.com/sourcey/materiallogindemo/tree/master/app/src/main/res
https://www.udemy.com/course/social-networking-android-app-development-from-scratch/learn/lecture/12911404?start=75#content
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.fourtothefloor.R;
import com.example.fourtothefloor.fragment.FriendsFragment;
import com.example.fourtothefloor.fragment.NewsFeedFragment;
import com.example.fourtothefloor.fragment.NotificationFragment;
import com.example.fourtothefloor.util.BottomNavigationViewHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    NewsFeedFragment newsFeedFragment;
    NotificationFragment notificationFragment;
    FriendsFragment friendsFragment;

    @BindView(R.id.toolbar_title)
    AppCompatTextView toolbarTitle;
    @BindView(R.id.search)
    AppCompatImageView search;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.framelayout)
    androidx.appcompat.widget.ContentFrameLayout framelayout;
    @BindView(R.id.fab)
    com.google.android.material.floatingactionbutton.FloatingActionButton fab;
    @BindView(R.id.bottom_navigation)
    com.google.android.material.bottomnavigation.BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bottomNavigation.inflateMenu(R.menu.bottom_navigation_main);
        bottomNavigation.setItemBackgroundResource(R.color.primary);
        bottomNavigation.setItemTextColor(ContextCompat.getColorStateList(bottomNavigation
        .getContext(), R.color.nav_item_colors));
        bottomNavigation.setItemIconTintList(ContextCompat.getColorStateList(bottomNavigation
        .getContext(), R.color.nav_item_colors));

        BottomNavigationViewHelper.removeShiftMode(bottomNavigation);

        newsFeedFragment = new NewsFeedFragment();
        notificationFragment = new NotificationFragment();
        friendsFragment = new FriendsFragment();

        // Have news feed as initial fragment
        setFragment(newsFeedFragment);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.newsfeed_fragment:
                        setFragment(newsFeedFragment);
                        break;

                    case R.id.profile_fragment:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class).putExtra("uid", FirebaseAuth.getInstance().getCurrentUser().getUid()));
                        break;

                    case R.id.profile_friends:
                        setFragment(friendsFragment);
                        break;

                    case R.id.profile_notification:
                        setFragment(notificationFragment);
                        break;
                }
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, UploadActivity.class));
            }
        });

    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, fragment);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.search)
    public void onViewClicked() {
        startActivity(new Intent (MainActivity.this, SearchActivity.class));
    }
}
