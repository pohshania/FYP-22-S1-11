package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UserSettingsRulesActivity extends AppCompatActivity {

    // read rules from the firebase and print on this page
    FirebaseFirestore db;
    String rule;
    String cpu_max, net_send, net_recv, disk_read, disk_write, cpu, netR, netS, diskR, diskW;
    // todo - trying to add org id dynamically
    LinearLayout cpu_ll, net_ll, disk_ll;


    private String orgID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings_rules);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("User Rules");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();


        // pass current user's info
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            orgID = extras.getString("adminOrgID");
            Log.d("====CURRENT ADMIN'S ORG ID====", orgID);
        }

        // get rules from the database display them.
        getRulesList();


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
//    }
    }

    public void getRulesList()
    {
//        String path = currentUserInfo.getOrgID() + "_detection";
        String path = orgID + "_detection";

        DocumentReference docRef = db.collection(path).document("rules");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        rule= String.valueOf(doc.getData());
                        Log.d("RULES", "Rules data: " + rule);
                    }
                    else {
                        Log.d("RULES", "No such document");
                    }
                }
                else {
                    Log.d("RULES", "get failed with ", task.getException());
                }
                // todo separate 'rule' variable to each data type variable
                cpu = rule.substring(rule.indexOf("CPU=") + 5);
                cpu = cpu.substring(0, cpu.indexOf("}"));
                cpu_max = cpu.substring(cpu.indexOf("maximum=")+8, cpu.indexOf(","));
                cpu = cpu.substring(cpu.indexOf("enabled=")+8);

                netR = rule.substring(rule.indexOf("net_recv=") + 10);
                netR = netR.substring(0, netR.indexOf("}"));
                net_recv = netR.substring(netR.indexOf("value=")+6, netR.indexOf(","));
                netR = netR.substring(netR.indexOf("enabled=")+8);


                netS = rule.substring(rule.indexOf("net_send=") + 10);
                netS = netS.substring(0, netS.indexOf("}"));
                net_send = netS.substring(netS.indexOf("value=")+6, netS.indexOf(","));
                netS = netS.substring(netS.indexOf("enabled=")+8);

                diskR = rule.substring(rule.indexOf("disk_read=") + 11);
                diskR = diskR.substring(0, diskR.indexOf("}"));
                disk_read = diskR.substring(diskR.indexOf("value=")+6, diskR.indexOf(","));
                diskR = diskR.substring(diskR.indexOf("enabled=")+8);


                diskW = rule.substring(rule.indexOf("disk_writ=") + 11);
                diskW = diskW.substring(0, diskW.indexOf("}"));
                disk_write = diskW.substring(diskW.indexOf("value=")+6, diskW.indexOf(","));
                diskW = diskW.substring(diskW.indexOf("enabled=")+8);

//                System.out.print("\n data value:\n"+cpu_max + " ~ "+ net_recv+ " ~ "+ net_send + " ~ "+ disk_read + " ~ "+ disk_write + "\n");

                // TODO - linear layout for each data type
                cpu_ll  = (LinearLayout) findViewById(R.id.cpu_layout);
                net_ll  = (LinearLayout) findViewById(R.id.network_layout);
                disk_ll = (LinearLayout) findViewById(R.id.disk_layout);

                // todo - call function that create text view dynamically
                displayRule(cpu_ll, "CPU Maxium",     cpu_max,    cpu);
                displayRule(net_ll, "Network Send",   net_recv+"M",   netR);
                displayRule(net_ll, "Network Recieve",net_send+"M",   netS);
                displayRule(disk_ll,"Disk Read",      disk_read+"M",  diskR);
                displayRule(disk_ll,"Disk Write",     disk_write+"M", diskW);
            }
        });
    }

    public void displayRule(LinearLayout ll, String title,  String value, String enabled){
        LinearLayout newLL = new LinearLayout(this);
        newLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        newLL.setOrientation(LinearLayout.HORIZONTAL);
        newLL.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 100);
        lp.weight =1;

        // Text view
        TextView textView1 = new TextView(this);
        textView1.setLayoutParams(lp);
        TextView textView2 = new TextView(this);
        textView2.setLayoutParams(lp);

        textView1.setText(title);
        textView1.setPadding(0, 10, 10, 10);// in pixels (left, top, right, bottom)
        textView1.setTextSize(20);
        textView1.setTextColor(Color.BLACK);
        textView2.setText(" : "+ value);
        textView2.setPadding(0, 10, 0, 10);// in pixels (left, top, right, bottom)
        textView2.setTextSize(20);
        textView2.setTextColor(Color.BLACK);

        // Check Box
        CheckBox checkBox = new CheckBox(this);
        checkBox.setLayoutParams(lp);

        checkBox.setText("Enabled");
        checkBox.setTextColor(Color.BLACK);
        checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#009900")));
        checkBox.setPadding(0, 10, 0, 30);// in pixels (left, top, right, bottom)
        checkBox.setChecked(true);
        checkBox.setEnabled(false);

        // if the data is disabled
        if(enabled.trim().contains("false"))
        {
            textView1.setTextColor(Color.GRAY);
            textView2.setTextColor(Color.GRAY);
            checkBox.setText("Disabled");
            checkBox.setChecked(true);
            checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#9d7b7b")));
            checkBox.setTextColor(Color.GRAY);
            ll.setBackgroundColor(Color.parseColor("#cdcdcd"));
        }
        ll.addView(newLL);
        newLL.addView(textView1);
        newLL.addView(textView2);
        newLL.addView(checkBox);

    }

    // MJ - enable back button in ActionBar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
