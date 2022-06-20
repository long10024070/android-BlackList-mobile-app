package com.example.blacklist.ui.Contact;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.blacklist.ui.callLogModel.CallLogItem;

import java.util.ArrayList;
import java.util.List;

public class ContactViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<ContactModel>> mContactListLiveData;
    private ArrayList<ContactModel> mContactList;

    public ContactViewModel() {
        mContactListLiveData = new MutableLiveData<>();
        initData();
    }

    private void initData() {
        mContactList = new ArrayList<>();

        //set value
        mContactListLiveData.setValue(mContactList);
    }

    public MutableLiveData<ArrayList<ContactModel>> getContactListLiveData() {
        return mContactListLiveData;
    }

    public void setContactList(ArrayList<ContactModel> contactList) {
        mContactList = contactList;
        mContactListLiveData.setValue(mContactList);
    }
}