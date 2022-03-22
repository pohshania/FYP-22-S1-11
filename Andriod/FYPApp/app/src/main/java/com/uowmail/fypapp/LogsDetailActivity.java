package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

public class LogsDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs_detail);

        // MJ - ActionBar setting
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Logs Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // MJ - enable back button in ActionBar
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