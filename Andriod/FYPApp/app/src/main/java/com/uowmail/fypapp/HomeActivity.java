package com.uowmail.fypapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.AdapterView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    ClientHomeFragment clientHomeFragment = new ClientHomeFragment();
    AdminHomeFragment adminHomeFragment = new AdminHomeFragment();
    NotificationFragment notificationFragment = new NotificationFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    // 1 -> admin,  0 -> client
    private int admin = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);

        // MJ - bottom nav bar -------------------------------------------------------------------------------
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // replace fragment (id = container) with admin/clientHomeFragment
        if (admin == 1)
            getSupportFragmentManager().beginTransaction().replace(R.id.container, adminHomeFragment).commit();
        else
            getSupportFragmentManager().beginTransaction().replace(R.id.container, clientHomeFragment).commit();

        // badge notification alert
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.notification);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(8);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        if (admin == 1){
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, adminHomeFragment).commit();
                        }
                        else
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, clientHomeFragment).commit();
                        return true;

                    case R.id.notification:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, notificationFragment).commit();
                        return true;

                    case R.id.settings:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, settingsFragment).commit();
                        return true;
                }
                return false;
            }
        });
    }
}