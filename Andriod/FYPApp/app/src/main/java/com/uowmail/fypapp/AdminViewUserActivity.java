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
import androidx.appcompat.widget.SearchView;

public class AdminViewUserActivity extends AppCompatActivity  {


    private Object Menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_user);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        ImageView userName = (ImageView) findViewById(R.id.Next1);

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

    // Search User function
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {

        getMenuInflater().inflate(R.menu.search_user, menu);
        MenuItem menuItem = menu.findItem(R.id.user_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Seach User");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Set table layout for search here
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
