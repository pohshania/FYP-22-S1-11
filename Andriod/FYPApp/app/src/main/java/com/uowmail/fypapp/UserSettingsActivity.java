package com.uowmail.fypapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.uowmail.fypapp.databinding.ActivityUserSettingsBinding;

public class UserSettingsActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityUserSettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_user_settings);
        //appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        //Implement volume on and off below

        //Implement email system
        EditText email;
        EditText subject;
        EditText body;
        Button button;

        email = (EditText) findViewById(R.id.email_et);
        subject = (EditText) findViewById(R.id.subject_et);
        body = (EditText) findViewById(R.id.rules_et);
        button = (Button) findViewById(R.id.sendButton);

        //Sets action for sendButton
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!email.getText().toString().isEmpty() && !body.getText().toString().isEmpty()
                        && !subject.getText().toString().isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email.getText().toString()});
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject.getText().toString());
                    intent.putExtra(Intent.EXTRA_TEXT, body.getText().toString());
                    intent.setData(Uri.parse("mailto:"));

                    startActivity(intent);
                }
                else{
                    Toast.makeText(UserSettingsActivity.this, "Please fill all fields",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_user_settings);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}