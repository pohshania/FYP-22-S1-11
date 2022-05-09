package com.uowmail.fypapp;

import com.google.firebase.Timestamp;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAnalysisEngineDetailsModel {

    private ArrayList<String> anomaly;
    private Timestamp date;
    private Timestamp detection_range;
    private String detection_type;
    private String event_status;

    public UserAnalysisEngineDetailsModel(){

    }

    public UserAnalysisEngineDetailsModel(ArrayList<String> anomaly, Timestamp date, Timestamp detection_range, String detection_type, String event_status) {
        this.anomaly = anomaly;
        this.date = date;
        this.detection_range = detection_range;
        this.detection_type = detection_type;
        this.event_status = event_status;
    }

    public ArrayList<String> getAnomaly() {
        return anomaly;
    }

    public void setAnomaly(ArrayList<String>  anomaly) {
        this.anomaly = anomaly;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Timestamp getDetection_range() {
        return detection_range;
    }

    public void setDetection_range(Timestamp detection_range) {
        this.detection_range = detection_range;
    }

    public String getDetection_type() {
        return detection_type;
    }

    public void setDetection_type(String detection_type) {
        this.detection_type = detection_type;
    }

    public String getEvent_status() {
        return event_status;
    }

    public void setEvent_status(String event_status) {
        this.event_status = event_status;
    }


}
