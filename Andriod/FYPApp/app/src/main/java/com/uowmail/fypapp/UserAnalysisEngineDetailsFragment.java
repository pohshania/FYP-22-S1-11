package com.uowmail.fypapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserAnalysisEngineDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserAnalysisEngineDetailsFragment extends Fragment {

    private FirebaseFirestore fStore;

    private TextView date;
    private TextView detected_range;
    private TextView detected_by;
    private TextView detection_type;
    private TextView event_status;

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





        date = view.findViewById(R.id.userAnalysisEngineDetails_date);
        detected_range = view.findViewById(R.id.userAnalysisEngineDetails_detected_range);
        detected_by = view.findViewById(R.id.userAnalysisEngineDetails_detected_by);
        detection_type = view.findViewById(R.id.userAnalysisEngineDetails_detection_type);;
        event_status = view.findViewById(R.id.userAnalysisEngineDetails_event_status);;


        // query
        String path = mParam2 + "_detection";
        fStore = FirebaseFirestore.getInstance();
        DocumentReference docRef = fStore.collection(path).document(mParam1);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                UserAnalysisEngineDetailsModel intrusionDetails = snapshot.toObject(UserAnalysisEngineDetailsModel.class);


                // date
                date.setText("Date: " + intrusionDetails.getDate().toString());

                // detected range
                detected_range.setText("Detected range: " + intrusionDetails.getDetected_range().toString());

                // detected by
                detected_by.setText("Detected by: " + intrusionDetails.getDetected_by());

                // detection type
                detection_type.setText("Detection type: " + intrusionDetails.getDetection_type());

                // event status
                event_status.setText("Event status: " + intrusionDetails.getEvent_status());
            }
        });



        // Inflate the layout for this fragment
        return view;
    }
}