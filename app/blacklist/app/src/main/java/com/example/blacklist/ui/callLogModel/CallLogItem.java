package com.example.blacklist.ui.callLogModel;

public class CallLogItem {
    public String phoneName ;
    public String phoneNumber ;

    public CallLogItem(String phoneName, String phoneNumber) {
        this.phoneNumber = phoneNumber ;
        this.phoneName = phoneName ;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }
}

