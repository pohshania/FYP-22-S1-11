package com.uowmail.fypapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class AdminViewUserActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_user);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Users");

        TextView userName = (TextView) findViewById(R.id.Select1);

        userName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(AdminViewUserActivity.this, AdminViewUserLogsActivity.class));
  //              getSupportFragmentManager().beginTransaction().replace(R.id.container, adminViewUserLogsActivity).commit();
            }
        });
    }
}
