package com.uowmail.fypapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ipsec.ike.exceptions.InvalidMajorVersionException;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class UserHomeFragment extends Fragment  {
    // MJ - swipte to refresh
    SwipeRefreshLayout refreshLayout;
    Integer num = 0;

    private PieChart pieChart;

    // MJ - LINE CHART
    private static final String TAG = "MainActivity";
    private LineChart mChart;

    // MJ - switch
    SwitchCompat switchCompat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_client_home, container, false);

        // JH - Implement Alert Button
        Button alertButton = (Button) v.findViewById(R.id.AlertButton);
        alertButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("ATTACK DETECTED!\n Incoming attack on Game Server!");
                alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "Alert viewed", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
        });

        // MJ - set title of the page
        TextView title = (TextView)getActivity().findViewById(R.id.toolbar_title);
        title.setText("Creeping Donut");

        // MJ - adding pieChart-------------------------------------------------------------------------------
        pieChart = v.findViewById(R.id.piechart);
        setupPieChart();
        loadPieChartData();

        // MJ - LINE CHART ------------------------------------------------------------------------------
        mChart = (LineChart) v.findViewById(R.id.linechart);
        setupLineChart();
        loadLineChartData();

        // MJ - Switch ------------------------------------------------------------------------------
        switchCompat = v.findViewById(R.id.switchButton);
        // main graph = donut graph shows on default
        pieChart.setVisibility(View.VISIBLE);
        switchCompat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (switchCompat.isChecked()){
                    // set it as line graph
                    pieChart.setVisibility(View.GONE);
                    mChart.setVisibility(View.VISIBLE);

                }else{
                    // set it as donut graph
                    pieChart.setVisibility(View.VISIBLE);
                    mChart.setVisibility(View.GONE);
                }
            }
        });



        // MJ - Swipe to refresh
        refreshLayout = v.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // update graph data
                num++;
                loadPieChartData();
                loadLineChartData();

                refreshLayout.setRefreshing(false);
            }
        });


        return v;
    }


    // MJ - adding pieChart-------------------------------------------------------------------------------
    private void setupPieChart(){
        // Donut not pie
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("All events by Event Type");
        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);
        pieChart.setTouchEnabled(false);

        // get legend
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (num==0){
            entries.add(new PieEntry(0.2f, "User Login"));
            entries.add(new PieEntry(0.15f, "User Logout"));
            entries.add(new PieEntry(0.10f,"PolicyScopeChange"));
            entries.add(new PieEntry(0.25f, "FileDataWrite"));
            entries.add(new PieEntry(0.15f, "MachineLogin"));
            entries.add(new PieEntry(0.15f, "MachineLogoff"));

            // add colors to the pieChart
            ArrayList<Integer> colors = new ArrayList<>();
            for (int color: ColorTemplate.MATERIAL_COLORS) {
                colors.add(color);
            }
            for (int color: ColorTemplate.VORDIPLOM_COLORS){
                colors.add(color);
            }
            PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
            dataSet.setColors(colors);

            PieData data = new PieData(dataSet);
            data.setDrawValues(true);
            data.setValueFormatter(new PercentFormatter(pieChart));
            data.setValueTextSize(12f);
            data.setValueTextColor(Color.BLACK);

            pieChart.setData(data);
        }
        else
        {
            entries.add(new PieEntry(0.5f, "User Login"));
            entries.add(new PieEntry(0.05f, "PolicyScopeChange"));
            entries.add(new PieEntry(0.30f, "FileDataWrite"));
            entries.add(new PieEntry(0.15f, "MachineLogin"));


            // add colors to the pieChart
            ArrayList<Integer> colors = new ArrayList<>();
            for (int color: ColorTemplate.MATERIAL_COLORS) {
                colors.add(color);
            }
            for (int color: ColorTemplate.VORDIPLOM_COLORS){
                colors.add(color);
            }
            PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
            dataSet.setColors(colors);

            PieData data = new PieData(dataSet);
            data.setDrawValues(true);
            data.setValueFormatter(new PercentFormatter(pieChart));
            data.setValueTextSize(12f);
            data.setValueTextColor(Color.BLACK);

            pieChart.setData(data);
        }
        pieChart.invalidate();
    }


    // MJ - adding Linechart-------------------------------------------------------------------------------
    private void setupLineChart() {
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
    }
    private void loadLineChartData() {
        ArrayList<Entry> yValues = new ArrayList<>();
        if(num==0)
        {
            yValues.add(new Entry(0, 60f));
            yValues.add(new Entry(1, 50f));
            yValues.add(new Entry(2, 70f));
            yValues.add(new Entry(3, 30f));
            yValues.add(new Entry(4, 50f));
            yValues.add(new Entry(5, 60f));
            yValues.add(new Entry(6, 65f));


            LineDataSet set1 = new LineDataSet(yValues, "Data set 1");

            set1.setFillAlpha(110);
            set1.setColors(Color.RED);
            set1.setLineWidth(3f);
            set1.setValueTextSize(10f);
            set1.setValueTextColor(Color.BLUE);


            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            LineData data = new LineData(dataSets);
            mChart.setData(data);
        }
        else
        {
            yValues.add(new Entry(0, 60f));
            yValues.add(new Entry(1, 50f));
            yValues.add(new Entry(2, 70f));
            yValues.add(new Entry(3, 30f));
            yValues.add(new Entry(4, 50f));
            yValues.add(new Entry(5, 60f));
            yValues.add(new Entry(6, 65f));
            yValues.add(new Entry(7, 9f));

            LineDataSet set1 = new LineDataSet(yValues, "Data set 1");

            set1.setFillAlpha(110);
            set1.setColors(Color.RED);
            set1.setLineWidth(3f);
            set1.setValueTextSize(10f);
            set1.setValueTextColor(Color.BLUE);


            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            LineData data = new LineData(dataSets);
            mChart.setData(data);
        }
    }

}