package com.uowmail.fypapp;

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


public class UserLogsFragment extends Fragment implements UserLogsAdapter.OnListItemClick{
    
    private RecyclerView mFirestoreList;
    private FirebaseFirestore firebaseFirestore;
    private UserLogsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirestoreRecyclerOptions<UserLogsModel> options;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_logs2, container, false);

        // MJ - set title of the page
        TextView title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        title.setText("Logs");


        firebaseFirestore = FirebaseFirestore.getInstance();


        // Query from firebase
        Query query = firebaseFirestore.collection("UOW_log") // TODO: filter by organisation ID
                .orderBy("date", Query.Direction.DESCENDING);


        // Recycler options
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



        // Create recycler adapter
        adapter = new UserLogsAdapter(options, this);
        mFirestoreList = view.findViewById(R.id.firestore_list2);
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        adapter.notifyItemChanged(0);
        mFirestoreList.setAdapter(adapter);


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



        //transaction.hide(getActivity().getSupportFragmentManager().findFragmentByTag("user_logs_fragment"));
        transaction.replace(R.id.container, fragment, "user_log_details_fragment");
        //transaction.add(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();



        /*
        transaction.replace(android.R.id.content, fragment);
        transaction.addToBackStack(null);//add the transaction to the back stack so the user can navigate back
        // Commit the transaction
        transaction.commit();
        */



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