package com.uowmail.fypapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AdminHomeFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    Button createUserBtn, deactivateUseBtn;

    public AdminHomeFragment() {
        // Required empty public constructor
    }

    public static AdminHomeFragment newInstance(String param1) {
        AdminHomeFragment fragment = new AdminHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            Log.d("===== INSIDE ADMIN HOME FRAG ===== ", mParam1);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_admin_home, container, false);


        // MJ - Buttons for admins
        Button rulesButton = v.findViewById(R.id.buttonRules);
        //Button logoutButton = v.findViewById(R.id.buttonLogOut);
        Button logsButton = v.findViewById(R.id.buttonLogs);

        // Shania
        createUserBtn = v.findViewById(R.id.createUser_btn);
        deactivateUseBtn = v.findViewById(R.id.adminHome_deactivateUser_btn);


        logsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //startActivity(new Intent(getActivity(), AdminDeleteLogsActivity.class));
                Intent i = new Intent(getActivity(), AdminDeleteLogsActivity.class);
                i.putExtra("adminOrgID",mParam1);
                startActivity(i);
            }
        });
        // MJ - open RulesActivity when button is clicked
        rulesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //System.out.println("button clicked");
                //startActivity(new Intent(getActivity(), AdminRulesActivity.class));
                Intent i = new Intent(getActivity(), AdminRulesActivity.class);
                i.putExtra("adminOrgID",mParam1);
                startActivity(i);
            }
        });

        /*
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });*/

        createUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AdminCreateNewUserActivity.class));
            }
        });



        deactivateUseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AdminDeactivateUserActivity.class));
            }
        });


        return v;
    }

}