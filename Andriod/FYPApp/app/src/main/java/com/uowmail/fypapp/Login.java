package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Login extends AppCompatActivity {
    // XML variables
    EditText loginEmail, loginPassword;
    Button loginBtn, loginTestFirebaseQueryBtn;
    ProgressBar loginProgressBar;
    Spinner loginUserTypeSpinner;

    // firebase variables
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    // userName and passsword
    public static String email, password;

    // user types boolean, true == Admin, false == User
    boolean userTypeBool;
    boolean userExists;
    boolean adminExists;

    enum UserTypes {
        ADMIN,
        USER
    }
    UserTypes selectedUserType;

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
        loginTestFirebaseQueryBtn = findViewById(R.id.login_testFirebase_btn);

        // set the user types drop down spinner
        loginUserTypeSpinner = (Spinner) findViewById(R.id.login_userType);
        String[] userTypes = getResources().getStringArray(R.array.user_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_textcolor_layout, userTypes);
        loginUserTypeSpinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.spinner_selected_item_layout);

        loginUserTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString().trim();

                if(item.equals("Admin")){
                    selectedUserType = UserTypes.ADMIN;
                    //Toast.makeText(getApplicationContext(), "UserType Selected: " + item + " " + selectedUserType.toString(), Toast.LENGTH_SHORT).show();
                }
                if(item.equals("User")){
                    selectedUserType = UserTypes.USER;
                    //Toast.makeText(getApplicationContext(), "UserType Selected: " + item + " " + selectedUserType.toString(), Toast.LENGTH_SHORT).show();
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


        // Firebase quering testing & debuggin
        loginTestFirebaseQueryBtn.setOnClickListener(new View.OnClickListener() {
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

                // check if the current selected user type is correct
                checkUserType(fStore, email, selectedUserType);
                if(userExists == true){
                    Log.d("CHECK USERTYPE", "DATS RIGHT");

                    // sign in
                    fAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(Login.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
                            //checkUserAccessLevel(authResult.getUser().getUid(), userTypeBool);
                            //checkUserAccessLevel2(fStore, email, selectedUserType);

                            switch(selectedUserType){
                                case ADMIN:
                                    startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
                                    finish();
                                    break;
                                case USER:
                                    startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
                                    finish();
                                    break;
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login.this, "Login unsuccessful. " + e.toString(), Toast.LENGTH_SHORT).show();
                            //Log.d("!!!!!! UNSUCCESSFUL LOGIN", "oi unsuccessful la");
                            //toggleKeyboardAndProgressBar(true, false);
                        }
                    });
                }
                else{
                    Log.d("CHECK USERTYPE", "FUCK U RETYPE LOGIN DETAILS");
                    loginProgressBar.setVisibility(View.INVISIBLE);
                    //toggleKeyboardAndProgressBar(true, false);
                }


            }
        });
    }

    private boolean checkUserType(FirebaseFirestore db, String email, UserTypes selectedUserType){
        DocumentReference docIdRef;

        switch(selectedUserType){
            case ADMIN:
                docIdRef = db.collection("Admins Profile").document(email);
                docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("FIREBASE QUERY => ", email + " Admin exists!");
                                userExists = true;
                            }
                            else {
                                Log.d("FIREBASE QUERY => ", "Admin does not exist!");
                                Toast.makeText(Login.this, "Nigga either you selected the wrong user type or user does not exists.", Toast.LENGTH_SHORT).show();
                                userExists = false;
                            }
                        }
                        else {
                            Log.d("FIREBASE QUERY => ", "Failed with: ", task.getException());
                        }
                    }
                });
                break;
            case USER:
                docIdRef = db.collection("Users Profile").document(email);
                docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("FIREBASE QUERY => ", email + " User exists!");
                                userExists = true;
                            }
                            else {
                                Log.d("FIREBASE QUERY => ", " User does not exist!");
                                Toast.makeText(Login.this, "Nigga either you selected the wrong user type or user does not exists.", Toast.LENGTH_SHORT).show();
                                userExists = false;
                            }
                        }
                        else {
                            Log.d("FIREBASE QUERY => ", "Failed with: ", task.getException());
                        }
                    }
                });
                break;
            default:
        }
        return userExists;
    }

    private void checkUserAccessLevel2(FirebaseFirestore db, String uid, UserTypes selectedUserType){
        FirebaseUser user = fAuth.getCurrentUser();
        DocumentReference df;

        switch(selectedUserType){
            case ADMIN:
                df = db.collection("Admins Profile").document(user.getEmail());
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
                break;
            case USER:
                df = db.collection("Users Profile").document(user.getEmail());
                df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d("CHECKUSERACCESSLEVEL","onSuccess: " + documentSnapshot.getData());

                        // if user type is admin, go to admin homepage
                        if(documentSnapshot.getBoolean("isAdmin") == true){
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
                break;
            default:
        }
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

                    // if user type is admin, go to admin homepage
                    if(documentSnapshot.getBoolean("isAdmin") == true){
                        startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
                        finish();
                    }
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

    private boolean fStoreQueryIfUserEmailExists(FirebaseFirestore db, String userEmail){
        DocumentReference docIdRef = db.collection("Users Profile").document(userEmail);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIREBASE QUERY => ", userEmail + " User exists!");
                        userExists = true;
                    } else {
                        Log.d("FIREBASE QUERY => ", "User does not exist!");
                        userExists = false;
                    }
                } else {
                    Log.d("FIREBASE QUERY => ", "Failed with: ", task.getException());
                }
            }
        });
        return userExists;
    }

    private boolean fStoreQueryIfAdminEmailExists(FirebaseFirestore db, String adminEmail){
        DocumentReference docIdRef = db.collection("Users Profile").document(adminEmail);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIREBASE QUERY => ", adminEmail + " Admin exists!");
                        adminExists = true;
                    } else {
                        Log.d("FIREBASE QUERY => ", "Admin does not exist!");
                        adminExists = false;
                    }
                } else {
                    Log.d("FIREBASE QUERY => ", "Failed with: ", task.getException());
                }
            }
        });
        return adminExists;
    }


