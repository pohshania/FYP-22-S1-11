package com.uowmail.fypapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHomeActivity extends AppCompatActivity {
    private Menu menu;
    AdminHomeFragment adminHomeFragment = new AdminHomeFragment();
    public CurrentUserInfo currentUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_admin_home);
        //getSupportFragmentManager().beginTransaction().replace(R.id.container, adminHomeFragment).commit();



        // pass current user's info
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            currentUserInfo = (CurrentUserInfo) extras.getSerializable("adminInfo");
            Log.d("====CURRENT ADMIN'S ORG ID====", currentUserInfo.getOrgID());
        }

        Fragment homeFragment = AdminHomeFragment.newInstance(currentUserInfo.getOrgID());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, homeFragment, "admin_home_fragment");
        transaction.addToBackStack(null);
        transaction.commit();


        // tool bar
        TextView title = findViewById(R.id.toolbar_title);
        title.setText("Creeping Donut");

        // log out button
        ImageView logoutBtn = findViewById(R.id.logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                //Toast.makeText(AdminHomeActivity.this, "ADMIN CLICK ON LOGOUT", Toast.LENGTH_SHORT).show();
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(getApplicationContext(), Login.class));
//                finish();
                showMenu(view);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void showMenu(View v){

        PopupMenu popupMenu = new PopupMenu(AdminHomeActivity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.admin_account_menu, popupMenu.getMenu());

        // shania
        Login main = new Login();
        popupMenu.getMenu().findItem(R.id.username).setTitle(currentUserInfo.getEmail());
        popupMenu.getMenu().findItem(R.id.orgID).setTitle(currentUserInfo.getOrgID());
        popupMenu.setForceShowIcon(true);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.orgID) {
                    Toast.makeText(AdminHomeActivity.this, "Your organisation is " + currentUserInfo.getOrgID() + ".", Toast.LENGTH_SHORT).show();
                }

                if(menuItem.getItemId() == R.id.username) {
                    Toast.makeText(AdminHomeActivity.this, "Your username is " + main.getUserName(), Toast.LENGTH_SHORT).show();
                }
                if(menuItem.getItemId() == R.id.logout)
                {
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