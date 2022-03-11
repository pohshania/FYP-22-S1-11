package com.uowmail.fypapp;

import android.app.AlertDialog;
import android.content.ClipData;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.zip.Inflater;


public class RulesFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    // MJ - Pop up window to enter pwd --------------------------------------------------------------------------------------
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button saveChange, cancelBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_rules, container, false);

        // MJ - Dropdown box for System type --------------------------------------------------------------------------------------
        spinner = v.findViewById(R.id.spinner_systemType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.systemType,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // MJ - btn to save rules
        Button submitBtn = (Button) v.findViewById(R.id.submitButton);
        submitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // define save button here!
                createNewContactDialog();
            }
        });
        return v;
    }

    // MJ - Dropdown box for System type --------------------------------------------------------------------------------------
    // MJ - List down the rules according to system type-----------------------------------------------------------------------
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String choice = adapterView.getItemAtPosition(i).toString();

        // this is returning null
        LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.ruleContainerr);

        // show list of rules with check box
        switch (choice){
            case "email system":
                // remove all the views first
                linearLayout.removeAllViews();
                // save rules of email system into array
                String[] ruleArray = getResources().getStringArray(R.array.rules_email);

                // create checkboxes for rules
                for (int count = 0; count < ruleArray.length; count++) {
                    CheckBox checkBox = new CheckBox(getActivity());
                    checkBox.setText(ruleArray[count]);
                    checkBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    linearLayout.addView(checkBox);
                }
                break;

            case "System Type 1":
                Toast.makeText( getActivity(),
                        "You clicked SYSTEM TYPE 1",
                        Toast.LENGTH_SHORT).show();
                // remove all the views first
                linearLayout.removeAllViews();
                // save rules of email system into array
                ruleArray = getResources().getStringArray(R.array.rules_1);

                for (int count = 0; count < ruleArray.length; count++) {
                    // create checkboxes for rules
                    CheckBox checkBox = new CheckBox(getActivity());
                    checkBox.setText(ruleArray[count]);
                    checkBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                    linearLayout.addView(checkBox);
                }
                break;
//            default:

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    // MJ - Popup window to enter password --------------------------------------------------------------------------------------
    public void createNewContactDialog(){
        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View passwordPopupView = getLayoutInflater().inflate(R.layout.pwdpopup, null);

        saveChange = (Button) passwordPopupView.findViewById(R.id.saveButton);
        cancelBtn = (Button) passwordPopupView.findViewById(R.id.cancelButton);

        dialogBuilder.setView(passwordPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        saveChange.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // define save button here!
                Toast.makeText( getActivity(), "The changes are saved", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // define cancel button here!
                dialog.dismiss();
            }
        });


    }
}