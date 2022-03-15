package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class RulesActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Spinner spinner;
    // MJ - Pop up window to enter pwd --------------------------------------------------------------------------------------
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button saveChange, cancelBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

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
//                createNewContactDialog();
//                Toast.makeText( RulesActivity.this, "You clicked save change button", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
                    checkBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                    linearLayout.addView(checkBox);
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
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

//    // MJ - Popup window to enter password --------------------------------------------------------------------------------------
//    public void createNewContactDialog(){
//        dialogBuilder = new AlertDialog.Builder(this);
//        final View passwordPopupView = getLayoutInflater().inflate(R.layout.pwdpopup, null);
//
//        saveChange = (Button) passwordPopupView.findViewById(R.id.saveButton);
//        cancelBtn = (Button) passwordPopupView.findViewById(R.id.cancelButton);
//
//        dialogBuilder.setView(passwordPopupView);
//        dialog = dialogBuilder.create();
//        dialog.show();
//
//        saveChange.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                // define save button here!
//                dialog.dismiss();
//            }
//        });
//
//    }
}