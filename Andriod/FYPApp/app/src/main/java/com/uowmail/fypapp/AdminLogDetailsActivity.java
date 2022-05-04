package com.uowmail.fypapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AdminLogDetailsActivity extends AppCompatActivity {


    private FirebaseFirestore db;

    private TextView date;
    private TextView disk_read;
    private TextView disk_write;
    private TextView idling, idling_avg, idling_min, idling_max;
    private TextView cpu_usage;
    private TextView net_recv;
    private TextView net_send;
    private TextView sys, sys_avg, sys_min, sys_max;
    private TextView usr, usr_avg, usr_min, usr_max;
    private Button download;

    private String FILE_NAME;
    private FileOutputStream fos;
    private String orgID, docID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_log_details);

        // pass current user's info
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            orgID = extras.getString("adminOrgID");
            docID = extras.getString("docID");
            Log.d("====CURRENT ADMIN'S ORG ID====", orgID);
            Log.d("====CURRENT ADMIN'S DOC ID====", docID);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Logs Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView titleTV = findViewById(R.id.adminLogDetails_Title);
        titleTV.setText(docID);

        date       = findViewById(R.id.adminLogDetails_date);
        disk_read  = findViewById(R.id.adminLogDetails_diskRead);
        disk_write = findViewById(R.id.adminLogDetails_diskWrite);
        idling     = findViewById(R.id.adminLogDetails_idling);
        idling_avg = findViewById(R.id.adminLogDetails_idling_avg);
        idling_min = findViewById(R.id.adminLogDetails_idling_min);
        idling_max = findViewById(R.id.adminLogDetails_idling_max);
        cpu_usage  = findViewById(R.id.adminLogDetails_cpu_usage);
        net_recv   = findViewById(R.id.adminLogDetails_netRecv);
        net_send   = findViewById(R.id.adminLogDetails_netSend);
        sys        = findViewById(R.id.adminLogDetails_sys);
        sys_avg    = findViewById(R.id.adminLogDetails_sys_avg);
        sys_min    = findViewById(R.id.adminLogDetails_sys_min);
        sys_max    = findViewById(R.id.adminLogDetails_sys_max);
        usr        = findViewById(R.id.adminLogDetails_usr);
        usr_avg    = findViewById(R.id.adminLogDetails_usr_avg);
        usr_min    = findViewById(R.id.adminLogDetails_usr_min);
        usr_max    = findViewById(R.id.adminLogDetails_usr_max);


        // query
        db = FirebaseFirestore.getInstance();
        String path = orgID + "_log";
        DocumentReference docRef = db.collection(path).document(docID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                AdminLogDetailsModel logDetails = documentSnapshot.toObject(AdminLogDetailsModel.class);

                // date
                //date.setText("Date: " + logDetails.getDate().toDate());
                date.append(getColoredString(AdminLogDetailsActivity.this, logDetails.getDate().toDate().toString(), Color.parseColor("#883000")));

                // disk
                //disk_read.setText("Disk Read: " + logDetails.getDisk_read());
                disk_read.append(getColoredString(AdminLogDetailsActivity.this, logDetails.getDisk_read(), Color.parseColor("#883000")));
                //disk_write.setText("Disk Write: " + logDetails.getDisk_write());
                disk_write.append(getColoredString(AdminLogDetailsActivity.this, logDetails.getDisk_write(), Color.parseColor("#883000")));

                // idling
                //idling.setText("Idling:");
                //idling_avg.setText("Avg: " + logDetails.getIdling().get("avg"));
                idling_avg.append(getColoredString(AdminLogDetailsActivity.this, logDetails.getIdling().get("avg").toString(), Color.parseColor("#883000")));
                //idling_min.setText("Min: " + logDetails.getIdling().get("min"));
                idling_min.append(getColoredString(AdminLogDetailsActivity.this, logDetails.getIdling().get("min").toString(), Color.parseColor("#883000")));
                //idling_max.setText("Max: " + logDetails.getIdling().get("max"));
                idling_max.append(getColoredString(AdminLogDetailsActivity.this, logDetails.getIdling().get("max").toString(), Color.parseColor("#883000")));

                // cpu
                float usage = 100 - logDetails.getIdling().get("avg");
                //cpu_usage.setText("CPU usage: " + String.valueOf(usage));
                cpu_usage.append(getColoredString(AdminLogDetailsActivity.this, String.valueOf(usage), Color.parseColor("#883000")));

                // net
                //net_recv.setText("Network recieve: " + logDetails.getNet_recv());
                net_recv.append(getColoredString(AdminLogDetailsActivity.this, logDetails.getNet_recv(), Color.parseColor("#883000")));
                //net_send.setText("Network send: " + logDetails.getNet_send());
                net_send.append(getColoredString(AdminLogDetailsActivity.this, logDetails.getNet_send(), Color.parseColor("#883000")));


                // sys
                //sys.setText("System:");
                //sys_avg.setText("Avg: " + logDetails.getSys().get("avg"));
                sys_avg.append(getColoredString(AdminLogDetailsActivity.this, logDetails.getSys().get("avg").toString(), Color.parseColor("#883000")));
                //sys_min.setText("Min: " + logDetails.getSys().get("min"));
                sys_min.append(getColoredString(AdminLogDetailsActivity.this, logDetails.getSys().get("min").toString(), Color.parseColor("#883000")));
                //sys_max.setText("Max: " + logDetails.getSys().get("max"));
                sys_max.append(getColoredString(AdminLogDetailsActivity.this, logDetails.getSys().get("max").toString(), Color.parseColor("#883000")));


                // usr
                //usr.setText("User:");
                //usr_avg.setText("Avg: " + logDetails.getUsr().get("avg"));
                usr_avg.append(getColoredString(AdminLogDetailsActivity.this, logDetails.getUsr().get("avg").toString(), Color.parseColor("#883000")));
                //usr_min.setText("Min: " + logDetails.getUsr().get("min"));
                usr_min.append(getColoredString(AdminLogDetailsActivity.this, logDetails.getUsr().get("min").toString(), Color.parseColor("#883000")));
                //usr_max.setText("Max: " + logDetails.getUsr().get("max"));
                usr_max.append(getColoredString(AdminLogDetailsActivity.this, logDetails.getUsr().get("max").toString(), Color.parseColor("#883000")));
            }
        });

        // download
        download = findViewById(R.id.adminLogDetails_download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile(date.getText().toString(), disk_read.getText().toString(), disk_write.getText().toString(),
                        idling.getText().toString() + " " + idling_avg.getText().toString() + " " + idling_min.getText().toString() + " " + idling_max.getText().toString(),
                        net_recv.getText().toString(), net_send.getText().toString(),
                        sys.getText().toString() + " " + sys_avg.getText().toString() + " " + sys_min.getText().toString() + " " + sys_max.getText().toString(),
                        usr.getText().toString() + " " + usr_avg.getText().toString() + " " + usr_min.getText().toString() + " " + usr_max.getText().toString());
            }
        });
    }

    private void downloadFile(String date, String disk_read, String disk_write,
                              String idling, String net_recv, String net_send,
                              String sys, String usr){

        // Get the input from EditText
        String filepath = "MyFileDir";
        String filename = docID + "_log_details";

        // Check for Storage Permission
        if(isStoragePermissionGranted()){

            // To access app-specific files from external storage, you can call
            // getExternalFilesDir() method. It returns the path to
            // storage > emulated > 0 > Android > data > [package_name] > files > MyFileDir
            // or,
            // storage > self > Android > data > [package_name] > files > MyFileDir
            // directory on the SD card. Once the app is uninstalled files here also get
            // deleted.
            // Create a File object like this.
            File myExternalFile = new File(this.getExternalFilesDir(filepath), filename);
            // Create an object of FileOutputStream for writing data to myFile.txt
            FileOutputStream fos = null;
            try {
                // Instantiate the FileOutputStream object and pass myExternalFile in constructor
                fos = new FileOutputStream(myExternalFile);


                // Write to the file
                String nl = "\n";

                // date
                fos.write(date.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));

                // disk read
                fos.write(disk_read.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));

                // disk write
                fos.write(disk_write.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));

                // idling
                fos.write(idling.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));

                // net recv
                fos.write(net_recv.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));

                // net send
                fos.write(net_send.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));

                // sys
                fos.write(sys.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));

                // usr
                fos.write(usr.getBytes(StandardCharsets.UTF_8));


                // Close the stream
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Show a Toast message to inform the user that the operation has been successfully completed.
            Toast.makeText(this, "Information saved to " + this.getExternalFilesDir(filepath), Toast.LENGTH_SHORT).show();
        }

    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //Permission is granted
                return true;
            } else {
                //Permission is revoked
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            //permission is automatically granted on sdk<23 upon installation
            //Permission is granted
            return true;
        }
    }

    private boolean isExternalStorageAvailableForRW() {
        // Check if the external storage is available for read and write by calling
        // Environment.getExternalStorageState() method. If the returned state is MEDIA_MOUNTED,
        // then you can read and write files. So, return true in that case, otherwise, false.
        String extStorageState = Environment.getExternalStorageState();
        if(extStorageState.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        return false;
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

    public static final Spannable getColoredString(Context context, CharSequence text, int color) {
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(color), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

}