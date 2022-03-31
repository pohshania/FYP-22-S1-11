package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Login extends AppCompatActivity {
    // XML variables
    EditText loginEmail, loginPassword;
    Button loginBtn;
    ProgressBar loginProgressBar;
    Spinner loginUserTypeSpinner;

    // firebase variables
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    // userName and passsword
    public static String email, password;

    // user types boolean, true == Admin, false == User
    boolean userTypeBool = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        loginEmail       = findViewById(R.id.login_email);
        loginPassword    = findViewById(R.id.login_password);
        loginBtn         = findViewById(R.id.login_btn);
        loginProgressBar = findViewById(R.id.login_progressBar);

        // set the user types drop down spinner
        loginUserTypeSpinner = (Spinner) findViewById(R.id.login_userType);
        String[] userTypes = getResources().getStringArray(R.array.user_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, userTypes);
        loginUserTypeSpinner.setAdapter(adapter);

        loginUserTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(getApplicationContext(), "UserType Selected: " + item, Toast.LENGTH_SHORT).show();

                if(item == "Admin"){
                    userTypeBool = true;

                }else if(item == "User"){
                    userTypeBool = false;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = loginEmail.getText().toString().trim();
                password = loginPassword.getText().toString().trim();

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

                toggleKeyboardAndProgressBar(false, true);


                fAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Login.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
                        checkUserAccessLevel(authResult.getUser().getUid(), userTypeBool);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "Login unsuccessful. " + e.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("!!!!!! UNSUCCESSFUL LOGIN", "oi unsuccessful la");
                        toggleKeyboardAndProgressBar(true, false);
                    }
                });
            }
        });
    }

    private void checkUserAccessLevel(String uid, Boolean userType) {
        Log.d("debug", "HELLLLLLLLLLLLLLLO OI !!!!!!!!!! :DDDDDDDDDDDDDDDDDDD");

        FirebaseUser user = fAuth.getCurrentUser();

        // Admin
        if(userType == true){
            DocumentReference df = fStore
                    .collection("Admins").document(user.getEmail())
                    .collection("Profile").document("Details");

            // extract data from the document
            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.d("CHECKUSERACCESSLEVEL","onSuccess: " + documentSnapshot.getData());

                    // if user type is admin, go to admin homepage
                    if(documentSnapshot.getBoolean("isAdmin") == true){
                        startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
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
        // User
        else if(userType == false){
            DocumentReference df = fStore
                    .collection("Users").document(user.getEmail())
                    .collection("Profile").document("Details");

            // extract data from the document
            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.d("CHECKUSERACCESSLEVEL","onSuccess: " + documentSnapshot.getData());

                    // user type is user, go to user homepage
                    if(documentSnapshot.getBoolean("isAdmin") == false){
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

    private void toggleKeyboardAndProgressBar(boolean keyboard, boolean progressbar){
        if(keyboard == true && progressbar == false){
            // show the key board
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

            // hide progress bar
            loginProgressBar.setVisibility(View.INVISIBLE);
        }

        if(keyboard == false && progressbar == true){
            // hide the key board
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            // show progress bar
            loginProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public String getUserName(){ return email; }

}