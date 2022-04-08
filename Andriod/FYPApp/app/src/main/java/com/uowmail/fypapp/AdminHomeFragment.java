package com.uowmail.fypapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AdminHomeFragment extends Fragment {
    Button createUserBtn, deactivateUseBtn;

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
                startActivity(new Intent(getActivity(), AdminViewUserActivity.class));
            }
        });
        // MJ - open RulesActivity when button is clicked
        rulesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("button clicked");
                startActivity(new Intent(getActivity(), RulesActivity.class));
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