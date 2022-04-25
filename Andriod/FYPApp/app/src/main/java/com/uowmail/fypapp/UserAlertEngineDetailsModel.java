package com.uowmail.fypapp;

import com.google.firebase.Timestamp;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class UserAlertEngineDetailsModel {

    private ArrayList<String> abnormal;
    private Timestamp date;

    private String detected_by;
    private String disk_read;
    private String disk_write;
    private String event_status;
    private Map<String, Float> idling;
    private String net_recv;
    private String net_send;
    private Map<String, Float> sys;
    private Map<String, Float> usr;

    public UserAlertEngineDetailsModel(){}

    public UserAlertEngineDetailsModel(ArrayList<String> abnormal, Timestamp date, String detected_by, String disk_read,
                                       String disk_write, String event_status, Map<String, Float> idling, String net_recv,
                                       String net_send, Map<String, Float> sys, Map<String, Float> usr) {
        this.abnormal = abnormal;
        this.date = date;
        this.detected_by = detected_by;
        this.disk_read = disk_read;
        this.disk_write = disk_write;
        this.event_status = event_status;
        this.idling = idling;
        this.net_recv = net_recv;
        this.net_send = net_send;
        this.sys = sys;
        this.usr = usr;
    }

    public ArrayList<String> getAbnormal() {
        return abnormal;
    }

    public void setAbnormal(ArrayList<String> abnormal) {
        this.abnormal = abnormal;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getDisk_read() {
        return disk_read;
    }

    public void setDisk_read(String disk_read) {
        this.disk_read = disk_read;
    }

    public String getDisk_write() {
        return disk_write;
    }

    public void setDisk_write(String disk_write) {
        this.disk_write = disk_write;
    }

    public Map<String, Float> getIdling() {
        return idling;
    }

    public void setIdling(Map<String, Float> idling) {
        this.idling = idling;
    }

    public String getNet_recv() {
        return net_recv;
    }

    public void setNet_recv(String net_recv) {
        this.net_recv = net_recv;
    }

    public String getNet_send() {
        return net_send;
    }

    public void setNet_send(String net_send) {
        this.net_send = net_send;
    }

    public Map<String, Float> getSys() {
        return sys;
    }

    public void setSys(Map<String, Float> sys) {
        this.sys = sys;
    }

    public Map<String, Float> getUsr() {
        return usr;
    }

    public void setUsr(Map<String, Float> usr) {
        this.usr = usr;
    }

    public String getDetected_by() {
        return detected_by;
    }

    public void setDetected_by(String detected_by) {
        this.detected_by = detected_by;
    }

    public String getEvent_status() {
        return event_status;
    }

    public void setEvent_status(String event_status) {
        this.event_status = event_status;
    }


}
