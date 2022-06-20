package com.example.blacklist.ui.Contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blacklist.MainActivity;
import com.example.blacklist.R;
import com.example.blacklist.databinding.FragmentContactBinding;
import com.example.blacklist.ui.callLogModel.CallLogAdapter;
import com.example.blacklist.ui.callLogModel.CallLogItem;

import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends Fragment {

    private FragmentContactBinding binding;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ContactViewModel contactViewModel =
                new ViewModelProvider(this).get(ContactViewModel.class);

        binding = FragmentContactBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.recyclerView;

        contactViewModel.getContactListLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<ContactModel>>() {
            @Override
            public void onChanged(ArrayList<ContactModel> contactList) {
                ContactAdapter contactAdapter = new ContactAdapter(contactList);
                recyclerView.setAdapter(contactAdapter);
            }
        });


        MainActivity activity = (MainActivity) getActivity();
        activity.getContactList();
        contactViewModel.setContactList(activity.getMyContactList());
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}