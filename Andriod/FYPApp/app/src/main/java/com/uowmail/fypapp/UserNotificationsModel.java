package com.uowmail.fypapp;


public class UserNotificationsModel {

    private String document_id;

    // for firebase
    private UserNotificationsModel(){

    }

    // for us to receive data
    private UserNotificationsModel(String document_id){
        this.document_id = document_id;

    }


    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }
}
