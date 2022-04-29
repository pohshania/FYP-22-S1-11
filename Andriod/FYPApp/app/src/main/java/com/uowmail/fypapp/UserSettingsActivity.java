package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserSettingsActivity extends AppCompatActivity {
    Button btn, resetPasswordBtn;
    private String orgID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_settings);

        // pass current user's info
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            orgID = extras.getString("adminOrgID");
            Log.d("====CURRENT ADMIN'S ORG ID====", orgID);
        }


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
        resetPasswordBtn = findViewById(R.id.resetPasswordButton);

        // send email to admin
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
                } else {
                    Toast.makeText(UserSettingsActivity.this, "Please fill all fields",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        //rules view
        Button rButton;
        rButton = (Button) findViewById(R.id.rulesButton);

        // view applied rules
        //Sets action for rulesButton
        rButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //startActivity(new Intent(UserSettingsActivity.this, UserSettingsRulesActivity.class));

//                startActivity( new Intent(UserSettingsActivity.this,
//                        RulesActivity.class).putExtra(Intent.EXTRA_TEXT, "setInvisible") );

                Intent i = new Intent(UserSettingsActivity.this, UserSettingsRulesActivity.class);
                i.putExtra("adminOrgID", orgID);
                startActivity(i);
            }
        });

        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserSettingsActivity.this, ResetPasswordActivity.class);
                i.putExtra("adminOrgID", orgID);
                startActivity(i);
            }
        });
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
