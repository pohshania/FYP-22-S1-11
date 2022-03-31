package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateNewUserActivity extends AppCompatActivity {
    EditText createUserFullName, createUserEmail, createUserOrgID, createUserPassword;
    Button createAccBtn;
    ProgressBar createUserProgressBar;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user);

        createUserFullName = findViewById(R.id.createUser_fullname);
        createUserEmail    = findViewById(R.id.createUser_email);
        createUserOrgID    = findViewById(R.id.createUser_organisationID);
        createUserPassword = findViewById(R.id.createUser_password);
        createAccBtn       = findViewById(R.id.createUser_createAcc_btn);
        createUserProgressBar = findViewById(R.id.createUser_progressBar);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // handling user registration details validations
        createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName = createUserFullName.getText().toString().trim();
                String email = createUserEmail.getText().toString().trim();
                String orgID = createUserOrgID.getText().toString().trim();
                String password = createUserPassword.getText().toString().trim();

                if(TextUtils.isEmpty(fullName)){
                    createUserFullName.setError("Full Name is required!");
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    createUserEmail.setError("Email is required!");
                    return;
                }
                if(TextUtils.isEmpty(orgID)){
                    createUserOrgID.setError("Organisation ID is required!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    createUserPassword.setError("Password is required!");
                    return;
                }
                if(password.length() < 6){
                    createUserPassword.setError("Password must be >= 6 characters!");
                    return;
                }

                toggleKeyboardAndProgressBar(false, true);

                // register the user into Firebase
                fAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(CreateNewUserActivity.this, "User created successfully!", Toast.LENGTH_SHORT).show();

                        // getting info of the user that was just created
                        FirebaseUser user = fAuth.getCurrentUser();

                        // store user info into firestore
                        DocumentReference df = fStore.collection("Users").document(user.getUid());
                        Map<String,Object> userInfo = new HashMap<>();
                        userInfo.put("Full Name", createUserFullName.getText().toString().trim());
                        userInfo.put("Email", createUserEmail.getText().toString().trim());
                        userInfo.put("Organisation ID", createUserOrgID.getText().toString().trim());
                        // specify user access level
                        userInfo.put("isAdmin",false);

                        // store the user info into firestore
                        df.set(userInfo);

                        createUserProgressBar.setVisibility(View.INVISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateNewUserActivity.this, "Not successful!" + e.toString(), Toast.LENGTH_SHORT).show();
                        toggleKeyboardAndProgressBar(true, false);
                    }
                });
            }
        });

    }

    private void toggleKeyboardAndProgressBar(boolean keyboard, boolean progressbar) {
        if (keyboard == true && progressbar == false) {
            // show the key board
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

            // hide progress bar
            createUserProgressBar.setVisibility(View.INVISIBLE);
        }

        if (keyboard == false && progressbar == true) {
            // hide the key board
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            // show progress bar
            createUserProgressBar.setVisibility(View.VISIBLE);
        }
    }
}