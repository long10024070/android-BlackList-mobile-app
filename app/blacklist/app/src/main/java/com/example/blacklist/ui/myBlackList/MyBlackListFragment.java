package com.example.blacklist.ui.myBlackList;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blacklist.R;
import com.example.blacklist.Telephone.BlackList;
import com.example.blacklist.databinding.FragmentMyBlackListBinding;
import com.example.blacklist.databinding.FragmentNotificationsBinding;
import com.example.blacklist.ui.CallLogModel.CallLogAdapter;
import com.example.blacklist.ui.CallLogModel.CallLogItem;
import com.example.blacklist.ui.myBlackList.MyBlackListModel.MyBlackListAdapter;
import com.example.blacklist.ui.myBlackList.MyBlackListModel.MyBlackListItem;
import com.example.blacklist.ui.notifications.NotificationsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyBlackListFragment extends Fragment {

    private FragmentMyBlackListBinding binding;
    private RecyclerView revMyBlackList ;
    private MyBlackListAdapter myBlackListAdapter ;
    private MyBlackListViewModel myBlackListViewModel ;
    private List<MyBlackListItem> myBlackList ;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        myBlackList = new ArrayList<MyBlackListItem>() ;
        myBlackListViewModel = new ViewModelProvider(this).get(MyBlackListViewModel.class) ;

        binding = FragmentMyBlackListBinding.inflate(inflater,container,false) ;
        View root = binding.getRoot() ;
        revMyBlackList = binding.myBlackListView ;

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity()) ;
        revMyBlackList.setLayoutManager(layoutManager);

        myBlackListViewModel.getmMyBlackListLiveData().observe(getViewLifecycleOwner(), new Observer<List<MyBlackListItem>>() {
            @Override
            public void onChanged(List<MyBlackListItem> myBlackListItems) {
                myBlackListAdapter = new MyBlackListAdapter(myBlackListItems, getActivity()) ;
                revMyBlackList.setAdapter(myBlackListAdapter);
            }
        });

        //get black list test
        List <String> numberList = BlackList.getInstance(getActivity()).getMyBlackListNumbers();
        for (int i = 0 ; i < numberList.size() ; ++i) {
            myBlackList.add(new MyBlackListItem(numberList.get(i))) ;
        }
        myBlackListViewModel.setMyBlackList(myBlackList);
        //-----


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}