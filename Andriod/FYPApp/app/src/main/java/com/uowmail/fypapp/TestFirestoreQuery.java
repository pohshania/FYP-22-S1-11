package com.uowmail.fypapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TestFirestoreQuery {

    private FirebaseFirestore fStore;

    public TestFirestoreQuery() {
    }

    public  void query(){


        //Date currentTime = Calendar.getInstance().getTime();
        //Log.d("=====FIRESTORE_DATE=====", currentTime.toString());


        fStore = FirebaseFirestore.getInstance();

        Date startDate = new Date();
        Date endDate = new Date();
        try {
            /*
            String myDate = "2022/04/10 00:00:00";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            date = sdf.parse(myDate);
            */

            /*
            LocalDateTime ldt = LocalDateTime.now().plusDays(1);
            DateTimeFormatter formmat1 = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
            Log.d("=====FIRESTORE_DATE=====", ldt.toString());
            // Output "2018-05-12T17:21:53.658"

            String formatter = formmat1.format(ldt);
            Log.d("=====FIRESTORE_DATE=====", formatter);
            // 2018-05-12
             */


/*            // TODAY - 2022/04/12
            LocalDateTime ldt_today = LocalDateTime.now();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            String str_today = format.format(ldt_today);
            Log.d("=====FIRESTORE_DATE_TODAY=====", str_today);


            // TOMORROW - 2022/04/13
            LocalDateTime ldt_tmr = LocalDateTime.now().plusDays(1);
            DateTimeFormatter format2 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            String str_tmr = format2.format(ldt_tmr);
            Log.d("=====FIRESTORE_DATE_TOMORROW=====", str_tmr);

            String myDate1 = str_today;
            Log.d("=====FIRESTORE_DATE_myDate1=====", myDate1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            startDate = sdf.parse(myDate1);

            String myDate2 = "2022/04/12";
            Log.d("=====FIRESTORE_DATE_myDate1=====", myDate2);
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
            endDate = sdf2.parse(str_tmr);*/




            String myDate1 = "2022/04/11 01:00:00";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            startDate = sdf.parse(myDate1);

            String myDate2 = "2022/04/12";
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
            endDate = sdf2.parse(myDate2);







        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //Timestamp ts = new Timestamp(date);


        /*
        fStore.collection("test_queries")
                //.whereEqualTo("date", ts)
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThan("date", endDate)
                .limit(10)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("=====FIRESTORE_QUERR=====", document.getId() + " => " + document.getData());
                            }

                        }else{
                            Log.d("=====FIRESTORE_QUERR=====", "Error getting documents: ", task.getException());
                        }
                    }
                });
         */

        // add();
        update();


    }

    public void add()
    {
        Map<String, Object> baseline_test = new HashMap<>();
        Map<String, Object> weight1 = new HashMap<>();
        weight1.put("weight", 1);
        baseline_test.put("usr", weight1);

        fStore.collection("UOW_detection").document("baseline_test")
                .set(baseline_test)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("=====FIRESTORE_QUERR=====", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("=====FIRESTORE_QUERR=====", "Error writing document", e);
                    }
                });
    }

    public void update()
    {
        Map<String, Object> weight1 = new HashMap<>();
        weight1.put("weight", 4);
        fStore.collection("UOW_detection").document("baseline_test")
                .update(
                        "usr", weight1
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("=====FIRESTORE_QUERR=====", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("=====FIRESTORE_QUERR=====", "Error updating document", e);
                    }
                });
    }

}
