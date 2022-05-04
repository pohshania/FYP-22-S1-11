package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AdminCreateNewUserActivity extends AppCompatActivity {
    EditText createUserFullName, createUserEmail, createUserOrgID, createUserPassword;
    Button createAccBtn;
    ProgressBar createUserProgressBar;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button saveChange, cancelBtn;
    private EditText userEmail, adminPassword;
    private String email, currAdminEmail, currAdminPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // MJ - ActionBar setting
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create New User");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_admin_create_new_user);

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
                    createUserFullName.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    createUserEmail.setError("Email is required!");
                    createUserEmail.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    createUserEmail.setError("Please provide a valid email address!");
                    createUserEmail.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(orgID)){
                    createUserOrgID.setError("Organisation ID is required!");
                    createUserOrgID.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    createUserPassword.setError("Password is required!");
                    createUserPassword.requestFocus();

                    return;
                }
                if(password.length() < 6){
                    createUserPassword.setError("Password must be >= 6 characters!");
                    createUserPassword.requestFocus();
                    return;
                }

                createNewContactDialog(email, password);
            }
        });

    }

    public void createNewContactDialog(String email, String password){
        dialogBuilder = new AlertDialog.Builder(this);
        final View passwordPopupView = getLayoutInflater().inflate(R.layout.popup_for_password, null);

        saveChange = (Button) passwordPopupView.findViewById(R.id.saveButton);
        cancelBtn = (Button) passwordPopupView.findViewById(R.id.cancelButton);

        dialogBuilder.setView(passwordPopupView);
        dialog = dialogBuilder.create();
        dialog.show();


        cancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // define save button here!
                dialog.dismiss();
            }
        });

        // authentication
        saveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currAdminEmail = fAuth.getCurrentUser().getEmail();

                adminPassword = (EditText) dialog.findViewById(R.id.admin_password);
                currAdminPassword = adminPassword.getText().toString().trim();

                if(TextUtils.isEmpty(currAdminPassword)){
                    adminPassword.setError("Password is required!");
                    return;
                }

                Log.d("ADMIN INFO", currAdminEmail + currAdminPassword);

                fAuth.signInWithEmailAndPassword(currAdminEmail, currAdminPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //Toast.makeText(AdminCreateNewUserActivity.this, "Correct Admin password! Rules are updated.", Toast.LENGTH_SHORT).show();
                        createUserInFirebase(email, password);
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminCreateNewUserActivity.this, "Unsuccessful." + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void createUserInFirebase(String email, String password){
        // check if the user is already in the system
        DocumentReference docIdRef = fStore.collection("Users Profile").document(email);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Log.d("FIREBASE QUERY => ", userEmail + " User exists!");
                        Toast.makeText(AdminCreateNewUserActivity.this, email + " This user exists!", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Log.d("FIREBASE QUERY => ", "User does not exist! Can create this new user!");
                        //toggleKeyboardAndProgressBar(false, true);


                        // register the user into Firebase
                        fAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(AdminCreateNewUserActivity.this, "User created successfully!", Toast.LENGTH_SHORT).show();

                                // getting info of the user that was just created
                                FirebaseUser user = fAuth.getCurrentUser();

                                CollectionReference usersProfile = fStore.collection("Users Profile");

                                Map<String, Object> data = new HashMap<>();
                                data.put("Full Name", createUserFullName.getText().toString().trim());
                                data.put("Email", createUserEmail.getText().toString().trim());
                                data.put("Organisation ID", createUserOrgID.getText().toString().trim());
                                data.put("isAdmin", false);
                                data.put("isActive", true);
                                data.put("Phone Number", Arrays.asList("000", "001"));
                                usersProfile.document(user.getEmail()).set(data);

                                createUserProgressBar.setVisibility(View.INVISIBLE);

                                createUserFullName.getText().clear();
                                createUserEmail.getText().clear();
                                createUserOrgID.getText().clear();
                                createUserPassword.getText().clear();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AdminCreateNewUserActivity.this, "Unsuccessful. " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                //toggleKeyboardAndProgressBar(true, false);
                            }
                        });
                    }
                } else {
                    Log.d("FIREBASE QUERY => ", "Failed with: ", task.getException());
                }
            }
        });
    }

    private void fStoreQueryIfUserEmailExists(FirebaseFirestore db, String userEmail){
        DocumentReference docIdRef = db.collection("Users Profile").document(userEmail);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Log.d("FIREBASE QUERY => ", userEmail + " User exists!");
                        Toast.makeText(AdminCreateNewUserActivity.this, userEmail + " User exists!", Toast.LENGTH_SHORT).show();


                    } else {
                        Log.d("FIREBASE QUERY => ", "User does not exist!");

                    }
                } else {
                    Log.d("FIREBASE QUERY => ", "Failed with: ", task.getException());
                }
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