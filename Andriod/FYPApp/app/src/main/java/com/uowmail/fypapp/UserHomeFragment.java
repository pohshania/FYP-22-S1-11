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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserHomeFragment extends Fragment  {

    // shania
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

    // MJ - testing ----------------------------------------------------------------------------------------
    FirebaseFirestore fStore;
    Task<QuerySnapshot> qs;
    float idling, sys, usr, net_recv, net_send, disk_read, disk_write;

    // MJ - swipte to refresh
    SwipeRefreshLayout refreshLayout;
    Integer num = 0;

    // MJ - PieChart
    private PieChart pieChart_cpu, pieChart_network, pieChart_disk;
    Button cpuBtn, networkBtn, diskBtn;

    // MJ - LINE CHART
    private static final String TAG = "MainActivity";
    private LineChart mChart;
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();


    // MJ - switch
    SwitchCompat switchCompat;

    // shania
    public UserHomeFragment() {
        // Required empty public constructor
    }
    // shania
    public static UserHomeFragment newInstance(String param1) {
        UserHomeFragment fragment = new UserHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
    // shania
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            Log.d("===== INSIDE USER DASHBOARD FRAG ===== ", mParam1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_client_home, container, false);

        // MJ - to fetch data from database
        fStore = FirebaseFirestore.getInstance();


        // JH - Implement Alert Button
//        Button alertButton = (Button) v.findViewById(R.id.AlertButton);
        cpuBtn = (Button) v.findViewById(R.id.cpuButton);
        networkBtn = (Button) v.findViewById(R.id.networkButton);
        diskBtn = (Button) v.findViewById(R.id.diskButton);

//        alertButton.setOnClickListener(new OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
//                alert.setMessage("ATTACK DETECTED!\n Incoming attack on Game Server!");
//                alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(getContext(), "Alert viewed", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//                alert.show();
//            }
//        });
        cpuBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                pieChart_cpu.setVisibility(View.VISIBLE);
                pieChart_network.setVisibility(View.GONE);
                pieChart_disk.setVisibility(View.GONE);
            }
        });
        networkBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                pieChart_cpu.setVisibility(View.GONE);
                pieChart_network.setVisibility(View.VISIBLE);
                pieChart_disk.setVisibility(View.GONE);
            }
        });
        diskBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                pieChart_cpu.setVisibility(View.GONE);
                pieChart_network.setVisibility(View.GONE);
                pieChart_disk.setVisibility(View.VISIBLE);
            }
        });

        // MJ - set title of the page
        TextView title = (TextView)getActivity().findViewById(R.id.toolbar_title);
        title.setText("Creeping Donut");

        // MJ - adding pieChart-------------------------------------------------------------------------------
        pieChart_cpu = v.findViewById(R.id.piechart_cpu);
        getPieChartData(pieChart_cpu, "cpu");
        setupPieChart(pieChart_cpu, "CPU Usage");

        pieChart_network = v.findViewById(R.id.piechart_network);
        getPieChartData(pieChart_network, "network");
        setupPieChart(pieChart_network, "Network Usage");

        pieChart_disk = v.findViewById(R.id.piechart_disk);
        getPieChartData(pieChart_disk, "disk");
        setupPieChart(pieChart_disk, "Disk Usage");

        // MJ - LINE CHART ------------------------------------------------------------------------------
        mChart = (LineChart) v.findViewById(R.id.linechart);
        setupLineChart();
        getLineChartDate("cpu", "idling");
        getLineChartDate("cpu", "sys");
        getLineChartDate("cpu", "usr");
        getLineChartDate("network", "net_send");
        getLineChartDate("network", "net_recv");
        getLineChartDate("disk", "disk_read");
        getLineChartDate("disk", "disk_write");


        // MJ - Switch ------------------------------------------------------------------------------
        switchCompat = v.findViewById(R.id.switchButton);
        // main graph = donut graph shows on default
        pieChart_cpu.setVisibility(View.VISIBLE);
        switchCompat.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                if (switchCompat.isChecked()){
                    // set it as line graph
                    pieChart_cpu.setVisibility(View.GONE);
                    pieChart_network.setVisibility(View.GONE);
                    pieChart_disk.setVisibility(View.GONE);
                    cpuBtn.setVisibility(View.GONE);
                    networkBtn.setVisibility(View.GONE);
                    diskBtn.setVisibility(View.GONE);
                    mChart.setVisibility(View.VISIBLE);

                }else{
                    // set it as donut graph
                    pieChart_cpu.setVisibility(View.VISIBLE);
                    pieChart_network.setVisibility(View.GONE);
                    pieChart_disk.setVisibility(View.GONE);
                    cpuBtn.setVisibility(View.VISIBLE);
                    networkBtn.setVisibility(View.VISIBLE);
                    diskBtn.setVisibility(View.VISIBLE);
                    mChart.setVisibility(View.GONE);
                }
            }
        });

        // button to change pieChart graph info (cpu, network or disk)

        // MJ - Swipe to refresh
        refreshLayout = v.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPieChartData(pieChart_cpu, "cpu");
