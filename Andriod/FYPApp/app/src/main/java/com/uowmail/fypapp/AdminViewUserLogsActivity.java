package com.uowmail.fypapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AdminViewUserLogsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_user_logs);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("User Logs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


}