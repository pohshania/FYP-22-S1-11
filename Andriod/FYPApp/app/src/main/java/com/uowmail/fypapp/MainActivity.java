package com.uowmail.fypapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        //Declare login username and password edittext
        EditText loginusrnameTxt = (EditText) findViewById(R.id.loginusrnameTxt);
        EditText loginpwdTxt = (EditText) findViewById(R.id.loginpwdTxt);
        //Declare signup button and login button
        Button loginButton = (Button) findViewById(R.id.loginButton);
        Button signupButton = (Button) findViewById(R.id.signupButton);

        //Sets action for sign up button
        signupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SignUpActivity.class));
            }
        });
        //Sets action for login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                intent.putExtra("adminbool", loginusrnameTxt.getText().toString());
                startActivity(intent);
            }
        });

    }


}