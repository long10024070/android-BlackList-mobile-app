package com.example.blacklist.ui.profile;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.blacklist.R;
import com.example.blacklist.Telephone.BlackList;
import com.example.blacklist.databinding.FragmentMyBlackListBinding;
import com.example.blacklist.databinding.FragmentProfileBinding;
import com.example.blacklist.ui.myBlackList.MyBlackListModel.MyBlackListAdapter;
import com.example.blacklist.ui.myBlackList.MyBlackListModel.MyBlackListItem;
import com.example.blacklist.ui.myBlackList.MyBlackListViewModel;
import com.example.blacklist.ui.profile.ProfileModel.ProfileAdapter;
import com.example.blacklist.ui.profile.ProfileModel.ProfileItem;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private RecyclerView revProfileList ;
    private ProfileAdapter profileAdapter ;
    private ProfileViewModel profileViewModel ;
    private List<ProfileItem> profileList ;

    private Button AddButton ;
    private EditText subcribeEnterItem ;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        profileList = new ArrayList<ProfileItem>() ;
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class) ;

        binding = FragmentProfileBinding.inflate(inflater,container,false) ;
        View root = binding.getRoot() ;
        revProfileList = binding.profileView ;

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity()) ;
        revProfileList.setLayoutManager(layoutManager);

        profileViewModel.getmProfileLiveData().observe(getViewLifecycleOwner(), new Observer<List<ProfileItem>>() {
            @Override
            public void onChanged(List<ProfileItem> profileItems) {
                profileAdapter = new ProfileAdapter(profileItems, getActivity()) ;
                revProfileList.setAdapter(profileAdapter);
            }
        });

        //get black list test
        List <String> numberList = BlackList.getInstance(getActivity()).getSubcribeNumbers();
        for (int i = 0 ; i < numberList.size() ; ++i) {
            profileList.add(new ProfileItem(numberList.get(i))) ;
        }
        profileViewModel.setProfileList(profileList);


        //When click add button
        AddButton = binding.addButton ;
        subcribeEnterItem = binding.subcribeEditText ;

        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileViewModel.addProfileList(new ProfileItem(subcribeEnterItem.getText().toString())); ;
                Toast.makeText(getActivity(),"add", Toast.LENGTH_SHORT).show();
            }
        });
        //-------

        return root ;
    }

}