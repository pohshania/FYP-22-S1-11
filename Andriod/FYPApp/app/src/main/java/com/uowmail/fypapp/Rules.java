package com.uowmail.fypapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

public class Rules {

    private FirebaseFirestore db;

    public void Rules()
    {
        db = FirebaseFirestore.getInstance();
    }


    public String[] getRulesList()
    {

        DocumentReference docRef = db.collection("UOW_detection").document("rules");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Log.d("RULES", "Rules data: " + doc.getData());
                        // System.out.println(doc.getData());
                    }
                    else {
                        Log.d("RULES", "No such document");
                    }
                }
                else {
                    Log.d("RULES", "get failed with ", task.getException());
                }
            }
        });

        String[] a = {"123"};
        return a;
    }

}
