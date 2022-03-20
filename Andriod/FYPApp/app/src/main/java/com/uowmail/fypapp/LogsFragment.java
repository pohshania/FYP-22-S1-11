package com.uowmail.fypapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class LogsFragment extends Fragment  {
    SwipeRefreshLayout refreshLogsLayout;
    TableLayout logsTable;
    TableRow logsRow;

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_logs, container, false);

        ImageView selectBtn = (ImageView) v.findViewById(R.id.Select);

        selectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                getParentFragmentManager().beginTransaction().replace(R.id.container, logsDetailFragment).commit();
                startActivity(new Intent(getActivity(), LogsDetailActivity.class));
            }
        });

        // MJ - set title of the page
        TextView title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        title.setText("Logs");

        // MJ - Swipe to refresh
        refreshLogsLayout = v.findViewById(R.id.refreshLogsLayout);
        logsTable = (TableLayout) v.findViewById(R.id.logs_table);
        refreshLogsLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onRefresh() {
                // Creating a new log
                TableRow row = new TableRow(getActivity());
                row.setBackgroundColor(Color.WHITE);
                row.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));

                TextView time = new TextView(getActivity());
                time.setText("1 second ago");
                time.setPadding(5, 5, 5, 5);
                row.addView(time);

                TextView domain = new TextView(getActivity());
                domain.setText("MineCraft.com");
                domain.setPadding(5, 5, 5, 5);
                row.addView(domain);

                TextView loc = new TextView(getActivity());
                loc.setText("China");
                loc.setPadding(5, 5, 5, 5);
                row.addView(loc);

                TextView ip = new TextView(getActivity());
                ip.setText("137.46.762");
                ip.setPadding(5, 5, 5, 5);
                row.addView(ip);

                ImageView arrow = new ImageView(getActivity());
                arrow.setImageResource(R.drawable.arrow_circle_right);
                row.addView(arrow);

                // Add to the top row
                logsTable.addView(row, 1);

                refreshLogsLayout.setRefreshing(false);
            }
        });


        return v;
    }


}