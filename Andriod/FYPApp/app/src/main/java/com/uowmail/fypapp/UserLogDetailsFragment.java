package com.uowmail.fypapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

    // TODO: Rename and change types of parameters
    private String mParam1;

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
    public static UserLogDetailsFragment newInstance(String param1) {
        UserLogDetailsFragment fragment = new UserLogDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
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
        DocumentReference docRef = db.collection("UOW_log").document(mParam1);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserLogDetailsModel logDetails = documentSnapshot.toObject(UserLogDetailsModel.class);

                Log.d("TEST_QUERY", logDetails.getDate() + " " + logDetails.getDisk_read() + " " + logDetails.getDisk_write() + " " +
                        logDetails.getIdling() + " " + logDetails.getNet_recv() + " " + logDetails.getNet_send() + " " + logDetails.getSys() +
                        " " + logDetails.getUsr());

                //Date d = logDetails.getTimestamp().toDate();
                date.setText("Date: " + logDetails.getDate().toDate());
                disk_read.setText("Disk Read: " + logDetails.getDisk_read());
                disk_write.setText("Disk Write: " + logDetails.getDisk_write());
                idling.setText("Idling:");
                idling_avg.setText("Avg: " + logDetails.getIdling().get("avg"));
                idling_min.setText("Min: " + logDetails.getIdling().get("min"));
                idling_max.setText("Max: " + logDetails.getIdling().get("max"));
                net_recv.setText("Network recieve: " + logDetails.getNet_recv());
                net_send.setText("Network send: " + logDetails.getNet_send());
                sys.setText("System:");
                sys_avg.setText("Avg: " + logDetails.getSys().get("avg"));
                sys_min.setText("Min: " + logDetails.getSys().get("min"));
                sys_max.setText("Max: " + logDetails.getSys().get("max"));
                usr.setText("User:");
                usr_avg.setText("Avg: " + logDetails.getUsr().get("avg"));
                usr_min.setText("Min: " + logDetails.getUsr().get("min"));
                usr_max.setText("Max: " + logDetails.getUsr().get("max"));


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
    }
}