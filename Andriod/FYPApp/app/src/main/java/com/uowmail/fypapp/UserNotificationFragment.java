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

public class UserNotificationFragment extends Fragment implements UserNotificationsAdapter.OnListItemClick{
    private RecyclerView mFirestoreList;
    private FirebaseFirestore firebaseFirestore;
    private UserNotificationsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirestoreRecyclerOptions<UserNotificationModel> options;
    public static ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_user_notification, container, false);

        TextView title = (TextView)getActivity().findViewById(R.id.toolbar_title);
        title.setText("Notification");


        // Firestore
        firebaseFirestore = FirebaseFirestore.getInstance();

        // XML variables
        progressBar = (ProgressBar) view.findViewById(R.id.userNotifications_progress_bar);


        // Query from firebase
        Query query = firebaseFirestore.collection("UOW_detection")
                .orderBy("date", Query.Direction.DESCENDING);

        // FirebaseRecyclerOptions
        options = new FirestoreRecyclerOptions.Builder<UserNotificationModel>()
                .setLifecycleOwner(this)
                .setQuery(query, new SnapshotParser<UserNotificationModel>() {
                    @NonNull
                    @Override
                    public UserNotificationModel parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        UserNotificationModel userNotificationModel = snapshot.toObject(UserNotificationModel.class);
                        String docId = snapshot.getId();
                        userNotificationModel.setDocument_id(docId);
                        return userNotificationModel;
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
        //loadingText.setVisibility(View.INVISIBLE);
    }

    // FirestoreRecyclerView onclick to log details
    @Override
    public void onItemClick(UserNotificationModel snapshot, int position) {
        Log.d("ITEM_CLICK", "Clicked the item: " + position + " and the ID is: " + snapshot.getDocument_id());
        //startActivity(new Intent(getActivity(), LogsDetailActivity.class));

/*        Fragment fragment = UserLogDetailsFragment.newInstance(snapshot.getDocument_id());
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, "user_log_details_fragment");
        transaction.addToBackStack(null);
        transaction.commit();*/
    }
}