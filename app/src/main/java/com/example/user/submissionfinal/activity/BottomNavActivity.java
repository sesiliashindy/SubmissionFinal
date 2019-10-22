package com.example.user.submissionfinal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.user.submissionfinal.fragment.SearchFragment;
import com.example.user.submissionfinal.fragment.FavoriteFragment;
import com.example.user.submissionfinal.fragment.MovieFragment;
import com.example.user.submissionfinal.fragment.TvShowFragment;
import com.example.user.submissionfinal.R;

public class BottomNavActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
     private Fragment fragment;
     private final String TAG_FRAGMENT = "FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);

        BottomNavigationView bottomNavigationMain;

        bottomNavigationMain = findViewById(R.id.btn_navigation);
        bottomNavigationMain.setOnNavigationItemSelectedListener(this);
        if(savedInstanceState == null){
            bottomNavigationMain.setSelectedItemId(R.id.navigation_movie);
            fragment = new MovieFragment();
        } else {
            fragment = getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
            loadFragment(fragment);
        }

    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void loadFragment(Fragment fragment) {
        if(fragment != null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame, fragment, TAG_FRAGMENT)
                    .commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.navigation_movie:
                fragment = new MovieFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame,fragment,fragment.getClass().getSimpleName())
                        .commit();
                return true;
            case R.id.navigation_tv_show:
                fragment = new TvShowFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame,fragment,fragment.getClass().getSimpleName())
                        .commit();
                return true;
            case R.id.navigation_favorite:
                fragment = new FavoriteFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame,fragment,fragment.getClass().getSimpleName())
                        .commit();
                return true;
            case R.id.navigation_search:
                fragment = new SearchFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame,fragment,fragment.getClass().getSimpleName())
                        .commit();
                return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.language_settings:
                Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(intent);
                break;

            case R.id.menu_reminder:
                Intent intent1 = new Intent(BottomNavActivity.this, RemainderActivity.class);
                startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
