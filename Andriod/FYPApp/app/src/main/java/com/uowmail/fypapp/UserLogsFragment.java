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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class UserLogsFragment extends Fragment implements FirestoreAdapter.OnListItemClick{
    
    private RecyclerView mFirestoreList;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreAdapter adapter;

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_logs, container, false);

        // MJ - set title of the page
        TextView title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        title.setText("Logs");


        firebaseFirestore = FirebaseFirestore.getInstance();
        mFirestoreList = view.findViewById(R.id.firestore_list); // this is for the recyclerview



        // Query from firebase
        Query query = firebaseFirestore.collection("Products");


        // Recycler options
        FirestoreRecyclerOptions<ProductsModel> options = new FirestoreRecyclerOptions.Builder<ProductsModel>()
                .setLifecycleOwner(this)
                .setQuery(query, new SnapshotParser<ProductsModel>() {
                    @NonNull
                    @Override
                    public ProductsModel parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        ProductsModel productsModel = snapshot.toObject(ProductsModel.class);
                        String itemId = snapshot.getId();
                        productsModel.setItem_id(itemId);
                        return productsModel;
                    }
                })
                .build();


        // Create recycler adapter
        adapter = new FirestoreAdapter(options, this);

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
    public void onItemClick(ProductsModel snapshot, int position) {
        Log.d("ITEM_CLICK", "Clicked the item: " + position + " and the ID is: " + snapshot.getItem_id());
        startActivity(new Intent(getActivity(), LogsDetailActivity.class));
    }
}