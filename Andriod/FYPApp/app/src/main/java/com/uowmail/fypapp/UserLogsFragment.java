package com.uowmail.fypapp;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class UserLogsFragment extends Fragment implements UserLogsAdapter.OnListItemClick, DatePickerDialog.OnDateSetListener{
    
    private RecyclerView mFirestoreList;
    private FirebaseFirestore firebaseFirestore;
    private UserLogsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirestoreRecyclerOptions<UserLogsModel> options;
    public static ProgressBar progressBar;
    public static TextView loadingText;
    public static Button datePickerbtn;
    public static TextView dateText;
    private static String chosenDate1, chosenDate2;
    public static TextView noDataFoundText;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_logs2, container, false);

        // MJ - set title of the page
        TextView title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        title.setText("Logs");

        firebaseFirestore = FirebaseFirestore.getInstance();

        progressBar = (ProgressBar) view.findViewById(R.id.userLogs_progress_bar);
        loadingText = view.findViewById(R.id.userLogs_loadingText);
        datePickerbtn = (Button) view.findViewById(R.id.userLogs_datePicker);
        dateText = view.findViewById(R.id.userLogs_dateText);
        noDataFoundText = view.findViewById(R.id.userLogs_noDataText);


        //queryForAllLogs(view);
        //queryByDate(view, "2022/04/10", "2022/04/11");

        Date startDate = new Date();
        Date endDate = new Date();
        try {

            String myDate1 = "2022/04/10";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            startDate = sdf.parse(myDate1);

            String myDate2 = "2022/04/11";
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


        // Set recyclerView adapter
        adapter = new UserLogsAdapter(options, this);
        mFirestoreList = (RecyclerView) view.findViewById(R.id.firestore_list2);
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        adapter.notifyItemChanged(0);
        mFirestoreList.setAdapter(adapter);


        // date picker
        datePickerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showDatePickerDialog();

                //mFirestoreList.setAdapter(null);

                final Calendar newCalendar = Calendar.getInstance();
                final DatePickerDialog  datePickerDialog = new DatePickerDialog(datePickerbtn.getContext(), new DatePickerDialog.OnDateSetListener() {
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

                        queryByDate(view, chosenDate1, chosenDate2);

                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();

                //queryByDate(view, chosenDate1, chosenDate2);
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

    public void showDatePickerDialog(){
        final Calendar newCalendar = Calendar.getInstance();
        final DatePickerDialog  datePickerDialog = new DatePickerDialog(datePickerbtn.getContext(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                String pattern = "yyyy/MM/dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                dateText.setText(simpleDateFormat.format(newDate.getTime()));

                String choosenDate1 = simpleDateFormat.format(newDate.getTime());
                String choosenDate2 = year + "/" + monthOfYear + "/" + dayOfMonth+1;
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // display ALL the logs from firestore
    private void queryForAllLogs(View view){
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

    // display (default) TODAY logs from firestore
    private void queryForDefaultTodayLogs(View view){
        Date startDate = new Date();
        Date endDate = new Date();
        try {

            String myDate1 = "2022/04/10";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            startDate = sdf.parse(myDate1);

            String myDate2 = "2022/04/11";
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

    // display CHOOSEN DATE logs from firestore
    private void queryByDate(View view, String chosenDate, String choseDate2){

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

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

    }
}