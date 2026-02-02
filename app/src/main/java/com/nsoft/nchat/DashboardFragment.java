package com.nsoft.nchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.nsoft.nchat.databinding.FragmentDashboardBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private ChatListAdapter myAdapter;
    private MyMethodsClass myMethodsClass;
    private List<String> receiverList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // my code here ----------------------------------------------------------------------------
        myMethodsClass = new MyMethodsClass(getContext());
        receiverList = new ArrayList<>();

        myAdapter = new ChatListAdapter(getActivity(), receiverList);
        binding.chatListRecyclerView.setAdapter(myAdapter);
        binding.chatListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.chatButton.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ChatActivity.class));
        });

    }
    // on create end here --------------------------------------------------------------------------



    // ---------------------------------------------------------------------------------------------
}