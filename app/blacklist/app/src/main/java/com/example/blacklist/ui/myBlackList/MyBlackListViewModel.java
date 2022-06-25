package com.example.blacklist.ui.myBlackList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.blacklist.ui.CallLogModel.CallLogItem;
import com.example.blacklist.ui.myBlackList.MyBlackListModel.MyBlackListItem;

import java.util.ArrayList;
import java.util.List;

public class MyBlackListViewModel extends ViewModel {
    private final MutableLiveData<List<MyBlackListItem>> mMyBlackListLiveData;
    private List<MyBlackListItem> mMyBlackList ;
    public MyBlackListViewModel() {
        mMyBlackListLiveData = new MutableLiveData<>() ;
        initData() ;
    }

    private void initData() {
        mMyBlackList = new ArrayList<>() ;
        mMyBlackListLiveData.setValue(mMyBlackList);
    }

    public MutableLiveData<List<MyBlackListItem>> getmMyBlackListLiveData (){
        return mMyBlackListLiveData;
    }

    public void setMyBlackList(List<MyBlackListItem> myList) {
        mMyBlackList = myList;
        mMyBlackListLiveData.setValue(mMyBlackList) ;
    }
}