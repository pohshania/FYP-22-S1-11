package com.uowmail.fypapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

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
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserAnalysisEngineDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserAnalysisEngineDetailsFragment extends Fragment {

    private FirebaseFirestore fStore;

    private TextView anomaly;
    private TextView date;
    private TextView detection_range;
    private TextView detection_type;
    private TextView event_status;
    private Button correctButton;
    private Button ignoreButton;

    private Button download;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserAnalysisEngineDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserAnalysisEngineDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserAnalysisEngineDetailsFragment newInstance(String param1, String param2) {
        UserAnalysisEngineDetailsFragment fragment = new UserAnalysisEngineDetailsFragment();
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

            Log.d("========IN ANALYSIS ENGINE DETAILS========", mParam1);
            Log.d("========IN ANALYSIS ENGINE DETAILS========", mParam2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_analysis_engine_details, container, false);

        // MJ - set title of the page
        TextView title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        title.setText("Analysis Engine Intrusion Details");

        // Title -> eg, 202204252157_alert
        TextView titleTV = view.findViewById(R.id.userAnalysisEngineDetails_Title);
        titleTV.setText(mParam1);

        anomaly        = view.findViewById(R.id.userAnalysisEngineDetails_anomaly);
        date           = view.findViewById(R.id.userAnalysisEngineDetails_date);
        detection_range = view.findViewById(R.id.userAnalysisEngineDetails_detection_range);
        detection_type = view.findViewById(R.id.userAnalysisEngineDetails_detection_type);
        event_status   = view.findViewById(R.id.userAnalysisEngineDetails_event_status);

        // Steven - add feedback button
        correctButton = view.findViewById(R.id.correctButton);
        ignoreButton = view.findViewById(R.id.ignoreButton);
        correctButton.setOnClickListener(correctButtonListener);
        ignoreButton.setOnClickListener(ignoreButtonListener);

        // query
        String path = mParam2 + "_detection";
        fStore = FirebaseFirestore.getInstance();
        DocumentReference docRef = fStore.collection(path).document(mParam1);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {

                Log.d("=====SNAPSHOT==========", snapshot.getData().toString());

                UserAnalysisEngineDetailsModel intrusionDetails = snapshot.toObject(UserAnalysisEngineDetailsModel.class);

                Log.d("======MODEL======", intrusionDetails.getAnomaly().toString());
                // anomaly
                //anomaly.append(getColoredString(getContext(), intrusionDetails.getAnomaly(), Color.parseColor("#0000FF")));
                //anomaly.setText("Abnormal(s) Found: " + intrusionDetails.getAnomaly());
                //anomaly.setTextColor(Color.parseColor("#FF0000"));

                // date
                //date.setText("Date: " + intrusionDetails.getDate().toDate());
                date.append(getColoredString(getContext(), intrusionDetails.getDate().toDate().toString(), Color.parseColor("#883000")));


                // detection range
                //detected_range.setText("Detected range: " + intrusionDetails.getDetected_range().toDate());
                detection_range.append(getColoredString(getContext(), intrusionDetails.getDetection_range().toDate().toString(), Color.parseColor("#883000")));
                //Log.d("============DETECTION RANGE=======", intrusionDetails.getDetection_range().toDate().toString());

                // detection type
                //detection_type.setText("Detection type: " + intrusionDetails.getDetection_type());
                detection_type.append(getColoredString(getContext(), intrusionDetails.getDetection_type(), Color.parseColor("#883000")));


                // event status
                //event_status.setText("Event status: " + intrusionDetails.getEvent_status());
                event_status.append(getColoredString(getContext(), intrusionDetails.getEvent_status(), Color.parseColor("#883000")));
            }
        });

        // download
        download = view.findViewById(R.id.userAnalysisEngineDetails_download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile(date.getText().toString(), detection_range.getText().toString(), detection_type.getText().toString(),
                        event_status.getText().toString());
            }
        });


        // Inflate the layout for this fragment
        return view;
    }


    private void downloadFile(String date, String detection_range,
                              String detection_type, String event_status){

        // Get the input from EditText
        String filepath = "MyFileDir";
        String filename = mParam1 + "_analysis_log_details";

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
                // detection range
                fos.write(detection_range.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));
                // detection type
                fos.write(detection_type.getBytes(StandardCharsets.UTF_8));
                fos.write(nl.getBytes(StandardCharsets.UTF_8));
                // event status
                fos.write(event_status.getBytes(StandardCharsets.UTF_8));


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

    // Steven - get feedback and update firebase
    private final View.OnClickListener correctButtonListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View w)
                {
                    updateStatus("confirmed");
                }
            };
    private final View.OnClickListener ignoreButtonListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View w)
                {
                    updateStatus("ignored");
                }
            };

    public void updateStatus(String status)
    {
        String path = mParam2 + "_detection";
        DocumentReference docRef = fStore.collection(path).document(mParam1);

        docRef.update("event_status", status)
              .addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void unused) {
                      Log.d("=====FIRESTORE_QUERR=====", "DocumentSnapshot successfully updated!");
                  }
              });
    }

    public static final Spannable getColoredString(Context context, CharSequence text, int color) {
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(color), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }
}