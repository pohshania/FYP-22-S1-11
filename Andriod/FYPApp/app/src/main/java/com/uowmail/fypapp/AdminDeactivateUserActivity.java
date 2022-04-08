package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminDeactivateUserActivity extends AppCompatActivity {

    private FirebaseFirestore fStore;
    private EditText userEmail;
    private Button deactivateUser;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // MJ - ActionBar setting
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Deactivate User Account");

        setContentView(R.layout.activity_admin_deactivate_user);

        fStore = FirebaseFirestore.getInstance();
        userEmail = findViewById(R.id.deactivateUser_email);
        deactivateUser = findViewById(R.id.deactivateUser_btn);


        deactivateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });


    }
}