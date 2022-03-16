package com.uowmail.fypapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NotificationFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View v = inflater.inflate(R.layout.fragment_notification, container, false);

        TextView title = (TextView)getActivity().findViewById(R.id.toolbar_title);
        title.setText("Notification");

        //TextView txt = (TextView)getActivity().findViewById(R.id.toolbartxt);
        //txt.setText("Notifications");


        return v;
    }
}