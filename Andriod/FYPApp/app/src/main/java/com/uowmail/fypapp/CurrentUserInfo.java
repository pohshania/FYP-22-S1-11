package com.uowmail.fypapp;

import java.io.Serializable;

public class CurrentUserInfo implements Serializable {

    public String fullName;
    public String email;
    public String orgID;
    public boolean isAdmin;
    public boolean isActive;

    public CurrentUserInfo(String fullName, String email, String orgID, boolean isAdmin, boolean isActive) {
        this.fullName = fullName;
        this.email = email;
        this.orgID = orgID;
        this.isAdmin = isAdmin;
        this.isActive = isActive;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrgID() {
        return orgID;
    }

    public void setOrgID(String orgID) {
        this.orgID = orgID;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }




}
