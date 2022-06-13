package com.example.blacklist.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blacklist.MainActivity;
import com.example.blacklist.databinding.FragmentNotificationsBinding;
import com.example.blacklist.ui.callLogModel.CallLogItem;
import com.example.blacklist.ui.callLogModel.CallLogAdapter;

import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private RecyclerView revCallLog ;
    private CallLogAdapter callLogAdapter ;
    private NotificationsViewModel notificationsViewModel ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        revCallLog  = binding.CallLogRecycleView;

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        revCallLog.setLayoutManager(layoutManager);
        notificationsViewModel.getListCallLogLiveData().observe(getViewLifecycleOwner(), new Observer<List<CallLogItem>>() {
            @Override
            public void onChanged(List<CallLogItem> callLogs) {
                callLogAdapter = new CallLogAdapter(callLogs) ;
                revCallLog.setAdapter(callLogAdapter);
            }
        });

        MainActivity activity = (MainActivity) getActivity();
        activity.fetchCallLog();
        notificationsViewModel.setCallLog(activity.getMyCallLog());

        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}