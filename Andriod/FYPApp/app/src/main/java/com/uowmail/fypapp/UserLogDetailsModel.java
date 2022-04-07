package com.uowmail.fypapp;

import com.google.firebase.Timestamp;

import java.util.Map;

public class UserLogDetailsModel {

    private Timestamp date;
    private String disk_read;
    private String disk_write;
    private Map<String, Long> idling;
    private String net_recv;
    private String net_send;
    private Map<String, Long> sys;
    private Map<String, Long> usr;

    public UserLogDetailsModel(){}

    public UserLogDetailsModel(Timestamp date, String disk_read,
                               String disk_write, Map<String, Long> idling, String net_recv,
                               String net_send, Map<String, Long> sys, Map<String, Long> usr) {
        this.date = date;
        this.disk_read = disk_read;
        this.disk_write = disk_write;
        this.idling = idling;
        this.net_recv = net_recv;
        this.net_send = net_send;
        this.sys = sys;
        this.usr = usr;
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

    public Map<String, Long> getIdling() {
        return idling;
    }

    public void setIdling(Map<String, Long> idling) {
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

    public Map<String, Long> getSys() {
        return sys;
    }

    public void setSys(Map<String, Long> sys) {
        this.sys = sys;
    }

    public Map<String, Long> getUsr() {
        return usr;
    }

    public void setUsr(Map<String, Long> usr) {
        this.usr = usr;
    }

}