/*=======================================================FOR TESTING=======================================================*/
    private void insertUsersDataToFirestore(FirebaseFirestore db){
        CollectionReference usersProfile = db.collection("Users Profile");

        Map<String, Object> data1 = new HashMap<>();
        data1.put("Full Name", "John Doe");
        data1.put("Email", "user1@gmail.com");
        data1.put("Organisation ID", "UOW");
        data1.put("isAdmin", false);
        data1.put("Phone Number", Arrays.asList("999", "995"));
        usersProfile.document("user1@gmail.com").set(data1);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("Full Name", "Harry Potter");
        data2.put("Email", "user2@gmail.com");
        data2.put("Organisation ID", "UOW");
        data2.put("isAdmin", false);
        data2.put("Phone Number", Arrays.asList("991", "101"));
        usersProfile.document("user2@gmail.com").set(data2);

        Map<String, Object> data3 = new HashMap<>();
        data3.put("Full Name", "Tony Stark");
        data3.put("Email", "user3@gmail.com");
        data3.put("Organisation ID", "SIM");
        data3.put("isAdmin", false);
        data3.put("Phone Number", Arrays.asList("991", "101"));
        usersProfile.document("user3@gmail.com").set(data3);

        Map<String, Object> data4 = new HashMap<>();
        data4.put("Full Name", "Peter Parker");
        data4.put("Email", "user4@gmail.com");
        data4.put("Organisation ID", "SIM");
        data4.put("isAdmin", false);
        data4.put("Phone Number", Arrays.asList("000", "222"));
        usersProfile.document("user4@gmail.com").set(data4);

        Map<String, Object> data5 = new HashMap<>();
        data5.put("Full Name", "James Bond");
        data5.put("Email", "user5@gmail.com");
        data5.put("Organisation ID", "SPY");
        data5.put("isAdmin", false);
        data5.put("Phone Number", Arrays.asList("007", "700","070"));
        usersProfile.document("user5@gmail.com").set(data5);
    }

    private void insertAdminsDataToFirestore2(FirebaseFirestore db){
        CollectionReference adminsProfile = db.collection("Admins Profile");

        Map<String, Object> data1 = new HashMap<>();
        data1.put("Full Name", "Jane Doe");
        data1.put("Email", "admin1@gmail.com");
        data1.put("Organisation ID", "UOW");
        data1.put("isAdmin", true);
        data1.put("Phone Number", Arrays.asList("001", "002"));
        adminsProfile.document("admin1@gmail.com").set(data1);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("Full Name", "Lee Hsien Loong");
        data2.put("Email", "admin2@gmail.com");
        data2.put("Organisation ID", "SPY");
        data2.put("isAdmin", true);
        data2.put("Phone Number", Arrays.asList("003", "004"));
        adminsProfile.document("admin2@gmail.com").set(data2);

        Map<String, Object> data3 = new HashMap<>();
        data3.put("Full Name", "Ali Baba");
        data3.put("Email", "admin3@gmail.com");
        data3.put("Organisation ID", "SIM");
        data3.put("isAdmin", true);
        data3.put("Phone Number", Arrays.asList("005", "006"));
        adminsProfile.document("admin3@gmail.com").set(data3);

    }
}