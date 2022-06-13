package com.example.blacklist.ui.notifications;

import static android.app.PendingIntent.getActivity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.blacklist.MainActivity;
import com.example.blacklist.ui.callLogModel.CallLogItem;

import java.util.ArrayList;
import java.util.List;

public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<List<CallLogItem>> mListCallLogLiveData;
    private List<CallLogItem> mListCallLog ;
    public NotificationsViewModel() {
        mListCallLogLiveData = new MutableLiveData<>() ;
        initData() ;
    }

    private void initData() {
        mListCallLog = new ArrayList<>() ;
        // get call logs

        //

        mListCallLogLiveData.setValue(mListCallLog);
    }

    public MutableLiveData<List<CallLogItem>> getListCallLogLiveData() {
        return mListCallLogLiveData;
    }

    public void setCallLog(List<CallLogItem> callLogs) {
        mListCallLog = callLogs;
        mListCallLogLiveData.setValue(mListCallLog) ;
    }

}