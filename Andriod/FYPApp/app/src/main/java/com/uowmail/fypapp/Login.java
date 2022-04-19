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
    Button loginBtn;
    ProgressBar loginProgressBar;
    Spinner loginUserTypeSpinner;

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

    private Button testFirestoreQuery;
    private static TestFirestoreQuery testQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //insertTestData(fStore);
        //testQuery(fStore);
        // testing firestore queries - to be deleted
        testFirestoreQuery = findViewById(R.id.testFirestoreQuery_btn);
        testFirestoreQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testQuery = new TestFirestoreQuery();
                testQuery.query();
            }
        });


        loginEmail       = findViewById(R.id.login_email);
        loginPassword    = findViewById(R.id.login_password);
        loginBtn         = findViewById(R.id.login_btn);
        loginProgressBar = findViewById(R.id.login_progressBar);

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
                    toggleKeyboardAndProgressBar(false, false);
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    loginPassword.setError("Password is required!");
                    toggleKeyboardAndProgressBar(false, false);
                    return;
                }
                if(password.length() < 6){
                    loginPassword.setError("Password must be >= 6 characters!");
                    toggleKeyboardAndProgressBar(false, false);
                    return;
                }

                // check if the current selected user type is correct
                checkUserType(fStore, email, password, selectedUserType);
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
                                        Toast.makeText(Login.this, "Login unsuccessful. " + e.toString(), Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(Login.this, "Login unsuccessful. " + e.toString(), Toast.LENGTH_SHORT).show();
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
/*
    public void getCurrentUserOrgID(FirebaseAuth fAuth, FirebaseFirestore fStore){
        // get the current's user organisation ID, so that program will only retrieve docs from that organisation
        String currentUserEmail = fAuth.getCurrentUser().getEmail();
        DocumentReference docRef = fStore.collection("Users Profile").document(currentUserEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    String orgIDValue = document.getString("Organisation ID");
                    Log.d("TEST_QUERY", orgIDValue);
                    setCurrentUserOrgID(orgIDValue);
                }
            }
        });
    }
*/






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

    private void insertTestData(FirebaseFirestore db){
        CollectionReference cities = db.collection("cities");

        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "San Francisco");
        data1.put("state", "CA");
        data1.put("country", "USA");
        data1.put("capital", false);
        data1.put("population", 860000);
        data1.put("regions", Arrays.asList("west_coast", "norcal"));
        cities.document("SF").set(data1);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("name", "Los Angeles");
        data2.put("state", "CA");
        data2.put("country", "USA");
        data2.put("capital", false);
        data2.put("population", 3900000);
        data2.put("regions", Arrays.asList("west_coast", "socal"));
        cities.document("LA").set(data2);

        Map<String, Object> data3 = new HashMap<>();
        data3.put("name", "Washington D.C.");
        data3.put("state", null);
        data3.put("country", "USA");
        data3.put("capital", true);
        data3.put("population", 680000);
        data3.put("regions", Arrays.asList("east_coast"));
        cities.document("DC").set(data3);

        Map<String, Object> data4 = new HashMap<>();
        data4.put("name", "Tokyo");
        data4.put("state", null);
        data4.put("country", "Japan");
        data4.put("capital", true);
        data4.put("population", 9000000);
        data4.put("regions", Arrays.asList("kanto", "honshu"));
        cities.document("TOK").set(data4);

        Map<String, Object> data5 = new HashMap<>();
        data5.put("name", "Beijing");
        data5.put("state", null);
        data5.put("country", "China");
        data5.put("capital", true);
        data5.put("population", 21500000);
        data5.put("regions", Arrays.asList("jingjinji", "hebei"));
        cities.document("BJ").set(data5);
    }

    private void testQuery(FirebaseFirestore db){

        /*
        // Get a document - The following example shows how to retrieve the contents of a single document using get():
        DocumentReference docRef = db.collection("cities").document("SF");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TEST_QUERY", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("TEST_QUERY", "No such document");
                    }
                } else {
                    Log.d("TEST_QUERY", "get failed with ", task.getException());
                }
            }
        });
         */


/*        DocumentReference docRef = db.collection("cities").document("BJ");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                City city = documentSnapshot.toObject(City.class);

                Log.d("TEST_QUERY", city.getCountry() + " " + city.getName()
                        + " " + city.getState() + " " + city.getPopulation() + " " + city.getRegions());


            }
        });*/


        // retrieve all documents id
        CollectionReference colRef = db.collection("UOW_log");
        colRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot d : queryDocumentSnapshots){
                    Log.d("TEST_QUERY", d.getId());
                }
            }
        });

    }
}