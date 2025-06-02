package com.example.SE104_DoAn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Khởi tạo các view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Tìm NavHostFragment và lấy NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment == null) {
            throw new IllegalStateException("NavHostFragment with ID nav_host_fragment not found in activity_main.xml");
        }
        NavController navController = navHostFragment.getNavController();

        // Cấu hình BottomNavigationView với NavController
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_board, R.id.nav_notifications, R.id.nav_account)
                .build();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
}