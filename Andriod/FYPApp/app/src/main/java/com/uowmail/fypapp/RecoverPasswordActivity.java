package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecoverPasswordActivity extends AppCompatActivity {
    private EditText emailET;
    private Button resetBtn;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Recover Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailET = findViewById(R.id.recoverPassword_email);
        resetBtn = findViewById(R.id.recoverPassword_recover_btn);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = emailET.getText().toString();

                if(TextUtils.isEmpty(email)){
                    emailET.setError("Email cannot be empty!");
                    emailET.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailET.setError("Please provide a valid email address!");
                    emailET.requestFocus();
                    return;
                }

                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                email = emailET.getText().toString();
                Log.d("===RECOVER PASSWORD====", email);
                fAuth.sendPasswordResetEmail(email)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(RecoverPasswordActivity.this, "Check your email to recover your password!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RecoverPasswordActivity.this, "Unsuccessful. " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

//                fAuth.sendPasswordResetEmail(email)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if(task.isSuccessful()){
//                                    Toast.makeText(RecoverPasswordActivity.this, "Check your email to recover your password!", Toast.LENGTH_SHORT).show();
//                                }else{
//                                    Toast.makeText(RecoverPasswordActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
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