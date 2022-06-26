package com.example.blacklist.ui.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.blacklist.ui.myBlackList.MyBlackListModel.MyBlackListItem;
import com.example.blacklist.ui.profile.ProfileModel.ProfileItem;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<List<ProfileItem>> mProfileLiveData;
    private List<ProfileItem> mProfileList ;
    public ProfileViewModel() {
        mProfileLiveData = new MutableLiveData<>() ;
        initData() ;
    }

    private void initData() {
        mProfileList = new ArrayList<>() ;
        mProfileLiveData.setValue(mProfileList);
    }

    public MutableLiveData<List<ProfileItem>> getmProfileLiveData (){
        return mProfileLiveData;
    }

    public void setProfileList(List<ProfileItem> myList) {
        mProfileList = myList;
        mProfileLiveData.setValue(mProfileList) ;
    }
    public void addProfileList (ProfileItem profileItem) {
        if (profileItem == null || profileItem.getSubcribeItem().equals("")) {
            return;
        }
        mProfileList.add(profileItem) ;
        mProfileLiveData.setValue(mProfileList);
    }
}