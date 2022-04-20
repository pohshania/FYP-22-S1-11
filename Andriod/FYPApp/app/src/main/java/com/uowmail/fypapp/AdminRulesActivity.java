package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AdminRulesActivity extends AppCompatActivity {

    Intent intent = getIntent();

    // Steven - admin setting rules
    private EditText cpumaxText;
    private EditText netrecvText;
    private EditText netsendText;
    private EditText diskreadText;
    private EditText diskwritText;

    
    // private Spinner spinner;
    // MJ - Pop up window to enter pwd --------------------------------------------------------------------------------------
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button saveChange, cancelBtn;
    private String text;
    private FirebaseFirestore db;

    // shania
    private FirebaseAuth fAuth;
    private EditText userEmail, adminPassword;
    private String email, currAdminEmail, currAdminPassword;
    private String orgID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_rules);

        // pass current user's info
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            orgID = extras.getString("adminOrgID");
            Log.d("====CURRENT ADMIN'S ORG ID====", orgID);
        }


        Button testbutton = findViewById(R.id.test_button);
        testbutton.setOnClickListener(testButtonListener);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        // Steven - set rules edittext
        cpumaxText = findViewById(R.id.cpumaxText);
        netrecvText = findViewById(R.id.netrecvText);
        netsendText = findViewById(R.id.netsendText);
        diskreadText = findViewById(R.id.diskreadText);
        diskwritText = findViewById(R.id.diskwritText);

        // MJ - set actionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Rules");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.systemType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // MJ - btn to save rules
        Button submitBtn = (Button) findViewById(R.id.adminRules_submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // define save button here!
                createNewContactDialog();
            }
        });

        // MJ
        // get value from intent
        // get the text from MainActivity
        Intent intent = getIntent();
        text = intent.getStringExtra(Intent.EXTRA_TEXT);
        // use the text in a TextView
        if (text != null) {
            // delete save button in rulesActivity
            saveChange = (Button) findViewById(R.id.adminRules_submit_btn);
            saveChange.setVisibility(View.GONE);
        }

    }

    private final View.OnClickListener testButtonListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    updateRules();
                }
            };


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

    /*
    @SuppressLint("ResourceAsColor")
    @Override
    // MJ - Dropdown box for System type --------------------------------------------------------------------------------------
    // MJ - List down the rules according to system type-----------------------------------------------------------------------
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String choice = adapterView.getItemAtPosition(i).toString();

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ruleContainer);

        // show list of rules with check box
        switch (choice){
            case "email system":
                // remove all the views first
                linearLayout.removeAllViews();
                // save rules of email system into array
                String[] ruleArray = getResources().getStringArray(R.array.rules_email);

                for (int count = 0; count < ruleArray.length; count++) {
                    // create checkboxes for rules
                    CheckBox checkBox = new CheckBox(this);
                    checkBox.setText(ruleArray[count]);
//                    checkBox.onClickListeners(onCheckboxClicked);
                    checkBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    linearLayout.addView(checkBox);

//                  add the text box here
                    EditText editText = new EditText(this);
                    editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    editText.getLayoutParams().width = 300;
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    linearLayout.addView(editText);


                    // MJ - if user opens rules page
                    if (text!=null)
                    {
                        // disable checkbox
                        checkBox.setEnabled(false);
                        checkBox.setTextColor(Color.parseColor("#000000"));
                        // remove checkbox icon
//                        checkBox.setButtonDrawable(null);


                        // disable spinner
//                        spinner.setEnabled(false);
//                        spinner.setTextColor(Color.parseColor("#000000"));
                    }
                    checkBox.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            // define save button here!
                            onCheckboxClicked(v);
                        }
                    });

                }
                break;

            case "System Type 1":
                // remove all the views first
                linearLayout.removeAllViews();
                // save rules of email system into array
                ruleArray = getResources().getStringArray(R.array.rules_1);

                for (int count = 0; count < ruleArray.length; count++) {
                    // create checkboxes for rules
                    CheckBox checkBox = new CheckBox(this);
                    checkBox.setText(ruleArray[count]);
                    checkBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    linearLayout.addView(checkBox);
                    // MJ - if user opens rules page
                    if (text!=null)
                    {
                        //disable checkbox
                        checkBox.setEnabled(false);
                        checkBox.setTextColor(Color.parseColor("#000000"));
                        checkBox.setButtonDrawable(null);
                    }
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    */

