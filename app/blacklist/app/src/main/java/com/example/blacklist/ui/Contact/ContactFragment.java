package com.example.blacklist.ui.Contact;

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
import com.example.blacklist.databinding.FragmentContactBinding;

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

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        contactViewModel.getContactListLiveData().observe(getViewLifecycleOwner(), new Observer<List<ContactModel>>() {
            @Override
            public void onChanged(List<ContactModel> contactList) {
                ContactAdapter contactAdapter = new ContactAdapter(contactList,getActivity());
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