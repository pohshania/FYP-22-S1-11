package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    // XML variables
    EditText loginEmail, loginPassword;
    Button loginBtn;
    ProgressBar loginProgressBar;
    Spinner loginUserTypeSpinner;
    TextView forgotPasswordTV;

    // firebase variables
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    // userName and passsword
    public static String email, password;

    // user types boolean, true == Admin, false == User
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
        forgotPasswordTV = findViewById(R.id.login_forgotPassword);


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
                }
                if(item.equals("User")){
                    selectedUserType = UserTypes.USER;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                toggleKeyboardAndProgressBar(false, true);

                email = loginEmail.getText().toString().trim();
                password = loginPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    loginEmail.setError("Email is required!");
                    loginEmail.requestFocus();
                    toggleKeyboardAndProgressBar(false, false);
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    loginEmail.setError("Please provide a valid email address!");
                    loginEmail.requestFocus();
                    toggleKeyboardAndProgressBar(false, false);
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    loginPassword.setError("Password is required!");
                    loginPassword.requestFocus();
                    toggleKeyboardAndProgressBar(false, false);
                    return;
                }
                if(password.length() < 6){
                    loginPassword.setError("Password must be >= 6 characters!");
                    loginPassword.requestFocus();
                    toggleKeyboardAndProgressBar(false, false);
                    return;
                }

                // check if the current selected user type is correct
                checkUserType(fStore, email, password, selectedUserType);
            }
        });

        forgotPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, RecoverPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signIn(FirebaseAuth fAuth, String email, String password, UserTypes selectedUserType){
        // sign in
        fAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(Login.this, "Logged in successfully", Toast.LENGTH_SHORT).show();

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
            }
        });
    }

    private boolean checkUserType(FirebaseFirestore db, String email, String password, UserTypes selectedUserType){
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
                                toggleKeyboardAndProgressBar(false, true);
                                userExists = true;
                                fAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(Login.this, "Logged in successfully to Admin.", Toast.LENGTH_SHORT).show();
                                        //startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
                                        setCurrentAdminInfo(fAuth, fStore);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // handle error message
                                        try {
                                            throw task.getException();
                                        } catch(FirebaseAuthInvalidCredentialsException e2) {
                                            Toast.makeText(Login.this, "Login unsuccessful. Invalid credentials.", Toast.LENGTH_SHORT).show();
                                        } catch(FirebaseAuthInvalidUserException e3){
                                            Toast.makeText(Login.this, "Login unsuccessful. Invalid user.", Toast.LENGTH_SHORT).show();
                                        } catch(Exception e5) {
                                            Toast.makeText(Login.this, "Login unsuccessful." + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        //Toast.makeText(Login.this, "Login unsuccessful. " + e.toString(), Toast.LENGTH_SHORT).show();
                                        toggleKeyboardAndProgressBar(false, false);
                                    }
                                });
                            }
                            else {
                                Log.d("FIREBASE QUERY => ", "Admin does not exist!");
                                Toast.makeText(Login.this, "You have either selected the wrong user type or the user email does not exists.", Toast.LENGTH_SHORT).show();
                                toggleKeyboardAndProgressBar(false, false);
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
                                toggleKeyboardAndProgressBar(false, true);
                                userExists = true;

                                // check if the use account is active, to allow them to sign in
                                boolean isActive = document.getBoolean("isActive");
                                Log.d("CURRENT USER ACCOUNT STATUS", String.valueOf(isActive));

                                if(isActive == false){
                                    Toast.makeText(Login.this, "Your account has been deactivated.", Toast.LENGTH_SHORT).show();
                                    toggleKeyboardAndProgressBar(false, false);
                                    return;
                                }

                                fAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(Login.this, "Logged in successfully to User.", Toast.LENGTH_SHORT).show();
                                        //startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
                                        setCurrentUserInfo(fAuth, fStore);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // handle error message
                                        try {
                                            throw task.getException();
                                        } catch(FirebaseAuthInvalidCredentialsException e2) {
                                            Toast.makeText(Login.this, "Login unsuccessful. Invalid credentials.", Toast.LENGTH_SHORT).show();
                                        } catch(FirebaseAuthInvalidUserException e3){
                                            Toast.makeText(Login.this, "Login unsuccessful. Invalid user.", Toast.LENGTH_SHORT).show();
                                        }
                                        catch(Exception e4) {
                                            Toast.makeText(Login.this, "Login unsuccessful." + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        //Toast.makeText(Login.this, "Login unsuccessful. " + e.toString(), Toast.LENGTH_SHORT).show();
                                        toggleKeyboardAndProgressBar(false, false);
                                    }
                                });
                            }
                            else {
                                Log.d("FIREBASE QUERY => ", " User does not exist!");
                                Toast.makeText(Login.this, "You have either selected the wrong user type or the user email does not exists.", Toast.LENGTH_SHORT).show();
                                toggleKeyboardAndProgressBar(false, false);
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

    private void toggleKeyboardAndProgressBar(boolean keyboard, boolean progressbar){

        if(keyboard == true){
            // show the key board
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }

        if(keyboard == false){
            // hide the key board
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }

        if(progressbar == true){
            // show progress bar
            loginProgressBar.setVisibility(View.VISIBLE);
        }

        if(progressbar == false){
            // hide progress bar
            loginProgressBar.setVisibility(View.INVISIBLE);
        }


        /*
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

        if(keyboard == false && progressbar == false){
            // hide the key board
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            // hide progress bar
            loginProgressBar.setVisibility(View.INVISIBLE);

        }
         */
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

    private void setCurrentUserInfo(FirebaseAuth fAuth, FirebaseFirestore fStore){
        Intent i = new Intent(Login.this, UserHomeActivity.class);

        String currentUserEmail = fAuth.getCurrentUser().getEmail();
        DocumentReference docRef = fStore.collection("Users Profile").document(currentUserEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();

                    String fullName = document.getString("Full Name");
                    String email = document.getString("Email");
                    String orgID = document.getString("Organisation ID");
                    boolean isAdmin = document.getBoolean("isAdmin");
                    boolean isActive = document.getBoolean("isActive");

                    CurrentUserInfo currentUserInfo = new CurrentUserInfo(fullName, email, orgID, isAdmin, isActive);

                    Log.d("USER_INFO", currentUserInfo.getFullName() + currentUserInfo.getEmail() +
                            currentUserInfo.getOrgID() + currentUserInfo.isAdmin() + currentUserInfo.isActive());

                    i.putExtra("userInfo", currentUserInfo);
                    startActivity(i);


                    // note to myself - shania
/*                    Intent intent = new Intent(SendingActivity.this, RecievingActivity.class);
                    intent.putExtra("keyName", value);  // pass your values and retrieve them in the other Activity using keyName
                    startActivity(intent);

                    // In RecievingActivity
                    Bundle extras = intent.getExtras();
                    if(extras != null)
                        String data = extras.getString("keyName"); // retrieve the data using keyName


                    // shortest way to recieve data..
                    String data = getIntent().getExtras().getString("keyName","defaultKey");


                    // OR
                    Intent intent = new Intent(view.getContext(), ApplicationActivity.class);
                    intent.putExtra("int", intValue);
                    intent.putExtra("Serializable", object);
                    intent.putExtra("String", stringValue);
                    intent.putExtra("parcelable", parObject);
                    startActivity(intent);

                    // In ApplicationActivity
                    Intent intent = getIntent();
                    Bundle bundle = intent.getExtras();

                    if(bundle != null){
                       int mealId = bundle.getInt("int");
                       Object object = bundle.getSerializable("Serializable");
                       String string = bundle.getString("String");
                       T string = <T>bundle.getString("parcelable");
                       CurrentUserInfo currentUserInfo = (CurrentUserInfo) extras.getSerializable("userInfo");
                    }*/




                }
            }
        });
    }

    private void setCurrentAdminInfo(FirebaseAuth fAuth, FirebaseFirestore fStore){
        Intent i = new Intent(Login.this, AdminHomeActivity.class);

        String currentUserEmail = fAuth.getCurrentUser().getEmail();
        DocumentReference docRef = fStore.collection("Admins Profile").document(currentUserEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();

                    String fullName = document.getString("Full Name");
                    String email = document.getString("Email");
                    String orgID = document.getString("Organisation ID");
                    boolean isAdmin = document.getBoolean("isAdmin");
                    boolean isActive = document.getBoolean("isActive");

                    CurrentUserInfo currentUserInfo = new CurrentUserInfo(fullName, email, orgID, isAdmin, isActive);

                    Log.d("ADMIN_INFO", currentUserInfo.getFullName() + currentUserInfo.getEmail() +
                            currentUserInfo.getOrgID() + currentUserInfo.isAdmin() + currentUserInfo.isActive());

                    i.putExtra("adminInfo", currentUserInfo);
                    startActivity(i);


                    // note to myself - shania
/*                    Intent intent = new Intent(SendingActivity.this, RecievingActivity.class);
                    intent.putExtra("keyName", value);  // pass your values and retrieve them in the other Activity using keyName
                    startActivity(intent);

                    // In RecievingActivity
                    Bundle extras = intent.getExtras();
                    if(extras != null)
                        String data = extras.getString("keyName"); // retrieve the data using keyName


                    // shortest way to recieve data..
                    String data = getIntent().getExtras().getString("keyName","defaultKey");


                    // OR
                    Intent intent = new Intent(view.getContext(), ApplicationActivity.class);
                    intent.putExtra("int", intValue);
                    intent.putExtra("Serializable", object);
                    intent.putExtra("String", stringValue);
                    intent.putExtra("parcelable", parObject);
                    startActivity(intent);

                    // In ApplicationActivity
                    Intent intent = getIntent();
                    Bundle bundle = intent.getExtras();

                    if(bundle != null){
                       int mealId = bundle.getInt("int");
                       Object object = bundle.getSerializable("Serializable");
                       String string = bundle.getString("String");
                       T string = <T>bundle.getString("parcelable");
                       CurrentUserInfo currentUserInfo = (CurrentUserInfo) extras.getSerializable("userInfo");
                    }*/




                }
            }
        });
    }
}