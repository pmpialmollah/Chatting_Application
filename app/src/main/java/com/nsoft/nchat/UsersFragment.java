package com.nsoft.nchat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nsoft.nchat.databinding.FragmentUsersBinding;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {
    private FragmentUsersBinding binding;
    private ChatListAdapter myAdapter;
    private List<String> myList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(inflater, container, false);

        myList = new ArrayList<>();
        myAdapter = new ChatListAdapter(getActivity(), myList);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // my code here ----------------------------------------------------------------------------

        binding.userRecyclerView.setAdapter(myAdapter);
        binding.userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }
}