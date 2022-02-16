package com.uowmail.fypapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //declare signup button
        Button signNowButton = (Button) findViewById(R.id.signNowButton);

        signNowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)  {
                Toast.makeText(getBaseContext(), "Account registered successfully" , Toast.LENGTH_SHORT ).show();
            }
        });

    }
}