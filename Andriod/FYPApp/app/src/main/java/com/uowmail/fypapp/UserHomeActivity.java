package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessaging;

import org.w3c.dom.Text;

public class UserHomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    UserHomeFragment userHomeFragment = new UserHomeFragment();
    AdminHomeFragment adminHomeFragment = new AdminHomeFragment();
    UserNotificationsFragment userNotificationsFragment = new UserNotificationsFragment();
    UserLogsFragment userLogsFragment = new UserLogsFragment();

    public CurrentUserInfo currentUserInfo;
    private FirebaseFirestore fStore;
    //private static int numberOfIntrusion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        // pass current user's info
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            currentUserInfo = (CurrentUserInfo) extras.getSerializable("userInfo");
            Log.d("====CURRENT USER'S ORG ID====", currentUserInfo.getOrgID());
        }


        //getSupportFragmentManager().beginTransaction().replace(R.id.container, userHomeFragment).commit();

        // shania
        fStore = FirebaseFirestore.getInstance();

        // firebase cloud function
        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
        firebaseMessaging.subscribeToTopic("intrusion_detected");


        Fragment homeFragment = UserHomeFragment.newInstance(currentUserInfo.getOrgID());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, homeFragment, "user_home_fragment");
        transaction.addToBackStack(null);
        transaction.commit();

        setContentView(R.layout.activity_user_home);
        bottomNavigationView = findViewById(R.id.bottom_navigation_client);




        // badge notification alert
        if (bottomNavigationView !=null){
            /*BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.notification);
            badgeDrawable.setVisible(true);
            badgeDrawable.setNumber(8);*/

            // shania
            queryForNumberOfIntrusion(currentUserInfo);

            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected( MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.home:
                            //getSupportFragmentManager().beginTransaction().replace(R.id.container, userHomeFragment).commit();
                            Fragment homeFragment = UserHomeFragment.newInstance(currentUserInfo.getOrgID());
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.container, homeFragment, "user_home_fragment");
                            transaction.addToBackStack(null);
                            transaction.commit();
                            return true;

                        case R.id.notification:
                            //getSupportFragmentManager().beginTransaction().replace(R.id.container, userNotificationsFragment).commit();
                            Fragment notificationsFragment = UserNotificationsFragment.newInstance(currentUserInfo.getOrgID());
                            FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
                            transaction3.replace(R.id.container, notificationsFragment, "user_notifications_fragment");
                            transaction3.addToBackStack(null);
                            transaction3.commit();
                            return true;

                        case R.id.logs:
                            //Fragment fragment = UserLogsFragment.newInstance(currentUserInfo.getOrgID());
                            //getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, "user_logs_fragment").commit();

                            Fragment logsFragment = UserLogsFragment.newInstance(currentUserInfo.getOrgID());
                            FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                            transaction2.replace(R.id.container, logsFragment, "user_log_fragment");
                            transaction2.addToBackStack(null);
                            transaction2.commit();
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
        //title.setText("Creeping Donut");
        title.setText("Creeping Donut");
    }

    // MJ - account menu
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void showMenu(View v){

        PopupMenu popupMenu = new PopupMenu(UserHomeActivity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.account_menu, popupMenu.getMenu());

        // shania
        Login main = new Login();
        popupMenu.getMenu().findItem(R.id.username).setTitle(/*"Username: " + */main.getUserName());
        popupMenu.setForceShowIcon(true);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.username) {
                    Toast.makeText(UserHomeActivity.this, "You clicked in the username", Toast.LENGTH_SHORT).show();
                }


                if(menuItem.getItemId() == R.id.settings){
                    // YT open page to user settings
                    //startActivity(new Intent(UserHomeActivity.this, UserSettingsActivity.class));
                    Intent i = new Intent(UserHomeActivity.this, UserSettingsActivity.class);
                    i.putExtra("adminOrgID",currentUserInfo.getOrgID());
                    startActivity(i);
                }
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

    // firestore get the number of notification
    private void queryForNumberOfIntrusion(CurrentUserInfo currentUserInfo){
        String path = currentUserInfo.getOrgID() + "_detection";

        fStore.collection(path)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            int numberOfIntrusion = task.getResult().size();
                            setNotificationBadgeDrawble(numberOfIntrusion);
                            Log.d("=====FIRESTORE_QUERY=====", "COUNT: " + numberOfIntrusion);

                        }else{
                            Log.d("=====FIRESTORE_QUERR=====", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void setNotificationBadgeDrawble(int count){
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.notification);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(count);
    }


}