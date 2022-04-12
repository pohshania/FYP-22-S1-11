package com.uowmail.fypapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserHomeFragment extends Fragment  {

    // MJ - testing ----------------------------------------------------------------------------------------
    FirebaseFirestore fStore;
    Task<QuerySnapshot> qs;
    float idling, sys, usr;


    // MJ - swipte to refresh
    SwipeRefreshLayout refreshLayout;
    Integer num = 0;

    private PieChart pieChart, pieChart_network, pieChart_disk;

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

        // MJ - testing ----------------------------------------------------------------------------------------
        fStore = FirebaseFirestore.getInstance();
        getPieChartData();
//        qs = fStore.collection("UOW_log")
//                .orderBy("date", Query.Direction.DESCENDING)
//                .limit(1).get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            QuerySnapshot querySnapshot = task.getResult();
//        //                    String value = documentSnapshot.get("idling").toString();
//                            for (QueryDocumentSnapshot document : task.getResult()) {
////                                Log.d(TAG, document.getId() + " => " + document.get("idling"));
//                                String str;
//                                // Idling
//                                str = document.get("idling").toString();
//                                idling = Float.parseFloat(str.substring(str.indexOf("avg=")+4, str.indexOf(",")));
//                                // sys
//                                str = document.get("sys").toString();
//                                sys = Float.parseFloat(str.substring(str.indexOf("avg=")+4, str.indexOf(",")));
//                                //usr
//                                str = document.get("usr").toString();
//                                usr = Float.parseFloat(str.substring(str.indexOf("avg=")+4, str.indexOf(",")));
//
//                                // load data
//                                loadPieChartData(idling, sys, usr);
//                            }
//                        }
//                        else
//                        {
//                            System.out.println("hello it's failed");
//                        }
//                    }
//                });


        // JH - Implement Alert Button
        Button alertButton = (Button) v.findViewById(R.id.AlertButton);
        alertButton.setOnClickListener(new OnClickListener(){
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
//        loadPieChartData();

        // MJ - LINE CHART ------------------------------------------------------------------------------
        mChart = (LineChart) v.findViewById(R.id.linechart);
        setupLineChart();
        loadLineChartData();

        // MJ - Switch ------------------------------------------------------------------------------
        switchCompat = v.findViewById(R.id.switchButton);
        // main graph = donut graph shows on default
        pieChart.setVisibility(View.VISIBLE);
        switchCompat.setOnClickListener(new OnClickListener(){
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
//                num++;
                loadPieChartData(idling, sys, usr);
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
    private  void getPieChartData(){
        qs = fStore.collection("UOW_log")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String str;
                                Matcher m;
                                Pattern pat = Pattern.compile("avg=[-]?[0-9]*\\.?[0-9]+");

                                // Idling
                                str = document.get("idling").toString();
                                m = pat.matcher(str);
                                while(m.find()) {
                                    idling = Float.parseFloat(m.group().toString().substring(4));
                                }
                                // sys
                                str = document.get("sys").toString();
                                m = pat.matcher(str);
                                while(m.find()) {
                                    sys = Float.parseFloat(m.group().toString().substring(4));
                                }
                                // usr
                                str = document.get("usr").toString();
                                m = pat.matcher(str);
                                while(m.find()) {
                                    usr = Float.parseFloat(m.group().toString().substring(4));
                                }
                                System.out.println(document.getId()+"--->"+ idling +"=" +sys+"="  +usr+"<----");
                                // load data
                                loadPieChartData(idling, sys, usr);
                            }
                        }
                        else
                        {
                            System.out.println("hello it's failed");
                        }
                    }
                });
    }
    private void loadPieChartData(float idling, float sys, float usr) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(idling, "idling"));
        entries.add(new PieEntry(sys, "sys"));
        entries.add(new PieEntry(usr,"usr"));


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