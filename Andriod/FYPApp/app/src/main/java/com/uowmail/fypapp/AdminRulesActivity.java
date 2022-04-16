package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.DataOutputStream;
import java.io.IOException;

public class AdminRulesActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Intent intent = getIntent();

    
    private Spinner spinner;
    // MJ - Pop up window to enter pwd --------------------------------------------------------------------------------------
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button saveChange, cancelBtn;
    private String text;
    private EditText currentAdminEmai;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_rules);
        Button testbutton = findViewById(R.id.test_button);
        testbutton.setOnClickListener(testButtonListener);

        db = FirebaseFirestore.getInstance();

        // MJ - set actionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Rules");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinner = findViewById(R.id.spinner_systemType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.systemType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // MJ - btn to save rules
        Button submitBtn = (Button) findViewById(R.id.submitButton);
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
            saveChange = (Button) findViewById(R.id.submitButton);
            saveChange.setVisibility(View.GONE);
        }

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ruleContainer);
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

    }

    private final View.OnClickListener testButtonListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    getRulesList();
                }
            };


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


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

    // MJ - Popup window to enter password --------------------------------------------------------------------------------------
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
    }


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


    public String[] getRulesList()
    {

        DocumentReference docRef = db.collection("UOW_detection").document("rules");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Log.d("RULES", "Rules data: " + doc.getData());
                    }
                    else {
                        Log.d("RULES", "No such document");
                    }
                }
                else {
                    Log.d("RULES", "get failed with ", task.getException());
                }
            }
        });

        String[] a = {"123"};
        return a;
    }

}