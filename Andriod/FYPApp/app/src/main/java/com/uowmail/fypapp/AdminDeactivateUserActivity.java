package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminDeactivateUserActivity extends AppCompatActivity {

    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private EditText userEmail, adminPassword;
    private Button deactivateUser;
    private String email, currAdminEmail, currAdminPassword;

    // MJ - Pop up window to enter pwd --------------------------------------------------------------------------------------
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button saveChange, cancelBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // MJ - ActionBar setting
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Deactivate User Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_admin_deactivate_user);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        userEmail = findViewById(R.id.deactivateUser_email);
        deactivateUser = findViewById(R.id.deactivateUser_btn);

        deactivateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = userEmail.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    userEmail.setError("Email is required!");
                    userEmail.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    userEmail.setError("Please provide a valid email address!");
                    userEmail.requestFocus();
                    return;
                }

                createNewContactDialog();

            }
        });


    }

    private void deactivateUserFirestore(){
        email = userEmail.getText().toString().trim();


        // Update an existing document
        DocumentReference docIdRef = fStore.collection("Users Profile").document(email);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIREBASE QUERY => ", email + " User exists!");
                        docIdRef.update("isActive", false);
                        Toast.makeText(AdminDeactivateUserActivity.this, email + " is now deactivated!", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        Log.d("FIREBASE QUERY => ", " User does not exist!");
                        Toast.makeText(AdminDeactivateUserActivity.this, "The user email does not exists.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Log.d("FIREBASE QUERY => ", "Failed with: ", task.getException());
                }
            }
        });
    }



    // popup window to enter password --------------------------------------------------------------------------------------
    public void createNewContactDialog(){
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
                        Toast.makeText(AdminDeactivateUserActivity.this, "Correct Admin password!", Toast.LENGTH_SHORT).show();
                        deactivateUserFirestore();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminDeactivateUserActivity.this, "Incorrect Admin password." + e.toString(), Toast.LENGTH_SHORT).show();
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
}