package com.uowmail.fypapp;


import java.util.Map;

public class UserLogsModel {

    private String document_id;

    // for firebase
    private UserLogsModel(){

    }

    // for us to receive data
    private UserLogsModel(String document_id){
        this.document_id = document_id;

    }


    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }
}
