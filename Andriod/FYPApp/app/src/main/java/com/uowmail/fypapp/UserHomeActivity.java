package com.uowmail.fypapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class UserHomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    UserHomeFragment userHomeFragment = new UserHomeFragment();
    AdminHomeFragment adminHomeFragment = new AdminHomeFragment();
    NotificationFragment notificationFragment = new NotificationFragment();
    UserLogsFragment userLogsFragment = new UserLogsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_user_home);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, userHomeFragment).commit();
        bottomNavigationView = findViewById(R.id.bottom_navigation_client);

        // badge notification alert
        if (bottomNavigationView !=null){
            BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.notification);
            badgeDrawable.setVisible(true);
            badgeDrawable.setNumber(8);

            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected( MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.home:
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, userHomeFragment).commit();
                            return true;

                        case R.id.notification:
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, notificationFragment).commit();
                            return true;

                        case R.id.logs:
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, userLogsFragment).commit();
                            return true;
                    }
                    return false;
                }
            });
        }

        // MJ - Top toolbar ------------------------------------------------------------------------------------
        ImageView logoIcon = findViewById(R.id.logo);
        ImageView accountIcon = findViewById(R.id.account);
        TextView title = findViewById(R.id.toolbar_title);

        logoIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Go to Homepage when logo is clicked
                getSupportFragmentManager().beginTransaction().replace(R.id.container, userHomeFragment).commit();
            }
        });
        accountIcon.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });
        title.setText("Creeping Donut");
    }

    // MJ - account menu
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void showMenu(View v){

        PopupMenu popupMenu = new PopupMenu(UserHomeActivity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.account_menu, popupMenu.getMenu());

        // shania
        Login main = new Login();
        popupMenu.getMenu().findItem(R.id.username).setTitle("Username: " + main.getUserName());
        popupMenu.setForceShowIcon(true);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.username)
                    Toast.makeText( UserHomeActivity.this, "You clicked in the username", Toast.LENGTH_SHORT).show();
                if(menuItem.getItemId() == R.id.settings)
                    // YT open page to user settings
                    startActivity(new Intent(UserHomeActivity.this, UserSettingsActivity.class));
                if(menuItem.getItemId() == R.id.logout)
                {
                    //Toast.makeText(UserHomeActivity.this, "USER CLICK ON LOGOUT", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
                return false;
            }
        });

        popupMenu.show();
    }
}