package com.uowmail.fypapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AdminHomeFragment extends Fragment {
    RulesFragment rulesFragment = new RulesFragment();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // MJ - Buttons for admins
        Button rulesButton = v.findViewById(R.id.buttonRules);
        Button logoutButton = v.findViewById(R.id.buttonLogOut);

        // MJ - open RulesFragment when button is clicked
        rulesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().replace(R.id.container, rulesFragment).commit();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),MainActivity.class));
            }
        });

        return v;
    }

}