//                getLineChartDate();
                refreshLayout.setRefreshing(false);
            }
        });


        return v;
    }


    // MJ - adding pieChart-------------------------------------------------------------------------------
    private void setupPieChart(PieChart pieChart, String info){
        // Donut not pie
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText(info);
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
    private void getPieChartData(PieChart pieChart, final String dataType){
        ArrayList<Float> dataValueList = new ArrayList<Float>();
        ArrayList<String> dataNameList = new ArrayList<String>();

        // shania
        String path = mParam1 + "_log";
        qs = fStore.collection(path)
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
                                if (dataType == "cpu")
                                {
                                    // Idling
                                    str = document.get("idling").toString();
                                    m = pat.matcher(str);
                                    while(m.find()) {
                                        dataValueList.add(Float.parseFloat(m.group().toString().substring(4)));
                                        dataNameList.add("idling");
                                    }
                                    // sys
                                    str = document.get("sys").toString();
                                    m = pat.matcher(str);
                                    while(m.find()) {
                                        dataValueList.add(Float.parseFloat(m.group().toString().substring(4)));
                                        dataNameList.add("sys");
                                    }
                                    // usr
                                    str = document.get("usr").toString();
                                    m = pat.matcher(str);
                                    while(m.find()) {
                                        dataValueList.add(Float.parseFloat(m.group().toString().substring(4)));
                                        dataNameList.add("usr");
                                    }
                                }

                                else if (dataType == "network")
                                {
                                    // net_recv
                                    str = document.get("net_recv").toString();
                                    str = str.trim().substring(0, str.length()-1);
                                    dataValueList.add(Float.parseFloat(str));
                                    dataNameList.add("net_recv");

                                    // net_send
                                    str = document.get("net_send").toString();
                                    str = str.trim().substring(0, str.length()-1);
                                    dataValueList.add(Float.parseFloat(str));
                                    dataNameList.add("net_send");
                                }

                                else if (dataType == "disk")
                                {
                                    // disk_read
                                    str = document.get("disk_read").toString();
                                    str = str.trim().substring(0, str.length()-1);
                                    dataValueList.add(Float.parseFloat(str));
                                    dataNameList.add("disk_read");

                                    // disk_write
                                    str = document.get("disk_write").toString();
                                    str = str.trim().substring(0, str.length()-1);
                                    dataValueList.add(Float.parseFloat(str));
                                    dataNameList.add("disk_write");
                                }

                                System.out.println(document.getId()+"--->"+ idling +"=" +sys+
                                        "="  +usr+" = "+net_recv+net_send+disk_read+disk_write+"<----");

                                //load data graph
                                loadPieChartData(pieChart, dataValueList, dataNameList);
                                System.out.println(dataType);
                            }
                        }
                        else
                        {
                            System.out.println("hello it's failed");
                        }
                    }
                });
    }
    private void loadPieChartData(PieChart pieChart, ArrayList<Float> dataVal,
                                  ArrayList<String> dataName) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for(int i=0; i<dataVal.size(); i++)
        {
            entries.add(new PieEntry(dataVal.get(i), dataName.get(i)));

        }

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

    private void getLineChartDate(String dataType, String dataName){
        ArrayList<Long> dateValueList = new ArrayList<Long>();
        ArrayList<Float> dataValueList = new ArrayList<Float>();
        ArrayList<String> dataNameList = new ArrayList<String>();

        qs = fStore.collection("UOW_log")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(5).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            System.out.println("LineGraph-------->");
                            QuerySnapshot querySnapshot = task.getResult();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String str;
                                Matcher m;
                                Pattern pat = Pattern.compile("avg=[-]?[0-9]*\\.?[0-9]+");
                                if (dataType == "cpu")
                                {
                                    // Idling
                                    if(dataName=="idling")
                                    {
                                        str = document.get("idling").toString();
                                        m = pat.matcher(str);
                                        while(m.find()) {
                                            dataValueList.add(Float.parseFloat(m.group().toString().substring(4)));
                                            dateValueList.add(Long.valueOf(document.getId()));
                                            dataNameList.add("idling");
                                        }
                                    }

                                    // sys
                                    if(dataName=="sys")
                                    {
                                        str = document.get("sys").toString();
                                        m = pat.matcher(str);
                                        while(m.find()) {
                                            dataValueList.add(Float.parseFloat(m.group().toString().substring(4)));
                                            dateValueList.add(Long.valueOf(document.getId()));
                                            dataNameList.add("sys");
                                        }
                                    }

                                    // usr
                                    if(dataName=="usr")
                                    {
                                        str = document.get("usr").toString();
                                        m = pat.matcher(str);
                                        while(m.find()) {
                                            dataValueList.add(Float.parseFloat(m.group().toString().substring(4)));
                                            dateValueList.add(Long.valueOf(document.getId()));
                                            dataNameList.add("usr");
                                        }
                                    }
                                }

                                else if (dataType == "network")
                                {
                                    // net_recv
                                    if(dataName == "net_recv"){
                                        str = document.get("net_recv").toString();
                                        str = str.trim().substring(0, str.length()-1);
                                        dataValueList.add(Float.parseFloat(str));
                                        dateValueList.add(Long.valueOf(document.getId()));
                                        dataNameList.add("net_recv");
                                    }
                                    // net_send
                                    else if(dataName == "net_send"){
                                        str = document.get("net_send").toString();
                                        str = str.trim().substring(0, str.length()-1);
                                        dataValueList.add(Float.parseFloat(str));
                                        dateValueList.add(Long.valueOf(document.getId()));
                                        dataNameList.add("net_send");
                                    }
                                }

                                else if (dataType == "disk")
                                {
                                    // disk_read
                                    if(dataName == "disk_read"){
                                        str = document.get("disk_read").toString();
                                        str = str.trim().substring(0, str.length()-1);
                                        dataValueList.add(Float.parseFloat(str));
                                        dateValueList.add(Long.valueOf(document.getId()));
                                        dataNameList.add("disk");
                                    }
                                    // disk_write
                                    if(dataName=="disk_write"){
                                        str = document.get("disk_write").toString();
                                        str = str.trim().substring(0, str.length()-1);
                                        dataValueList.add(Float.parseFloat(str));
                                        dateValueList.add(Long.valueOf(document.getId()));
                                        dataNameList.add("disk_write");
                                    }

                                }

                                //load data graph
//                                loadLineChartData(dataNameList.get(0), dateValueList, dataValueList);
//                                System.out.println(dataType);
                            }
                            for(int i=0; i<dataNameList.size(); i++)
                            {
                                System.out.print(dataNameList.get(i) + "=");
                            }
                            loadLineChartData(dataNameList.get(0), dateValueList, dataValueList);
                        }
                        else
                        {
                            System.out.println("hello it's failed");
                        }
                    }
                });
    }
    private void loadLineChartData(String info, ArrayList<Long> dateVal , ArrayList<Float> dataVal) {
        for (int i = 0; i < dateVal.size();i++)
        {
            System.out.print(dateVal.get(i) + " ~ ");
            System.out.print(dataVal.get(i) + " ~ ");

        }

        ArrayList<Entry> yValues = new ArrayList<>();

        for(int i=0; i<dataVal.size(); i++)
        {
            yValues.add(new Entry(i, dataVal.get(i)));

        }
//        yValues.add(new Entry(0, 60f));
//        yValues.add(new Entry(1, 50f));
//        yValues.add(new Entry(2, 70f));
//        yValues.add(new Entry(3, 30f));
//        yValues.add(new Entry(4, 50f));
//        yValues.add(new Entry(5, 60f));
//        yValues.add(new Entry(6, 65f));

        // set title of the line graph
        LineDataSet set1 = new LineDataSet(yValues, info);

        set1.setFillAlpha(110);
        set1.setColors(Color.RED);
        set1.setLineWidth(3f);
        set1.setValueTextSize(10f);
        set1.setValueTextColor(Color.BLUE);

//        LineDataSet set2 = new LineDataSet(yValues, "info");

//        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
//        dataSets.add(set2);


        LineData data = new LineData(dataSets);
        mChart.setData(data);
    }

}