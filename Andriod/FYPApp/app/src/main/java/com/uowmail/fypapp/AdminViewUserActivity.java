package com.uowmail.fypapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class AdminViewUserActivity extends AppCompatActivity  {


    private Object Menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_user);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        TextView userName = (TextView) findViewById(R.id.Select1);

        userName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(AdminViewUserActivity.this, AdminViewUserLogsActivity.class));
  //              getSupportFragmentManager().beginTransaction().replace(R.id.container, adminViewUserLogsActivity).commit();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