/*    // MJ - Popup window to enter password --------------------------------------------------------------------------------------
    public void createNewContactDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View passwordPopupView = getLayoutInflater().inflate(R.layout.popup_for_password, null);

        saveChange = (Button) passwordPopupView.findViewById(R.id.saveButton);
        cancelBtn = (Button) passwordPopupView.findViewById(R.id.cancelButton);

        dialogBuilder.setView(passwordPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        saveChange.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){


                // define save button here!
                dialog.dismiss();
            }
        });
    }*/


// save statusof check box
    public void onCheckboxClicked(View view){
        // Perform action on click
        // MJ
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox:
                if (checked){
                    try{
                        Process su = Runtime.getRuntime().exec("su");
                        DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
                        outputStream.writeBytes("cp /system/build.prop /system/build.prop.bak\n");
                        outputStream.writeBytes("echo 'persist.sys.scrollingcache=3' >> /system/build.prop\n");
                        outputStream.flush();

                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        su.waitFor();
                    }catch(IOException e){

                    }catch(InterruptedException e){

                    }
                }else{
                    try{
                        Process su = Runtime.getRuntime().exec("su");
                        DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

                        outputStream.writeBytes("rm -r /system/build.prop\n");
                        outputStream.writeBytes("mv /system/build.prop.bak /system/build.prop\n");
                        outputStream.flush();

                        outputStream.writeBytes("exit\n");
                        outputStream.flush();
                        su.waitFor();
                    }catch(IOException e){

                    }catch(InterruptedException e){

                    }
                    break;
                }

        }

    }


    public void updateRules()
    {
        Map<String, Object> cpumax = new HashMap<>();
        Map<String, Object> netrecv = new HashMap<>();
        Map<String, Object> netsend = new HashMap<>();
        Map<String, Object> diskread = new HashMap<>();
        Map<String, Object> diskwrit = new HashMap<>();
        String cpumaxvalue = cpumaxText.getText().toString();
        String netrecvvalue = netrecvText.getText().toString();
        String netsendvalue = netsendText.getText().toString();
        String diskreadvalue = diskreadText.getText().toString();
        String diskwritvalue = diskwritText.getText().toString();

        if (cpumaxvalue.equals("")) {
            cpumax.put("enabled", false);
            cpumax.put("value", 0);
        }
        else {
            cpumax.put("enabled", true);
            cpumax.put("maximum", Float.parseFloat(cpumaxvalue));
        }

        if (netrecvvalue.equals("")) {
            netrecv.put("enabled", false);
            netrecv.put("value", 0);
        }
        else {
            netrecv.put("enabled", true);
            netrecv.put("value", Float.parseFloat(netrecvvalue));
        }
        if (netsendvalue.equals("")) {
            netsend.put("enabled", false);
            netsend.put("value", 0);
        }
        else {
            netsend.put("enabled", true);
            netsend.put("value", Float.parseFloat(netsendvalue));
        }

        if (diskreadvalue.equals("")) {
            diskread.put("enabled", false);
            diskread.put("value", 0);
        }
        else {
            diskread.put("enabled", true);
            diskread.put("value", Float.parseFloat(diskreadvalue));
        }
        if (diskwritvalue.equals("")) {
            diskwrit.put("enabled", false);
            diskwrit.put("value", 0);
        }
        else {
            diskwrit.put("enabled", true);
            diskwrit.put("value", Float.parseFloat(diskwritvalue));
        }

        String path = orgID+"_detection";
        db.collection(path).document("rules")
                .update(
                        "CPU", cpumax,
                        "disk_read", diskread,
                        "disk_writ", diskwrit,
                        "net_recv", netrecv,
                        "net_send", netsend
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("=====FIRESTORE_QUERR=====", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("=====FIRESTORE_QUERR=====", "Error updating document", e);
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
                        Toast.makeText(AdminRulesActivity.this, "Correct Admin password! Rules are updated.", Toast.LENGTH_SHORT).show();
                        updateRules();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminRulesActivity.this, "Incorrect Admin password." + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}