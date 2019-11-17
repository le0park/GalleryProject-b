package com.example.galleryproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public final int READ_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        requestPermissions(
                new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                READ_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case READ_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    setContentView(R.layout.activity_main);

                    BottomNavigationView navView = findViewById(R.id.nav_view);
                    // Passing each menu ID as a set of Ids because each
                    // menu should be considered as top level destinations.
                    AppBarConfiguration appBarConfiguration =
                            new AppBarConfiguration.Builder(
                                    R.id.navigation_all, R.id.navigation_timeLine, R.id.navigation_map).build();


                    NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
                    NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
                    NavigationUI.setupWithNavController(navView, navController);
                } else {
                    System.exit(-1);// permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
