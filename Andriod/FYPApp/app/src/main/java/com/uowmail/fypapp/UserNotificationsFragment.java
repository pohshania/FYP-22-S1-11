package com.uowmail.fypapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserNotificationsFragment extends Fragment implements UserNotificationsAdapter.OnListItemClick{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private RecyclerView mFirestoreList;
    private FirebaseFirestore firebaseFirestore;
    private UserNotificationsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirestoreRecyclerOptions<UserNotificationsModel> options;
    public static ProgressBar progressBar;
    public static TextView loadingText, noDataText;

    private RadioButton allEngines_radio, alertEngine_radio, analysisEngine_radio, signatureEngine_radio;
    private Button applyBtn;

    public UserNotificationsFragment() {
        // Required empty public constructor
    }

    public static UserNotificationsFragment newInstance(String param1) {
        UserNotificationsFragment fragment = new UserNotificationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            Log.d("===== INSIDE NOTIIFICATIONS FRAG ===== ", mParam1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_user_notification, container, false);

        TextView title = (TextView)getActivity().findViewById(R.id.toolbar_title);
        title.setText("Intrusion Notifications");


        // Firestore
        firebaseFirestore = FirebaseFirestore.getInstance();

        // XML variables
        progressBar = (ProgressBar) view.findViewById(R.id.userNotifications_progress_bar);
        loadingText = view.findViewById(R.id.userNotifications_loadingText);
        noDataText = view.findViewById(R.id.userNotifications_noDataText);

        allEngines_radio = view.findViewById(R.id.radio_allEngines);
        alertEngine_radio = view.findViewById(R.id.radio_alertEngine);
        analysisEngine_radio = view.findViewById(R.id.radio_analysisEngine);
        signatureEngine_radio = view.findViewById(R.id.radio_signatureEngine);
        applyBtn = view.findViewById(R.id.userNotifications_apply_btn);

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(allEngines_radio.isChecked()){
                    queryForAllEngines();
                }

                if(alertEngine_radio.isChecked()){
                    queryForAlertEngine();
                }

                if(analysisEngine_radio.isChecked()){
                    queryForAnalysisEngine();
                }

                if(signatureEngine_radio.isChecked()){
                    queryForSignatureEngine();
                }
            }
        });





        defaultQuery();
        // Set recyclerView adapter
        adapter = new UserNotificationsAdapter(options, this);
        mFirestoreList = (RecyclerView) view.findViewById(R.id.userNotifications_firestore_list);
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setItemAnimator(null);
        mFirestoreList.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        adapter.notifyItemChanged(0);
        mFirestoreList.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();
        mFirestoreList.setAdapter(adapter);

        // Refresh
        swipeRefreshLayout = view.findViewById(R.id.userNotifications_swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mFirestoreList.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private void defaultQuery(){
        String path = mParam1 + "_detection";
        // Query from firebase
        Query query = firebaseFirestore.collection(path).orderBy("date", Query.Direction.DESCENDING);

        // FirebaseRecyclerOptions
        options = new FirestoreRecyclerOptions.Builder<UserNotificationsModel>()
                .setLifecycleOwner(this)
                .setQuery(query, new SnapshotParser<UserNotificationsModel>() {
                    @NonNull
                    @Override
                    public UserNotificationsModel parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        UserNotificationsModel userNotificationsModel = snapshot.toObject(UserNotificationsModel.class);
                        String docId = snapshot.getId();
                        userNotificationsModel.setDocument_id(docId);
                        return userNotificationsModel;
                    }
                })
                .build();
    }

    private void queryForAlertEngine(){
        String path = mParam1 + "_detection";

        // Query from firebase
        Query query = firebaseFirestore.collection(path)
                .orderBy("date", Query.Direction.DESCENDING)
                .whereEqualTo("detected_by", "Alert engine");

        // FirebaseRecyclerOptions
        FirestoreRecyclerOptions<UserNotificationsModel> new_options = new FirestoreRecyclerOptions.Builder<UserNotificationsModel>()
                .setLifecycleOwner(this)
                .setQuery(query, new SnapshotParser<UserNotificationsModel>() {
                    @NonNull
                    @Override
                    public UserNotificationsModel parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        UserNotificationsModel userNotificationsModel = snapshot.toObject(UserNotificationsModel.class);
                        String docId = snapshot.getId(); // TODO: different models for each engine
                        userNotificationsModel.setDocument_id(docId);
                        return userNotificationsModel;
                    }
                })
                .build();

        // update recycler adapter
        adapter.updateOptions(new_options);
        mFirestoreList.setAdapter(adapter);
    }

    private void queryForAnalysisEngine(){
        String path = mParam1 + "_detection";

        // Query from firebase
        Query query = firebaseFirestore.collection(path)
                .orderBy("date", Query.Direction.DESCENDING)
                .whereEqualTo("detected_by", "Analysis engine");

        // FirebaseRecyclerOptions
        FirestoreRecyclerOptions<UserNotificationsModel> new_options = new FirestoreRecyclerOptions.Builder<UserNotificationsModel>()
                .setLifecycleOwner(this)
                .setQuery(query, new SnapshotParser<UserNotificationsModel>() {
                    @NonNull
                    @Override
                    public UserNotificationsModel parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        UserNotificationsModel userNotificationsModel = snapshot.toObject(UserNotificationsModel.class);
                        String docId = snapshot.getId(); // TODO: different models for each engine
                        userNotificationsModel.setDocument_id(docId);
                        return userNotificationsModel;
                    }
                })
                .build();

        // update recycler adapter
        adapter.updateOptions(new_options);
        mFirestoreList.setAdapter(adapter);
    }

    private void queryForSignatureEngine(){
        String path = mParam1 + "_detection";

        // Query from firebase
        Query query = firebaseFirestore.collection(path)
                .orderBy("date", Query.Direction.DESCENDING)
                .whereEqualTo("detected_by", "Signature engine");

        // FirebaseRecyclerOptions
        FirestoreRecyclerOptions<UserNotificationsModel> new_options = new FirestoreRecyclerOptions.Builder<UserNotificationsModel>()
                .setLifecycleOwner(this)
                .setQuery(query, new SnapshotParser<UserNotificationsModel>() {
                    @NonNull
                    @Override
                    public UserNotificationsModel parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        UserNotificationsModel userNotificationsModel = snapshot.toObject(UserNotificationsModel.class);
                        String docId = snapshot.getId(); // TODO: different models for each engine
                        userNotificationsModel.setDocument_id(docId);
                        return userNotificationsModel;
                    }
                })
                .build();

        // update recycler adapter
        adapter.updateOptions(new_options);
        mFirestoreList.setAdapter(adapter);
    }

    private void queryForAllEngines(){
        String path = mParam1 + "_detection";

        // Query from firebase
        Query query = firebaseFirestore.collection(path)
                .orderBy("date", Query.Direction.DESCENDING);

        // FirebaseRecyclerOptions
        FirestoreRecyclerOptions<UserNotificationsModel> new_options = new FirestoreRecyclerOptions.Builder<UserNotificationsModel>()
                .setLifecycleOwner(this)
                .setQuery(query, new SnapshotParser<UserNotificationsModel>() {
                    @NonNull
                    @Override
                    public UserNotificationsModel parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        UserNotificationsModel userNotificationsModel = snapshot.toObject(UserNotificationsModel.class);
                        String docId = snapshot.getId(); // TODO: different models for each engine
                        userNotificationsModel.setDocument_id(docId);
                        return userNotificationsModel;
                    }
                })
                .build();

        // update recycler adapter
        adapter.updateOptions(new_options);
        mFirestoreList.setAdapter(adapter);
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
        noDataText.setText("No data found on the database from the engine(s).");
    }

    public static void hideNoDataFoundText(){
        noDataText.setText("");
    }

    // FirestoreRecyclerView onclick to log details
    @Override
    public void onItemClick(UserNotificationsModel snapshot, int position) {
        Log.d("ITEM_CLICK", "Clicked the item: " + position + " and the ID is: " + snapshot.getDocument_id());

        if(snapshot.getDocument_id().contains("_alert")){
            Fragment fragment = UserAlertEngineDetailsFragment.newInstance(snapshot.getDocument_id(), mParam1);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment, "user_alert_engine_details_fragment");
            transaction.addToBackStack(null);
            transaction.commit();
        }

        if(snapshot.getDocument_id().contains("_analysis")){
            Fragment fragment = UserAnalysisEngineDetailsFragment.newInstance(snapshot.getDocument_id(), mParam1);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment, "user_analysis_engine_details_fragment");
            transaction.addToBackStack(null);
            transaction.commit();
        }

        if(snapshot.getDocument_id().contains("_signature")){

        }

//        Fragment fragment = UserIntrusionDetectionDetailsFragment.newInstance(snapshot.getDocument_id(), mParam1);
//        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.container, fragment, "user_intrusion_detecion_details_fragment");
//        transaction.addToBackStack(null);
//        transaction.commit();
    }
}