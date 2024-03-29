package com.example.blacklist.ui.CallLogModel;

public class CallLogItem {
    public String phoneName ;
    public String phoneNumber ;
    public String callType ;

    public CallLogItem(String phoneName, String phoneNumber, String callType) {
        this.phoneNumber = phoneNumber ;
        this.phoneName = phoneName ;
        this.callType = callType ;
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

    public String getCallType() {
        return callType;
    }
}

