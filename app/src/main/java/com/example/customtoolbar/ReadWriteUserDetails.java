package com.example.customtoolbar;

public class ReadWriteUserDetails {
    public String doB, gender, mobile;

    // No argument constructor
    public ReadWriteUserDetails() {

    }

    public String getDoB() {
        return doB;
    }

    public String getGender() {
        return gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setDoB(String doB) {
        this.doB = doB;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public ReadWriteUserDetails (String textDob, String textGender, String textMobile) {
        this.doB = textDob;
        this.gender = textGender;
        this.mobile = textMobile;
    }
}
