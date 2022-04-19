package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AdminDeleteLogsActivity extends AppCompatActivity implements AdminDeleteLogsAdapter.OnListItemClick{

    // MJ - Pop up window to enter pwd --------------------------------------------------------------------------------------
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button saveChange, cancelBtn;

    // shania
    private RecyclerView mFirestoreList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth fAuth;
    private AdminDeleteLogsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirestoreRecyclerOptions<AdminDeleteLogsModel> options;

    private EditText userEmail, adminPassword;
    private String email, currAdminEmail, currAdminPassword;

    private AppCompatImageView deleteImg;
    public static ProgressBar progressBar;
    public static TextView loadingText;
    public static Button deleteLogsBtn, filterLogsBtn;
    public static TextView noDataFoundText;
    private TextView filterDateText, filterStartTimeText, filterEndTimeText, filterDateTimeSummaryText;
    private int startTimeHour, startTimeMinute, endTimeHour, endTimeMinute;

    // deleting
    private static int counter = 0;
    private ArrayList<String> idOfSelectedLogs = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delete_logs);

        SwipeRefreshLayout swipeRefreshLayout;


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Delete Logs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Firestore
        firebaseFirestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        // XML variables
        progressBar = (ProgressBar) findViewById(R.id.adminDeleteLogs_progress_bar);
        loadingText = findViewById(R.id.adminDeleteLogs_loadingText);
        noDataFoundText = findViewById(R.id.adminDeleteLogs_noDataText);
        filterLogsBtn = findViewById(R.id.adminDeleteLogs_filter_btn);
        deleteLogsBtn = findViewById(R.id.adminDeleteLogs_delete_btn);
        deleteImg = findViewById(R.id.adminDeleteLogs_delete);


        firestoreDefaultLogsQuery();

        // Set recyclerView adapter
        adapter = new AdminDeleteLogsAdapter(options, this);
        mFirestoreList = (RecyclerView) findViewById(R.id.adminDeleteLogs_firestore_list);
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        adapter.notifyItemChanged(0);
        mFirestoreList.setAdapter(adapter);


        // filter date & time bottom sheet
        filterLogsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AdminDeleteLogsActivity.this, R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(AdminDeleteLogsActivity.this.getApplicationContext())
                        .inflate(R.layout.admin_delete_logs_bottom_sheet, (LinearLayout) view.findViewById(R.id.adminDeleteLogs_bottomSheetContainer)
                        );

                // when date filter button is clicked
                filterDateText = bottomSheetView.findViewById(R.id.adminDeleteLogs_filterDate_txt);
                bottomSheetView.findViewById(R.id.adminDeleteLogs_filterPickDate_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDatePickerDialog(view);
                    }
                });

                // when start time filter button is clicked
                filterStartTimeText = bottomSheetView.findViewById(R.id.adminDeleteLogs_filterStartTime_txt);
                bottomSheetView.findViewById(R.id.adminDeleteLogs_filterStartTime_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showStartTimePickerDialog(view);
                    }
                });

                // when end time filter button is clicked
                filterEndTimeText = bottomSheetView.findViewById(R.id.adminDeleteLogs_filterEndTime_txt);
                filterDateTimeSummaryText = bottomSheetView.findViewById(R.id.adminDeleteLogs_filterSummary);
                bottomSheetView.findViewById(R.id.adminDeleteLogs_filterEndTime_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showEndTimePickerDialog(view);
                    }
                });

                // when apply filter button is clicked
                bottomSheetView.findViewById(R.id.adminDeleteLogs_applyFilter_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // error handling: check if the filter are empty, if not cant apply
                        if(checkFilterEmpty()==false){
                            Toast.makeText(AdminDeleteLogsActivity.this, "Filters applied!", Toast.LENGTH_SHORT).show();

                            // formatting the selected date and time for firestore query
                            Date startDateTime = new Date();
                            startDateTime = formatFilteredDateAndStartTime(filterDateText.getText().toString(), filterStartTimeText.getText().toString());
                            Date endDateTime = new Date();
                            endDateTime = formatFilteredDateAndEndTime(filterDateText.getText().toString(), filterEndTimeText.getText().toString());

                            // start new query here
                            firestoreQueryLogsByDateTime(startDateTime, endDateTime);

                            // dismiss the bottom sheet
                            bottomSheetDialog.dismiss();
                        }
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

        deleteLogsBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                deleteImg.setVisibility(View.VISIBLE);
            }
        });


        swipeRefreshLayout = findViewById(R.id.adminDeleteLogs_swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mFirestoreList.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onItemClick(AdminDeleteLogsModel snapshot, int position) {
        Log.d("ITEM_CLICK", "Clicked the item: " + position + " and the ID is: " + snapshot.getDocument_id());

        //adapter.deleteItem(position);
        createNewContactDialog(position);
    }

    private void deleteSelectedItem(int position){
        createNewContactDialog(position);
    }

    // display today (default) logs from firestore
    private void firestoreDefaultLogsQuery(){
        Date startDate = new Date();
        Date endDate = new Date();

        try {

            // TODAY
            LocalDateTime ldt_today = LocalDateTime.now();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            String str_today = format.format(ldt_today);
            Log.d("=====FIRESTORE_DATE_TODAY=====", str_today);


            // TOMORROW
            LocalDateTime ldt_tmr = LocalDateTime.now().plusDays(1);
            DateTimeFormatter format2 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            String str_tmr = format2.format(ldt_tmr);
            Log.d("=====FIRESTORE_DATE_TOMORROW=====", str_tmr);


            String myDate1 = str_today;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            startDate = sdf.parse(myDate1);

            String myDate2 = str_tmr;
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
            endDate = sdf2.parse(myDate2);


        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        // Query from firebase
        String path = "UOW" + "_log";
        Query query = firebaseFirestore.collection(path)
                .orderBy("date", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThan("date", endDate);


        // FirebaseRecyclerOptions
        options = new FirestoreRecyclerOptions.Builder<AdminDeleteLogsModel>()
                .setLifecycleOwner(this)
                .setQuery(query, new SnapshotParser<AdminDeleteLogsModel>() {
                    @NonNull
                    @Override
                    public AdminDeleteLogsModel parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        AdminDeleteLogsModel adminDeleteLogsModel = snapshot.toObject(AdminDeleteLogsModel.class);
                        String docId = snapshot.getId();
                        adminDeleteLogsModel.setDocument_id(docId);
                        return adminDeleteLogsModel;
                    }
                })
                .build();
    }

    // display CHOOSEN DATE & TIME logs from firestore
    private void firestoreQueryLogsByDateTime(Date startDateTime, Date endDateTime){

        Log.d("======firestoreQueryLogsByDateTime======", startDateTime.toString() + "   " + endDateTime.toString());

        // Query from firebase
        String path = "UOW" + "_log";
        Query query = firebaseFirestore.collection(path)
                .orderBy("date", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("date", startDateTime)
                .whereLessThanOrEqualTo("date", endDateTime);

        // FirebaseRecyclerOptions
        FirestoreRecyclerOptions<AdminDeleteLogsModel> new_options = new FirestoreRecyclerOptions.Builder<AdminDeleteLogsModel>()
                .setLifecycleOwner(this)
                .setQuery(query, new SnapshotParser<AdminDeleteLogsModel>() {
                    @NonNull
                    @Override
                    public AdminDeleteLogsModel parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        AdminDeleteLogsModel adminDeleteLogsModel = snapshot.toObject(AdminDeleteLogsModel.class);
                        String docId = snapshot.getId();
                        adminDeleteLogsModel.setDocument_id(docId);
                        return adminDeleteLogsModel;
                    }
                })
                .build();


        // update recycler adapter
        adapter.updateOptions(new_options);
        mFirestoreList.setAdapter(adapter);
    }

    // MJ - Popup window to enter password --------------------------------------------------------------------------------------
    public void createNewContactDialog(int position){
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
        saveChange.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

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
                        Toast.makeText(AdminDeleteLogsActivity.this, "Log deleted.", Toast.LENGTH_SHORT).show();
                        adapter.deleteItem(position);
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminDeleteLogsActivity.this, "Incorrect Admin password." + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void showDatePickerDialog(View view){
        final Calendar newCalendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(AdminDeleteLogsActivity.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                String pattern = "yyyy/MM/dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                filterDateText.setText(simpleDateFormat.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void showStartTimePickerDialog(View view){
        TimePickerDialog timePickerDialog = new TimePickerDialog(AdminDeleteLogsActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        // Initialise hour and minute
                        startTimeHour = i;
                        startTimeMinute = i1;
                        // Store hour and minute in string
                        String time = startTimeHour + ":" + startTimeMinute;
                        // Initialise 24 hours time format
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");

                        try {
                            Date date = f24Hours.parse(time);
                            // Initialised 12 hours time format
                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            // Set selected time on text view
                            filterStartTimeText.setText(f12Hours.format(date));

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, 12, 0, false);
        // Set transparent background
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Display selected time
        timePickerDialog.updateTime(startTimeHour, startTimeMinute);
        // Show dialog
        timePickerDialog.show();

    }

    public void showEndTimePickerDialog(View view){
        TimePickerDialog timePickerDialog = new TimePickerDialog(AdminDeleteLogsActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        // Initialise hour and minute
                        endTimeHour = i;
                        endTimeMinute = i1;
                        // Store hour and minute in string
                        String time = endTimeHour + ":" + endTimeMinute;
                        // Initialise 24 hours time format
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");

                        try {
                            Date date = f24Hours.parse(time);
                            // Initialised 12 hours time format
                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            // Set selected time on text view
                            filterEndTimeText.setText(f12Hours.format(date));

                            // set summary
                            filterDateTimeSummaryText.setText(filterDateText.getText() + ", FROM " +
                                    filterStartTimeText.getText() + " TO " + filterEndTimeText.getText());
                            filterDateTimeSummaryText.setTextColor(Color.parseColor("#008000"));

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, 12, 0, false);
        // Set transparent background
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Display selected time
        timePickerDialog.updateTime(endTimeHour, endTimeMinute);
        // Show dialog
        timePickerDialog.show();

    }

    private boolean checkFilterEmpty(){

        String dateTxt = filterDateText.getText().toString().trim();
        String startTimeTxt = filterStartTimeText.getText().toString().trim();
        String endTimeTxt = filterEndTimeText.getText().toString().trim();

        if(TextUtils.isEmpty(dateTxt)){
            //filterDateText.setError("Date is required!");
            filterDateTimeSummaryText.setText("Date is required!");
            filterDateTimeSummaryText.setTextColor(Color.parseColor("#FF0000"));
            return true;
        }
        if(TextUtils.isEmpty(startTimeTxt)){
            //filterStartTimeText.setError("Start time is required!");
            filterDateTimeSummaryText.setText("Start time is required!");
            filterDateTimeSummaryText.setTextColor(Color.parseColor("#FF0000"));
            return true;
        }
        if(TextUtils.isEmpty(endTimeTxt)){
            //filterEndTimeText.setError("End time is required!");
            filterDateTimeSummaryText.setText("End time is required!");
            filterDateTimeSummaryText.setTextColor(Color.parseColor("#FF0000"));
            return true;
        }
        if(compareTime(startTimeTxt, endTimeTxt)==false){
            filterDateTimeSummaryText.setText("End time is larger than start time!");
            filterDateTimeSummaryText.setTextColor(Color.parseColor("#FF0000"));
            return true;
        }

        return false;
    }

    private Date formatFilteredDateAndStartTime(String selectedDate, String selectedStartTime){
        Log.d("===== FORMAT DATE + START TIME ======", selectedDate + " " + selectedStartTime);

        Date startDateTime = new Date();
        String output = null;

        String myDate1 = selectedDate + " " + selectedStartTime; // -> eg. 2022/04/14 12:00 AM
        //Format of the date defined in the input String
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm aa");

        //Desired format: 24 hour format: Change the pattern as per the need
        DateFormat outputformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


        try {
            //Converting the input String to Date
            startDateTime = df.parse(myDate1);
            //Changing the format of date and storing it in String
            output = outputformat.format(startDateTime);
            Log.d("TEST DATE OUTPUT", output);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return startDateTime;
    }

    private Date formatFilteredDateAndEndTime(String selectedDate, String selectedEndTime){
        Log.d("===== FORMAT DATE + END TIME ======", selectedDate + " " + selectedEndTime);

        Date endDateTime = new Date();
        Date endDateTime2 = new Date();
        String output = null;

        String myDate2 = selectedDate + " " + selectedEndTime; // -> eg. 2022/04/14 12:00 AM

        //Format of the date defined in the input String
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm aa");

        //Desired format: 24 hour format: Change the pattern as per the need
        DateFormat outputformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


        try {
            //Converting the input String to Date
            //endDateTime = df.parse(myDate2);
            //Changing the format of date and storing it in String
            //output = outputformat.format(endDateTime);

            endDateTime = df.parse(myDate2);
            Calendar cal = Calendar.getInstance();
            cal.setTime(endDateTime);
            cal.add(Calendar.MINUTE, 1);
            String newTime = df.format(cal.getTime());
            endDateTime2 = df.parse(newTime);
            output = outputformat.format(endDateTime2);


            Log.d("TEST DATE OUTPUT", output);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return endDateTime2;
    }

    private boolean compareTime(String start, String end){
        SimpleDateFormat sdformat = new SimpleDateFormat("hh:mm aa");
        Date d1 = new Date();
        Date d2 = new Date();

        try {
            d1 = sdformat.parse(start);
            d2 = sdformat.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(d1.compareTo(d2) > 0) {
            return false;
        }

        return true;
    }

    public static void enableFilterButton(){
        filterLogsBtn.setVisibility(View.VISIBLE);
    }

    public static void disableFilterButton(){
        filterLogsBtn.setVisibility(View.INVISIBLE);
    }

    public static void enableDeleteButton(){
        deleteLogsBtn.setVisibility(View.VISIBLE);
    }

    public static void disableDeleteButton(){
        deleteLogsBtn.setVisibility(View.INVISIBLE);
    }

    public static void disableProgressBar(){
        progressBar.setVisibility(View.INVISIBLE);
        loadingText.setVisibility(View.INVISIBLE);
    }

    public static void enableProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
        loadingText.setVisibility(View.VISIBLE);
    }

    public static void showNoDataFoundText(){
        noDataFoundText.setText("There are no logs found in the database on this date.");
    }

    public static void hideNoDateFoundText(){
        noDataFoundText.setText("");
    }
}