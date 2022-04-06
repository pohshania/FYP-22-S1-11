package com.uowmail.fypapp;


import java.util.Map;

public class UserLogsModel {

    private String disk_read;
    private String disk_write;
    private Map<String, Long> idling;
    private String net_recv;
    private Map<String, Long> sys;
    private Map<String, Long> usr;

    // for firebase
    private UserLogsModel(){

    }

    // for us to receive data
    private UserLogsModel(String disk_read, String disk_write,
                          Map<String, Long> idling, String net_recv,
                          Map<String, Long> sys, Map<String, Long> usr){
        this.disk_read = disk_read;
        this.disk_write = disk_write;
        this.idling = idling;
        this.net_recv = net_recv;
        this.sys = sys;
        this.usr = usr;

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
