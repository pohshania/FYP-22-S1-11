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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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
    public static TextView loadingText;

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

        // Query from firebase
        Query query = firebaseFirestore.collection("UOW_detection")
                .orderBy("date", Query.Direction.DESCENDING);

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

        // Set recyclerView adapter
        adapter = new UserNotificationsAdapter(options, this);
        mFirestoreList = (RecyclerView) view.findViewById(R.id.userNotifications_firestore_list);
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        adapter.notifyItemChanged(0);
        mFirestoreList.setAdapter(adapter);

        return view;
    }

    public static void disableProgressBar(){
        progressBar.setVisibility(View.INVISIBLE);
        loadingText.setVisibility(View.INVISIBLE);
    }

    public static void enableProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
        loadingText.setVisibility(View.VISIBLE);
    }

    // FirestoreRecyclerView onclick to log details
    @Override
    public void onItemClick(UserNotificationsModel snapshot, int position) {
        Log.d("ITEM_CLICK", "Clicked the item: " + position + " and the ID is: " + snapshot.getDocument_id());

        Fragment fragment = UserIntrusionDetectionDetailsFragment.newInstance(snapshot.getDocument_id(), mParam1);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, "user_intrusion_detecion_details_fragment");
        transaction.addToBackStack(null);
        transaction.commit();
    }
}