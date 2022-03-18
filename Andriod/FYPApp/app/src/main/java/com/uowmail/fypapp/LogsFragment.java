package com.uowmail.fypapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class LogsFragment extends Fragment  {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_logs, container, false);

//        // MJ - set title of the page
//        TextView title = (TextView)getActivity().findViewById(R.id.toolbar_title);
//        title.setText("Logs");

        //TextView txt = (TextView)getActivity().findViewById(R.id.toolbartxt);
        //txt.setText("Logs");

        ImageView selectBtn = (ImageView)v.findViewById(R.id.Select);

        selectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                getParentFragmentManager().beginTransaction().replace(R.id.container, logsDetailFragment).commit();
                startActivity(new Intent(getActivity(), LogsDetailActivity.class));
            }
        });

        // MJ - set title of the page
        TextView title = (TextView)getActivity().findViewById(R.id.toolbar_title);
        title.setText("Logs");
        return v;
    }


}