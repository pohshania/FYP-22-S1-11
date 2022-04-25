package com.uowmail.fypapp;

import com.google.firebase.Timestamp;

public class UserAnalysisEngineDetailsModel {
    private Timestamp date;
    private Timestamp detected_range;
    private String detected_by;
    private String detection_type;
    private String event_status;

    public UserAnalysisEngineDetailsModel(){

    }

    public UserAnalysisEngineDetailsModel(Timestamp date, Timestamp detected_range, String detected_by, String detection_type, String event_status) {
        this.date = date;
        this.detected_range = detected_range;
        this.detected_by = detected_by;
        this.detection_type = detection_type;
        this.event_status = event_status;
    }


    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Timestamp getDetected_range() {
        return detected_range;
    }

    public void setDetected_range(Timestamp detected_range) {
        this.detected_range = detected_range;
    }

    public String getDetected_by() {
        return detected_by;
    }

    public void setDetected_by(String detected_by) {
        this.detected_by = detected_by;
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
