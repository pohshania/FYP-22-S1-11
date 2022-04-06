package com.uowmail.fypapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class UserLogsFragment extends Fragment implements UserLogsAdapter.OnListItemClick{
    
    private RecyclerView mFirestoreList;
    private FirebaseFirestore firebaseFirestore;
    private UserLogsAdapter adapter;

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_logs, container, false);

        // MJ - set title of the page
        TextView title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        title.setText("Logs");


        firebaseFirestore = FirebaseFirestore.getInstance();
        mFirestoreList = view.findViewById(R.id.firestore_list); // this is for the recyclerview


        // testing query





        // Query from firebase
        Query query = firebaseFirestore.collection("UOW_log");


        // Recycler options
        FirestoreRecyclerOptions<UserLogsModel> options = new FirestoreRecyclerOptions.Builder<UserLogsModel>()
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


        // Create recycler adapter
        adapter = new UserLogsAdapter(options, this);

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(getContext()));
        mFirestoreList.setAdapter(adapter);


        return view;
    }

    /*
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
     */

    @Override
    public void onItemClick(UserLogsModel snapshot, int position) {
        Log.d("ITEM_CLICK", "Clicked the item: " + position + " and the ID is: " + snapshot.getDocument_id());
        startActivity(new Intent(getActivity(), LogsDetailActivity.class));
    }
}