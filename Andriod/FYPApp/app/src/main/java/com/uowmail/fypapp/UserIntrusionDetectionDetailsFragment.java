package com.uowmail.fypapp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserIntrusionDetectionDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserIntrusionDetectionDetailsFragment extends Fragment {

    private FirebaseFirestore db;

    private TextView abnormal;
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

    public UserIntrusionDetectionDetailsFragment() {
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
    public static UserIntrusionDetectionDetailsFragment newInstance(String param1, String param2) {
        UserIntrusionDetectionDetailsFragment fragment = new UserIntrusionDetectionDetailsFragment();
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
            Log.d("===== INSIDE INTUSION DETAILS FRAG ===== ", mParam2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_intrusion_detection_details, container, false);

        // MJ - set title of the page
        TextView title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        title.setText("Intrusion Details");


        TextView titleTV = view.findViewById(R.id.userIntrusionDetails_Title);
        titleTV.setText(mParam1);


        abnormal   = view.findViewById(R.id.userIntrusionDetails_abnormal);
        date       = view.findViewById(R.id.userIntrusionDetails_date);
        disk_read  = view.findViewById(R.id.userIntrusionDetails_diskRead);
        disk_write = view.findViewById(R.id.userIntrusionDetails_diskWrite);
        idling     = view.findViewById(R.id.userIntrusionDetails_idling);
        idling_avg = view.findViewById(R.id.userIntrusionDetails_idling_avg);
        idling_min = view.findViewById(R.id.userIntrusionDetails_idling_min);
        idling_max = view.findViewById(R.id.userIntrusionDetails_idling_max);
        cpu_usage  = view.findViewById(R.id.userIntrusionDetails_CPU_usage);
        net_recv   = view.findViewById(R.id.userIntrusionDetails_netRecv);
        net_send   = view.findViewById(R.id.userIntrusionDetails_netSend);
        sys        = view.findViewById(R.id.userIntrusionDetails_sys);
        sys_avg    = view.findViewById(R.id.userIntrusionDetails_sys_avg);
        sys_min    = view.findViewById(R.id.userIntrusionDetails_sys_min);
        sys_max    = view.findViewById(R.id.userIntrusionDetails_sys_max);
        usr        = view.findViewById(R.id.userIntrusionDetails_usr);
        usr_avg    = view.findViewById(R.id.userIntrusionDetails_usr_avg);
        usr_min    = view.findViewById(R.id.userIntrusionDetails_usr_min);
        usr_max    = view.findViewById(R.id.userIntrusionDetails_usr_max);




        // query
        String path = mParam2 + "_detection";
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(path).document(mParam1);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserIntrusionDetectionDetailsModel intrusionDetails = documentSnapshot.toObject(UserIntrusionDetectionDetailsModel.class);

                Log.d("TEST_QUERY", intrusionDetails.getAbnormal() + " " + intrusionDetails.getDate() + " " + intrusionDetails.getDisk_read() + " " + intrusionDetails.getDisk_write() + " " +
                        intrusionDetails.getIdling() + " " + intrusionDetails.getNet_recv() + " " + intrusionDetails.getNet_send() + " " + intrusionDetails.getSys() +
                        " " + intrusionDetails.getUsr());

                //Date d = logDetails.getTimestamp().toDate();
                abnormal.setText("Abnormal: " + intrusionDetails.getAbnormal());
                abnormal.setTextColor(Color.parseColor("#FF0000"));
                disk_read.setText("Disk Read: " + intrusionDetails.getDisk_read());
                disk_read.setTextColor(Color.parseColor("#FF0000"));
                disk_write.setText("Disk Write: " + intrusionDetails.getDisk_write());
                idling.setText("Idling:");
                idling_avg.setText("Avg: " + intrusionDetails.getIdling().get("avg"));
                idling_min.setText("Min: " + intrusionDetails.getIdling().get("min"));
                idling_max.setText("Max: " + intrusionDetails.getIdling().get("max"));
                float usage = 100 - intrusionDetails.getIdling().get("min");
                cpu_usage.setText("CPU usage: " + usage);
                cpu_usage.setTextColor(Color.parseColor("#FF0000"));
                net_recv.setText("Network recieve: " + intrusionDetails.getNet_recv());
                net_send.setText("Network send: " + intrusionDetails.getNet_send());
                sys.setText("System:");
                sys_avg.setText("Avg: " + intrusionDetails.getSys().get("avg"));
                sys_min.setText("Min: " + intrusionDetails.getSys().get("min"));
                sys_max.setText("Max: " + intrusionDetails.getSys().get("max"));
                usr.setText("User:");
                usr_avg.setText("Avg: " + intrusionDetails.getUsr().get("avg"));
                usr_min.setText("Min: " + intrusionDetails.getUsr().get("min"));
                usr_max.setText("Max: " + intrusionDetails.getUsr().get("max"));


            }
        });

/*        // download
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
        });*/

        return view;
    }

    private void downloadFile(String date, String disk_read, String disk_write,
                              String idling, String net_recv, String net_send,
                              String sys, String usr){


        // Get the input from EditText
       String filepath = "MyFileDir";
       String filename = mParam1;

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
                fos.write(date.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));
                fos.write(disk_read.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));
                fos.write(disk_write.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));
                fos.write(idling.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));
                fos.write(net_recv.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));
                fos.write(net_send.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));
                fos.write(sys.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));
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



        /*
        FILE_NAME = mParam1;
        String nl = "\n";

        try{
            fos = getContext().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);

            fos.write(date.getBytes(StandardCharsets.UTF_8));
            fos.write(nl.getBytes(StandardCharsets.UTF_8));
            fos.write(disk_read.getBytes(StandardCharsets.UTF_8));
            fos.write(nl.getBytes(StandardCharsets.UTF_8));
            fos.write(disk_write.getBytes(StandardCharsets.UTF_8));
            fos.write(nl.getBytes(StandardCharsets.UTF_8));
            fos.write(idling.getBytes(StandardCharsets.UTF_8));
            fos.write(nl.getBytes(StandardCharsets.UTF_8));
            fos.write(net_recv.getBytes(StandardCharsets.UTF_8));
            fos.write(nl.getBytes(StandardCharsets.UTF_8));
            fos.write(net_send.getBytes(StandardCharsets.UTF_8));
            fos.write(nl.getBytes(StandardCharsets.UTF_8));
            fos.write(sys.getBytes(StandardCharsets.UTF_8));
            fos.write(nl.getBytes(StandardCharsets.UTF_8));
            fos.write(usr.getBytes(StandardCharsets.UTF_8));


            Toast.makeText(getContext(), "Saved to " + getContext().getFilesDir() + "/" + FILE_NAME, Toast.LENGTH_LONG).show();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
         */

    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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

}