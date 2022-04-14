package com.uowmail.fypapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class UserLogsFragment extends Fragment implements UserLogsAdapter.OnListItemClick{
    
    private RecyclerView mFirestoreList;
    private FirebaseFirestore firebaseFirestore;
    private UserLogsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirestoreRecyclerOptions<UserLogsModel> options;
    public static ProgressBar progressBar;
    public static TextView loadingText;
    public static Button datePickerbtn, logsFilterBtn;
    public static TextView dateText;
    private static String chosenDate1, chosenDate2;
    public static TextView noDataFoundText;
    private TextView filterDateText, filterStartTimeText, filterEndTimeText, filterDateTimeSummaryText;
    private int startTimeHour, startTimeMinute, endTimeHour, endTimeMinute;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // View
        View view = inflater.inflate(R.layout.fragment_user_logs2, container, false);

        // MJ - set title of the page
        TextView title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        title.setText("Logs");

        // Firestore
        firebaseFirestore = FirebaseFirestore.getInstance();

        // XML variables
        progressBar = (ProgressBar) view.findViewById(R.id.userLogs_progress_bar);
        loadingText = view.findViewById(R.id.userLogs_loadingText);
        datePickerbtn = (Button) view.findViewById(R.id.userLogs_datePicker);
        dateText = view.findViewById(R.id.userLogs_dateText);
        noDataFoundText = view.findViewById(R.id.userLogs_noDataText);
        logsFilterBtn = (Button) view.findViewById(R.id.userLogs_filter_btn);
        //filterDatePickerBtn = view.findViewById(R.id.userLogs_filterPickDate_btn);



        // Call default firestore recyclerview query
        firestoreDefaultLogsQuery(view);

        // Set recyclerView adapter
        adapter = new UserLogsAdapter(options, this);
        mFirestoreList = (RecyclerView) view.findViewById(R.id.firestore_list2);
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        adapter.notifyItemChanged(0);
        mFirestoreList.setAdapter(adapter);

        // date picker
        datePickerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar newCalendar = Calendar.getInstance();
                final DatePickerDialog  datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        String pattern = "yyyy/MM/dd";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                        dateText.setText(simpleDateFormat.format(newDate.getTime()));
                        chosenDate1 = simpleDateFormat.format(newDate.getTime());

                        Calendar newDate2 = Calendar.getInstance();
                        newDate2.set(year, monthOfYear, dayOfMonth+1);
                        String pattern2 = "yyyy/MM/dd";
                        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
                        chosenDate2 = simpleDateFormat2.format(newDate2.getTime());


                        Log.d("===CHOSENDATE1", chosenDate1);
                        Log.d("===CHOSENDATE2", chosenDate2);

                        //firestoreQueryLogsByDateTime2(view, chosenDate1, chosenDate2);

                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        // filter date & time bottom sheet
        logsFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getContext().getApplicationContext())
                        .inflate(R.layout.user_logs_bottom_sheet, (LinearLayout) view.findViewById(R.id.userLogs_bottomSheetContainer)
                        );

                // when date filter button is clicked
                filterDateText = bottomSheetView.findViewById(R.id.userLogs_filterDate_txt);
                bottomSheetView.findViewById(R.id.userLogs_filterPickDate_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDatePickerDialog(view);
                    }
                });

                // when start time filter button is clicked
                filterStartTimeText = bottomSheetView.findViewById(R.id.userLogs_filterStartTime_txt);
                bottomSheetView.findViewById(R.id.userLogs_filterStartTime_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showStartTimePickerDialog(view);
                    }
                });

                // when end time filter button is clicked
                filterEndTimeText = bottomSheetView.findViewById(R.id.userLogs_filterEndTime_txt);
                filterDateTimeSummaryText = bottomSheetView.findViewById(R.id.userLogs_filterSummary);
                bottomSheetView.findViewById(R.id.userLogs_filterEndTime_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showEndTimePickerDialog(view);
                    }
                });

                // when apply filter button is clicked
                bottomSheetView.findViewById(R.id.userLogs_applyFilter_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "Filters applied!", Toast.LENGTH_SHORT).show();

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
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });



        // Refresh
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mFirestoreList.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private Date formateTodayDate(){
        Date date = new Date();


        return date;
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
        String output = null;

        String myDate2 = selectedDate + " " + selectedEndTime; // -> eg. 2022/04/14 12:00 AM

        //Format of the date defined in the input String
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm aa");

        //Desired format: 24 hour format: Change the pattern as per the need
        DateFormat outputformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


        try {
            //Converting the input String to Date
            endDateTime = df.parse(myDate2);
            //Changing the format of date and storing it in String
            output = outputformat.format(endDateTime);
            Log.d("TEST DATE OUTPUT", output);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return endDateTime;
    }

    /* ================================== FIRESTORE FUNCTIONS ================================== */
    // FirestoreRecyclerView onclick to log details
    @Override
    public void onItemClick(UserLogsModel snapshot, int position) {
        Log.d("ITEM_CLICK", "Clicked the item: " + position + " and the ID is: " + snapshot.getDocument_id());
        //startActivity(new Intent(getActivity(), LogsDetailActivity.class));

        Fragment fragment = UserLogDetailsFragment.newInstance(snapshot.getDocument_id());
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, "user_log_details_fragment");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // display ALL the logs from firestore
    private void firestoreQueryAllLogs(View view){
        // Query from firebase
        Query query = firebaseFirestore.collection("UOW_log") // TODO: filter by organisation ID
                .orderBy("date", Query.Direction.DESCENDING);


        // FirebaseRecyclerOptions
        options = new FirestoreRecyclerOptions.Builder<UserLogsModel>()
                .setLifecycleOwner(this)
                .setQuery(query, new SnapshotParser<UserLogsModel>() {
                    @NonNull
                    @Override
                    public UserLogsModel parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        UserLogsModel userLogsModel = snapshot.toObject(UserLogsModel.class);
                        String docId = snapshot.getId();
                        userLogsModel.setDocument_id(docId);
                        return userLogsModel;
                    }
                })
                .build();


        // Set recyclerView adapter
        adapter = new UserLogsAdapter(options, this);
        mFirestoreList = view.findViewById(R.id.firestore_list2);
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        adapter.notifyItemChanged(0);
        mFirestoreList.setAdapter(adapter);
    }

    // display today (default) logs from firestore
    private void firestoreDefaultLogsQuery(View view){
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
        Query query = firebaseFirestore.collection("UOW_log") // TODO: filter by organisation ID
                .orderBy("date", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThan("date", endDate)
                .limit(20);


        // FirebaseRecyclerOptions
        options = new FirestoreRecyclerOptions.Builder<UserLogsModel>()
                .setLifecycleOwner(this)
                .setQuery(query, new SnapshotParser<UserLogsModel>() {
                    @NonNull
                    @Override
                    public UserLogsModel parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        UserLogsModel userLogsModel = snapshot.toObject(UserLogsModel.class);
                        String docId = snapshot.getId();
                        userLogsModel.setDocument_id(docId);
                        return userLogsModel;
                    }
                })
                .build();
    }

    // display CHOOSEN DATE & TIME logs from firestore
    private void firestoreQueryLogsByDateTime(Date startDateTime, Date endDateTime){
        // Query from firebase
        Query query = firebaseFirestore.collection("UOW_log") // TODO: filter by organisation ID
                .orderBy("date", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("date", startDateTime)
                .whereLessThanOrEqualTo("date", endDateTime);
                //.limit(30);

        // FirebaseRecyclerOptions
        FirestoreRecyclerOptions<UserLogsModel> new_options = new FirestoreRecyclerOptions.Builder<UserLogsModel>()
                .setLifecycleOwner(this)
                .setQuery(query, new SnapshotParser<UserLogsModel>() {
                    @NonNull
                    @Override
                    public UserLogsModel parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        UserLogsModel userLogsModel = snapshot.toObject(UserLogsModel.class);
                        String docId = snapshot.getId();
                        userLogsModel.setDocument_id(docId);
                        return userLogsModel;
                    }
                })
                .build();


        // update recycler adapter
        adapter.updateOptions(new_options);
        mFirestoreList.setAdapter(adapter);
    }

    // old
    private void firestoreQueryLogsByDateTime2(View view,  String chosenDate, String choseDate2){
        Date startDate = new Date();
        Date endDate = new Date();

        try {
            String myDate1 = chosenDate;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            startDate = sdf.parse(myDate1);

            String myDate2 = choseDate2;
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
            endDate = sdf2.parse(myDate2);


        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        // Query from firebase
        Query query = firebaseFirestore.collection("UOW_log") // TODO: filter by organisation ID
                .orderBy("date", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThan("date", endDate)
                .limit(30);


        // FirebaseRecyclerOptions
        FirestoreRecyclerOptions<UserLogsModel> new_options = new FirestoreRecyclerOptions.Builder<UserLogsModel>()
                .setLifecycleOwner(this)
                .setQuery(query, new SnapshotParser<UserLogsModel>() {
                    @NonNull
                    @Override
                    public UserLogsModel parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        UserLogsModel userLogsModel = snapshot.toObject(UserLogsModel.class);
                        String docId = snapshot.getId();
                        userLogsModel.setDocument_id(docId);
                        return userLogsModel;
                    }
                })
                .build();


        // update recycler adapter
        adapter.updateOptions(new_options);
        mFirestoreList.setAdapter(adapter);
    }


    /* ================================== LAYOUT DISPLAY TOGGLE FUNCTIONS ================================== */
    public void showDatePickerDialog(View view){
        final Calendar newCalendar = Calendar.getInstance();
        final DatePickerDialog  datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,
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

    public static void disableProgressBar(){
        progressBar.setVisibility(View.INVISIBLE);
        loadingText.setVisibility(View.INVISIBLE);
    }

    public static void enableProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
        loadingText.setVisibility(View.VISIBLE);
    }

    public static void showDatePickerButton(){
        datePickerbtn.setVisibility(View.VISIBLE);
        dateText.setVisibility(View.VISIBLE);
    }

    public static void showNoDataFoundText(){
        noDataFoundText.setText("There are no logs found in the database on this date.");
    }

    public static void hideNoDateFoundText(){
        noDataFoundText.setText("");
    }

    @Override
    public void onStart() {
        super.onStart();

        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}