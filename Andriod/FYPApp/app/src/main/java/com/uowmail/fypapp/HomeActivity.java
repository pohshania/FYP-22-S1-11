package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {


    BottomNavigationView bottomNavigationView;

    ClientHomeFragment clientHomeFragment = new ClientHomeFragment();
    AdminHomeFragment adminHomeFragment = new AdminHomeFragment();
    NotificationFragment notificationFragment = new NotificationFragment();
    SettingsFragment settingsFragment = new SettingsFragment();
    LogsFragment logsFragment = new LogsFragment();

    // true -> admin,  false -> client
    private boolean admin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        // MJ - bottom nav bar -------------------------------------------------------------------------------
        if (getIntent().getSerializableExtra("adminbool").toString().equals("admin"))
            admin = true;
        else
            admin = false;

        // replace fragment (id = container) with admin/clientHomeFragment
        if (admin)
        {
          setContentView(R.layout.activity_home_admin);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, adminHomeFragment).commit();
            bottomNavigationView = findViewById(R.id.bottom_navigation_admin);
        }
        else
        {
            setContentView(R.layout.activity_home_client);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, clientHomeFragment).commit();
            bottomNavigationView = findViewById(R.id.bottom_navigation_client);
        }

        // badge notification alert
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.notification);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(8);


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        if (admin == true){
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

                    case R.id.logs:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, logsFragment).commit();
                        return true;
                }
                return false;
            }
        });
        // MJ - Top toolbar ------------------------------------------------------------------------------------
        ImageView logoIcon = findViewById(R.id.logo);
        ImageView accountIcon = findViewById(R.id.account);
        TextView title = findViewById(R.id.toolbar_title);

        logoIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText( HomeActivity.this, "You clicked in the logo icon", Toast.LENGTH_SHORT).show();
                // Go to Homepage when logo is clicked
            }
        });
        accountIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showMenu(v);
                Toast.makeText( HomeActivity.this, "You clicked in the account icon", Toast.LENGTH_SHORT).show();
            }
        });
        title.setText("Creeping Donut");
    }

    // MJ - account menu
    private void showMenu(View v){
        PopupMenu popupMenu = new PopupMenu(HomeActivity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.account_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.usertype)
                    Toast.makeText( HomeActivity.this, "You clicked in the usertype", Toast.LENGTH_SHORT).show();
                if(menuItem.getItemId() == R.id.username)
                    Toast.makeText( HomeActivity.this, "You clicked in the username", Toast.LENGTH_SHORT).show();
                if(menuItem.getItemId() == R.id.settings)
                    Toast.makeText( HomeActivity.this, "You clicked in the settings", Toast.LENGTH_SHORT).show();
                if(menuItem.getItemId() == R.id.logout)
                    startActivity(new Intent(HomeActivity.this,MainActivity.class));

                return false;
            }
        });

        popupMenu.show();
    }

}