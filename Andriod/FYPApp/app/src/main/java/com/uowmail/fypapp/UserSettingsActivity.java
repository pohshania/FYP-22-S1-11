package com.uowmail.fypapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("User Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        //rules view
        Button rButton;
        rButton = (Button) findViewById(R.id.rulesButton);

        //Sets action for rulesButton
        rButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(UserSettingsActivity.this, UserRulesActivity.class));
            }
        });
    }
}
