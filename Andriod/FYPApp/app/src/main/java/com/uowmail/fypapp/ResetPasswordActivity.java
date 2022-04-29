package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText emailET, passwordET, confirmPasswordET;
    private Button submit_btn;
    private ProgressBar progressBar;
    private String email, password, confirmPassword;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailET = findViewById(R.id.resetPassword_email);
        passwordET = findViewById(R.id.resetPassword_password_1);
        confirmPasswordET = findViewById(R.id.resetPassword_password_2);
        submit_btn = findViewById(R.id.resetPassword_submit_btn);
        progressBar = findViewById(R.id.resetPassword_progressBar);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailET.getText().toString();
                password = passwordET.getText().toString();
                confirmPassword = confirmPasswordET.getText().toString();

                // check if the email box or password boxes are empty
                if(TextUtils.isEmpty(email)){
                    emailET.setError("Email cannot be empty!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    passwordET.setError("Password cannot be empty!");
                    return;
                }
                if(password.length() < 6){
                    passwordET.setError("New password must be >= 6 characters!");
                    return;
                }
                if(TextUtils.isEmpty(confirmPassword)){
                    confirmPasswordET.setError("Confirm password cannot be empty!");
                    return;
                }

                // Check if the entered email is the same as the current's user email
                fAuth = FirebaseAuth.getInstance();
                if(!email.equals(fAuth.getCurrentUser().getEmail()))
                {
                    emailET.setError("Email must be your own email address.");
                    return;
                }

                // check if both the password are the same
                if (!password.equals(confirmPassword)) {
                    confirmPasswordET.setError("Confirm password and password should be the same!");
                    return;
                }

                // if successful, update the user's password in firebase auth
                enableProgressBar();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                user.updatePassword(confirmPassword)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ResetPasswordActivity.this, "Your password has been reset successfully.", Toast.LENGTH_SHORT).show();
                                passwordET.getText().clear();
                                confirmPasswordET.getText().clear();
                                disableProgressBar();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ResetPasswordActivity.this, "Your password has been is unsuccessfull. " + e, Toast.LENGTH_SHORT).show();
                            }
                        });
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

    private void enableProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }
    private void disableProgressBar(){
        progressBar.setVisibility(View.INVISIBLE);
    }
}