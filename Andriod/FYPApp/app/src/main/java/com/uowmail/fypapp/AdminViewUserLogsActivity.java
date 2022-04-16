package com.uowmail.fypapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminViewUserLogsActivity extends AppCompatActivity {

    // MJ - Pop up window to enter pwd --------------------------------------------------------------------------------------
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button saveChange, cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_user_logs);
        SwipeRefreshLayout swipeRefreshLayout;


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("User Logs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // MJ - btn to save rules
        Button deleteBtn = (Button) findViewById(R.id.deleteButton);
//        CheckBox checkBoxBtn = (CheckBox) findViewById(R.id.checkboxButton1);
//        TableRow tableRow1 = (TableRow) findViewById(R.id.TableRow1);
        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                // define save button here!
//                public void onCheckboxClicked(View v) {
//                    // Is the view now checked?
//                    boolean checked = ((CheckBox) v).isChecked();
//                    if (checkBoxBtn = checked) {
//                        tableRow1.remove;
//                    }
//                    }

                createNewContactDialog();
            }

//                Toast.makeText( RulesActivity.this, "You clicked save change button", Toast.LENGTH_SHORT).show();
//            }
        });

//        TextView time = null;
//        TextView domain = null;
//        TextView loc = null;
//        TextView ip = null;
//        TableRow tableRow = null;
//        TableLayout tableLayout = null;
//
//        TableLayout adminLogsTable = (TableLayout) findViewById(R.id.admin_view_user_logs_table);
//        ImageView arrow = null;
        swipeRefreshLayout = findViewById(R.id.adminRefreshLogsLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                tableRow.setBackgroundColor(Color.WHITE);
//                tableRow.setLayoutParams(new TableRow.LayoutParams(
//                        TableRow.LayoutParams.MATCH_PARENT,
//                        TableRow.LayoutParams.MATCH_PARENT));
//
//                time.setText("1 second ago");
//                time.setPadding(5, 5, 5, 5);
//                tableRow.addView(time);
//
//                domain.setText("MineCraft.com");
//                domain.setPadding(5, 5, 5, 5);
//                tableRow.addView(domain);
//
//                loc.setText("China");
//                loc.setPadding(5, 5, 5, 5);
//                tableRow.addView(loc);
//
//                ip.setText("137.46.762");
//                ip.setPadding(5, 5, 5, 5);
//                tableRow.addView(ip);
//
//                arrow.setImageResource(R.drawable.arrow_circle_right);
//                tableRow.addView(arrow);
//
//                // Add to the top row
//                adminLogsTable.addView(tableRow, 1);

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    // MJ - Popup window to enter password --------------------------------------------------------------------------------------
    public void createNewContactDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View passwordPopupView = getLayoutInflater().inflate(R.layout.popup_for_delete_logs, null);

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