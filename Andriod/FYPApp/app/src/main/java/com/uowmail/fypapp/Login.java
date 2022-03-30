package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Login extends AppCompatActivity {
    EditText loginEmail, loginPassword;
    Button loginBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        loginEmail       = findViewById(R.id.login_email);
        loginPassword    = findViewById(R.id.login_password);
        loginBtn         = findViewById(R.id.login_btn);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    loginEmail.setError("Email is required!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    loginPassword.setError("Password is required!");
                    return;
                }
                if(password.length() < 6){
                    loginPassword.setError("Password must be >= 6 characters!");
                    return;
                }

                /*
                fAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Login.this, "Logged in successfully", Toast.LENGTH_SHORT);
                        //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, adminHomeFragment).commit();
                    }
                });*/

                /*
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // check if the login is successful or not
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
                        }else{ // unsucessful login
                            Toast.makeText(Login.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                */
                fAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Login.this, "Logged in successfully", Toast.LENGTH_SHORT);
                        //startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
                        checkUserAccessLevel(authResult.getUser().getUid());
                    }
                });
            }
        });
    }

    private void checkUserAccessLevel(String uid) {
        Log.d("debug", "HELLLLLLLLLLLLLLLO OI !!!!!!!!!! :DDDDDDDDDDDDDDDDDDD");
        DocumentReference df = fStore.collection("Users").document(uid);

        // extract data from the document
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("CHECKUSERACCESSLEVEL","onSuccess: " + documentSnapshot.getData());

                // if user type is admin, go to admin homepage
                if(documentSnapshot.getBoolean("isAdmin") == true){
                    startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
                    finish();
                }else{ // user type is user, go to user homepage
                    startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
                    finish();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FIREBASE LOGIN FAILURE: ", e.toString());
            }
        });

    }
}