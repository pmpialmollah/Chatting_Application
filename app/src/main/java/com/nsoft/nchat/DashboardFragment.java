package com.nsoft.nchat;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
    private SharedPreferences sharedPreferences;
    private ConversationsAdapter myAdapter;
    private MyMethodsClass myMethodsClass;
    private List<ConversationModel> receiverList;
    private String userId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", null);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // my code here ----------------------------------------------------------------------------
        myMethodsClass = new MyMethodsClass(getContext());
        receiverList = new ArrayList<>();

        binding.progressBar.setVisibility(View.VISIBLE);

        myAdapter = new ConversationsAdapter(getActivity(), receiverList);
        binding.chatListRecyclerView.setAdapter(myAdapter);
        binding.chatListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.chatButton.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ChatActivity.class));
        });

        if (userId != null) {
            myMethodsClass.getConversationsList(userId, new MyMethodsClass.ResponseCallback() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    if (jsonObject != null) {
                        binding.progressBar.setVisibility(View.GONE);
                        boolean status = Boolean.parseBoolean(jsonObject.optString("status"));
                        if (status) {
                            receiverList.clear();
                            JSONArray jsonArray = jsonObject.optJSONArray("data");
                            if (jsonArray != null) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject conversation = jsonArray.optJSONObject(i);
                                    if (conversation != null) {
                                        String name = conversation.optString("name");
                                        String online_status = conversation.optString("online_status");
                                        String last_message = conversation.optString("last_message");
                                        String last_time = conversation.optString("last_time");

                                        String user_one = conversation.optString("user_one");
                                        String user_two = conversation.optString("user_two");

                                        String receiver_id = user_one.equals(userId) ? user_two : user_one;

                                        receiverList.add(new ConversationModel(receiver_id, name, online_status, last_time, last_message));
                                    }
                                }
                                myAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "No data found...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    binding.progressBar.setVisibility(View.GONE);
                    Log.d("Volley", "onError: " + error.toString());
                }
            });

        }
    }
    // on create end here --------------------------------------------------------------------------


    // ---------------------------------------------------------------------------------------------
}