package com.example.blacklist.ui.Contact;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ContactViewModel extends ViewModel {

    private final MutableLiveData<List<ContactModel>> mContactListLiveData;
    private List<ContactModel> mContactList;

    public ContactViewModel() {
        mContactListLiveData = new MutableLiveData<>();
        initData();
    }

    private void initData() {
        mContactList = new ArrayList<>();

        //set value
        mContactListLiveData.setValue(mContactList);
    }

    public MutableLiveData<List<ContactModel>> getContactListLiveData() {
        return mContactListLiveData;
    }

    public void setContactList(List<ContactModel> contactList) {
        mContactList = contactList;
        mContactListLiveData.setValue(mContactList);
    }
}