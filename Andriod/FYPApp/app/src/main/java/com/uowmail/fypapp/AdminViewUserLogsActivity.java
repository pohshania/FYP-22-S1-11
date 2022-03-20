package com.uowmail.fypapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class AdminViewUserLogsActivity extends AppCompatActivity {

    // MJ - Pop up window to enter pwd --------------------------------------------------------------------------------------
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button saveChange, cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_user_logs);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("User Logs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // MJ - btn to save rules
        Button deleteBtn = (Button) findViewById(R.id.deleteButton);
        CheckBox checkBoxBtn = (CheckBox) findViewById(R.id.checkboxButton1);
        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // define save button here!

                createNewContactDialog();
//                Toast.makeText( RulesActivity.this, "You clicked save change button", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // MJ - Popup window to enter password --------------------------------------------------------------------------------------
    public void createNewContactDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View passwordPopupView = getLayoutInflater().inflate(R.layout.delete_logs_popup, null);

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


}