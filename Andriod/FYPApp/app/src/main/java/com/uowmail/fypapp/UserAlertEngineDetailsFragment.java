package com.uowmail.fypapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
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
 * Use the {@link UserAlertEngineDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserAlertEngineDetailsFragment extends Fragment {

    private FirebaseFirestore db;

    private TextView abnormal;
    private TextView date;
    private TextView detected_by;
    private TextView disk_read;
    private TextView disk_write;
    private TextView event_status;
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

    public UserAlertEngineDetailsFragment() {
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
    public static UserAlertEngineDetailsFragment newInstance(String param1, String param2) {
        UserAlertEngineDetailsFragment fragment = new UserAlertEngineDetailsFragment();
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
            Log.d("===== INSIDE INTUSION DETAILS FRAG ===== ", mParam1);
            Log.d("===== INSIDE INTUSION DETAILS FRAG ===== ", mParam2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user_alert_engine_details, container, false);

        // MJ - set title of the page
        TextView title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        title.setText("Alert Engine Intrusion Details");

        // Title -> eg, 202204252157_alert
        TextView titleTV = view.findViewById(R.id.userAlertEngineDetails_Title);
        titleTV.setText(mParam1);


        abnormal     = view.findViewById(R.id.userAlertEngineDetails_abnormal);
        date         = view.findViewById(R.id.userAlertEngineDetails_date);
        detected_by  = view.findViewById(R.id.userAlertEngineDetails_detectedBy);
        disk_read    = view.findViewById(R.id.userAlertEngineDetails_diskRead);
        disk_write   = view.findViewById(R.id.userAlertEngineDetails_diskWrite);
        event_status = view.findViewById(R.id.userAlertEngineDetails_eventStatus);
        idling       = view.findViewById(R.id.userAlertEngineDetails_idling);
        idling_avg   = view.findViewById(R.id.userAlertEngineDetails_idling_avg);
        idling_min   = view.findViewById(R.id.userAlertEngineDetails_idling_min);
        idling_max   = view.findViewById(R.id.userAlertEngineDetails_idling_max);
        cpu_usage    = view.findViewById(R.id.userAlertEngineDetails_CPU_usage);
        net_recv     = view.findViewById(R.id.userAlertEngineDetails_netRecv);
        net_send     = view.findViewById(R.id.userAlertEngineDetails_netSend);
        sys          = view.findViewById(R.id.userAlertEngineDetails_sys);
        sys_avg      = view.findViewById(R.id.userAlertEngineDetails_sys_avg);
        sys_min      = view.findViewById(R.id.userAlertEngineDetails_sys_min);
        sys_max      = view.findViewById(R.id.userAlertEngineDetails_sys_max);
        usr          = view.findViewById(R.id.userAlertEngineDetails_usr);
        usr_avg      = view.findViewById(R.id.userAlertEngineDetails_usr_avg);
        usr_min      = view.findViewById(R.id.userAlertEngineDetails_usr_min);
        usr_max      = view.findViewById(R.id.userAlertEngineDetails_usr_max);




        // query
        String path = mParam2 + "_detection";
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(path).document(mParam1);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserAlertEngineDetailsModel intrusionDetails = documentSnapshot.toObject(UserAlertEngineDetailsModel.class);

                // abnormal
                abnormal.setText("Abnormal(s) Found: " + intrusionDetails.getAbnormal());
                abnormal.setTextColor(Color.parseColor("#FF0000"));


                // date
                //date.setText("Date: " + intrusionDetails.getDate().toDate());
                date.append(getColoredString(getContext(), intrusionDetails.getDate().toDate().toString(), Color.parseColor("#883000")));


                // detected by
                //detected_by.setText("Detected by: " + intrusionDetails.getDetected_by());
                detected_by.append(getColoredString(getContext(), intrusionDetails.getDetected_by(), Color.parseColor("#883000")));


                // disk
                //disk_read.setText("Disk Read: " + intrusionDetails.getDisk_read());
                //disk_write.setText("Disk Write: " + intrusionDetails.getDisk_write());
                disk_read.append(getColoredString(getContext(), intrusionDetails.getDisk_read(), Color.parseColor("#883000")));
                disk_write.append(getColoredString(getContext(), intrusionDetails.getDisk_write(), Color.parseColor("#883000")));


                // event status
                //event_status.setText("Event status: " + intrusionDetails.getEvent_status());
                event_status.append(getColoredString(getContext(), intrusionDetails.getEvent_status(), Color.parseColor("#883000")));

                // idling
                idling.setText("Idling:");
                //idling_avg.setText("Avg: " + intrusionDetails.getIdling().get("avg"));
                idling_avg.append(getColoredString(getContext(), intrusionDetails.getIdling().get("avg").toString(), Color.parseColor("#883000")));
                //idling_min.setText("Min: " + intrusionDetails.getIdling().get("min"));
                idling_min.append(getColoredString(getContext(), intrusionDetails.getIdling().get("min").toString(), Color.parseColor("#883000")));
                //idling_max.setText("Max: " + intrusionDetails.getIdling().get("max"));
                idling_max.append(getColoredString(getContext(), intrusionDetails.getIdling().get("max").toString(), Color.parseColor("#883000")));
                float usage = 100 - intrusionDetails.getIdling().get("avg");
                //cpu_usage.setText("CPU usage: " + usage);
                cpu_usage.append(getColoredString(getContext(), String.valueOf(usage), Color.parseColor("#883000")));


                // network
                //net_recv.setText("Network recieve: " + intrusionDetails.getNet_recv());
                net_recv.append(getColoredString(getContext(), intrusionDetails.getNet_recv(), Color.parseColor("#883000")));
                //net_send.setText("Network send: " + intrusionDetails.getNet_send());
                net_send.append(getColoredString(getContext(), intrusionDetails.getNet_send(), Color.parseColor("#883000")));

                // sys
                sys.setText("System:");
                //sys_avg.setText("Avg: " + intrusionDetails.getSys().get("avg"));
                sys_avg.append(getColoredString(getContext(), intrusionDetails.getSys().get("avg").toString(), Color.parseColor("#883000")));
                //sys_min.setText("Min: " + intrusionDetails.getSys().get("min"));
                sys_min.append(getColoredString(getContext(), intrusionDetails.getSys().get("min").toString(), Color.parseColor("#883000")));
                //sys_max.setText("Max: " + intrusionDetails.getSys().get("max"));
                sys_max.append(getColoredString(getContext(), intrusionDetails.getSys().get("max").toString(), Color.parseColor("#883000")));

                // usr
                usr.setText("User:");
                //usr_avg.setText("Avg: " + intrusionDetails.getUsr().get("avg"));
                usr_avg.append(getColoredString(getContext(), intrusionDetails.getUsr().get("avg").toString(), Color.parseColor("#883000")));
                //usr_min.setText("Min: " + intrusionDetails.getUsr().get("min"));
                usr_min.append(getColoredString(getContext(), intrusionDetails.getUsr().get("min").toString(), Color.parseColor("#883000")));
                //usr_max.setText("Max: " + intrusionDetails.getUsr().get("max"));
                usr_max.append(getColoredString(getContext(), intrusionDetails.getUsr().get("max").toString(), Color.parseColor("#883000")));



                // higlight the abnormal activity in red
                int len = intrusionDetails.getAbnormal().size();
                for(int i = 0; i < len; i++){
                    String abnormalType = intrusionDetails.getAbnormal().get(i);
                    Log.d("===== ABNORMAL_TYPE =====", abnormalType);

                    if(abnormalType.equals("CPU max")){
                        cpu_usage.setTextColor(Color.parseColor("#FF0000"));
                        cpu_usage.setPaintFlags(cpu_usage.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    }

                    if(abnormalType.equals("disk_read")){
                        disk_read.setTextColor(Color.parseColor("#FF0000"));
                        disk_read.setPaintFlags(disk_read.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    }

                    if(abnormalType.equals("disk_writ")){
                        disk_write.setTextColor(Color.parseColor("#FF0000"));
                        disk_write.setPaintFlags(disk_write.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    }

                    if(abnormalType.equals("net_recv")){
                        net_recv.setTextColor(Color.parseColor("#FF0000"));
                        net_recv.setPaintFlags(net_recv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    }

                    if(abnormalType.equals("net_send")){
                        net_send.setTextColor(Color.parseColor("#FF0000"));
                        net_send.setPaintFlags(net_send.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    }

                }
            }
        });

        // download
        download = view.findViewById(R.id.userAlertEngineDetails_download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile(abnormal.getText().toString(), date.getText().toString(), detected_by.getContext().toString(), disk_read.getText().toString(), disk_write.getText().toString(),
                        event_status.getText().toString(), idling.getText().toString() + " " + idling_avg.getText().toString() + " " + idling_min.getText().toString() + " " + idling_max.getText().toString(),
                        net_recv.getText().toString(), net_send.getText().toString(),
                        sys.getText().toString() + " " + sys_avg.getText().toString() + " " + sys_min.getText().toString() + " " + sys_max.getText().toString(),
                        usr.getText().toString() + " " + usr_avg.getText().toString() + " " + usr_min.getText().toString() + " " + usr_max.getText().toString());
            }
        });

        return view;
    }

    private void downloadFile(String abnormal, String date, String detected_by,
                              String disk_read, String disk_write, String event_status,
                              String idling, String net_recv, String net_send,
                              String sys, String usr){


        // Get the input from EditText
       String filepath = "MyFileDir";
       String filename = mParam1 + "_alert_log_details";

        // Check for Storage Permission
        if(isStoragePermissionGranted()){

            File myExternalFile = new File(getContext().getExternalFilesDir(filepath), filename);
            // Create an object of FileOutputStream for writing data to myFile.txt
            FileOutputStream fos = null;
            try {
                // Instantiate the FileOutputStream object and pass myExternalFile in constructor
                fos = new FileOutputStream(myExternalFile);


                // Write to the file
                String nl = "\n";
                fos.write(abnormal.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));
                fos.write(date.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));
                fos.write(detected_by.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));
                fos.write(disk_read.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));
                fos.write(disk_write.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));
                fos.write(event_status.getBytes(StandardCharsets.UTF_8));
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

    public static final Spannable getColoredString(Context context, CharSequence text, int color) {
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(color), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }
}