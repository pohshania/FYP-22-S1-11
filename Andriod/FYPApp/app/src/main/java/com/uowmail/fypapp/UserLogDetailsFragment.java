package com.uowmail.fypapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserLogDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserLogDetailsFragment extends Fragment {

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserLogDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment UserLogDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserLogDetailsFragment newInstance(String param1, String param2) {
        UserLogDetailsFragment fragment = new UserLogDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_log_details, container, false);

        // MJ - set title of the page
        TextView title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        title.setText("Log Details");

        TextView titleTV = view.findViewById(R.id.userLogDetails_Title);
        titleTV.setText(mParam1);


        date       = view.findViewById(R.id.userLogDetails_date);
        disk_read  = view.findViewById(R.id.userLogDetails_diskRead);
        disk_write = view.findViewById(R.id.userLogDetails_diskWrite);
        idling     = view.findViewById(R.id.userLogDetails_idling);
        idling_avg = view.findViewById(R.id.userLogDetails_idling_avg);
        idling_min = view.findViewById(R.id.userLogDetails_idling_min);
        idling_max = view.findViewById(R.id.userLogDetails_idling_max);
        cpu_usage  = view.findViewById(R.id.userLogDetails_cpu_usage);
        net_recv   = view.findViewById(R.id.userLogDetails_netRecv);
        net_send   = view.findViewById(R.id.userLogDetails_netSend);
        sys        = view.findViewById(R.id.userLogDetails_sys);
        sys_avg    = view.findViewById(R.id.userLogDetails_sys_avg);
        sys_min    = view.findViewById(R.id.userLogDetails_sys_min);
        sys_max    = view.findViewById(R.id.userLogDetails_sys_max);
        usr        = view.findViewById(R.id.userLogDetails_usr);
        usr_avg    = view.findViewById(R.id.userLogDetails_usr_avg);
        usr_min    = view.findViewById(R.id.userLogDetails_usr_min);
        usr_max    = view.findViewById(R.id.userLogDetails_usr_max);


        // query
        db = FirebaseFirestore.getInstance();
        String path = mParam2 + "_log";
        DocumentReference docRef = db.collection(path).document(mParam1);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserLogDetailsModel logDetails = documentSnapshot.toObject(UserLogDetailsModel.class);

                // date
                //date.setText("Date: " + logDetails.getDate().toDate());
                date.append(getColoredString(getContext(), logDetails.getDate().toDate().toString(), Color.parseColor("#883000")));

                // disk
                //disk_read.setText("Disk Read: " + logDetails.getDisk_read());
                disk_read.append(getColoredString(getContext(), logDetails.getDisk_read(), Color.parseColor("#883000")));
                //disk_write.setText("Disk Write: " + logDetails.getDisk_write());
                disk_write.append(getColoredString(getContext(), logDetails.getDisk_write(), Color.parseColor("#883000")));

                // idling
                //idling.setText("Idling:");
                //idling_avg.setText("Avg: " + logDetails.getIdling().get("avg"));
                idling_avg.append(getColoredString(getContext(), logDetails.getIdling().get("avg").toString(), Color.parseColor("#883000")));
                //idling_min.setText("Min: " + logDetails.getIdling().get("min"));
                idling_min.append(getColoredString(getContext(), logDetails.getIdling().get("min").toString(), Color.parseColor("#883000")));
                //idling_max.setText("Max: " + logDetails.getIdling().get("max"));
                idling_max.append(getColoredString(getContext(), logDetails.getIdling().get("max").toString(), Color.parseColor("#883000")));

                // cpu
                float usage = 100 - logDetails.getIdling().get("avg");
                //cpu_usage.setText("CPU usage: " + String.valueOf(usage));
                cpu_usage.append(getColoredString(getContext(), String.valueOf(usage), Color.parseColor("#883000")));

                // net
                //net_recv.setText("Network recieve: " + logDetails.getNet_recv());
                net_recv.append(getColoredString(getContext(), logDetails.getNet_recv(), Color.parseColor("#883000")));
                //net_send.setText("Network send: " + logDetails.getNet_send());
                net_send.append(getColoredString(getContext(), logDetails.getNet_send(), Color.parseColor("#883000")));


                // sys
                //sys.setText("System:");
                //sys_avg.setText("Avg: " + logDetails.getSys().get("avg"));
                sys_avg.append(getColoredString(getContext(), logDetails.getSys().get("avg").toString(), Color.parseColor("#883000")));
                //sys_min.setText("Min: " + logDetails.getSys().get("min"));
                sys_min.append(getColoredString(getContext(), logDetails.getSys().get("min").toString(), Color.parseColor("#883000")));
                //sys_max.setText("Max: " + logDetails.getSys().get("max"));
                sys_max.append(getColoredString(getContext(), logDetails.getSys().get("max").toString(), Color.parseColor("#883000")));


                // usr
                //usr.setText("User:");
                //usr_avg.setText("Avg: " + logDetails.getUsr().get("avg"));
                usr_avg.append(getColoredString(getContext(), logDetails.getUsr().get("avg").toString(), Color.parseColor("#883000")));
                //usr_min.setText("Min: " + logDetails.getUsr().get("min"));
                usr_min.append(getColoredString(getContext(), logDetails.getUsr().get("min").toString(), Color.parseColor("#883000")));
                //usr_max.setText("Max: " + logDetails.getUsr().get("max"));
                usr_max.append(getColoredString(getContext(), logDetails.getUsr().get("max").toString(), Color.parseColor("#883000")));
            }
        });

        // download
        download = view.findViewById(R.id.userLogsDetails_download);
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

        return view;
    }

    private void downloadFile(String date, String disk_read, String disk_write,
                              String idling, String net_recv, String net_send,
                              String sys, String usr){

        // Get the input from EditText
       String filepath = "MyFileDir";
       String filename = mParam1 + "_log_details";

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
            File myExternalFile = new File(getContext().getExternalFilesDir(filepath), filename);
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
                // net receive
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
            Toast.makeText(getContext(), "Information saved to " + getContext().getExternalFilesDir(filepath), Toast.LENGTH_SHORT).show();
        }

    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //Permission is granted
                return true;
            } else {
                //Permission is revoked
                ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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

    public static final Spannable getColoredString(Context context, CharSequence text, int color) {
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(color), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }
}