package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UserSettingsRulesActivity extends AppCompatActivity {

    // read rules from the firebase and print on this page
    FirebaseFirestore db;
    String oneLine;
    String cpu, net_send, net_recv, disk_read, disk_write;
    private TextView tv_cpu, tv_nets, tv_netr, tv_diskr, tv_diskw, tv_net, tv_disk;
    // todo - try to add text view dynamically
//    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_example);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings_rules);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("User Rules");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        getRulesList();

        tv_cpu       = findViewById(R.id.rule_cpu);
        tv_net       = findViewById(R.id.rule_network);
        tv_disk      = findViewById(R.id.rule_disk);
//        tv_nets      = findViewById(R.id.rule_network);
//        tv_netr      = findViewById(R.id.rule_network);
//        tv_diskr     = findViewById(R.id.rule_disk);
//        tv_diskw     = findViewById(R.id.rule_disk);



//        //Getting the instance of Spinner and applying OnItemSelectedListener on it
//        Spinner spin = (Spinner) findViewById(R.id.spinner);
//        spin.setOnItemSelectedListener(this);
//
//        //Creating the ArrayAdapter instance having the country list
//        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,network);
//        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        //Setting the ArrayAdapter data on the Spinner
//        spin.setAdapter(aa);
//    }
//    //Performing action onItemSelected and onNothing selected
//    @Override
//    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
//        Toast.makeText(getApplicationContext(),network[position] , Toast.LENGTH_LONG).show();
//    }
//    @Override
//    public void onNothingSelected(AdapterView<?> arg0) {
//        // TODO Auto-generated method stub
//    }
//    // MJ - enable back button in ActionBar
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                this.finish();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
    }

    public void getRulesList()
    {
        DocumentReference docRef = db.collection("UOW_detection").document("rules");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        oneLine= String.valueOf(doc.getData());
                        Log.d("RULES", "Rules data: " + oneLine);
                    }
                    else {
                        Log.d("RULES", "No such document");
                    }
                }
                else {
                    Log.d("RULES", "get failed with ", task.getException());
                }
                // todo oneLine data to each data type
                cpu = oneLine.substring(oneLine.indexOf("CPU=") + 5 , oneLine.length());
                cpu = cpu.substring(0, cpu.indexOf("}"));

                net_recv = oneLine.substring(oneLine.indexOf("net_recv=") + 10 , oneLine.length());
                net_recv = net_recv.substring(0, net_recv.indexOf("}"));

                net_send = oneLine.substring(oneLine.indexOf("net_send=") + 10 , oneLine.length());
                net_send = net_send.substring(0, net_send.indexOf("}"));

                disk_read = oneLine.substring(oneLine.indexOf("disk_read=") + 11 , oneLine.length());
                disk_read = disk_read.substring(0, disk_read.indexOf("}"));

                disk_write = oneLine.substring(oneLine.indexOf("disk_writ=") + 11 , oneLine.length());
                disk_write = disk_write.substring(0, disk_write.indexOf("}"));

//                System.out.print("\n data value:\n"+cpu + " ~ "+ net_recv+ " ~ "+ net_send + " ~ "+ disk_read + " ~ "+ disk_write + "\n");

                // set each data into relavant textview
                tv_cpu.setText(cpu);
                tv_net.setText("net_recv: " + net_recv + "\nnet_send:" + net_send);
                tv_disk.setText("disk_read: " + disk_read + "\ndisk_write: " +disk_write);

                // todo - call function that create text view dynamically
                displayRule(cpu);
                displayRule(net_recv);
                displayRule(net_send);
                displayRule(disk_read);
                displayRule(disk_write);


            }
        });
    }

    public void displayRule(String value){
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_example);

        TextView textView1 = new TextView(this);
        textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView1.setText(value);
        textView1.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
        textView1.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
        linearLayout.addView(textView1);
    }
}
