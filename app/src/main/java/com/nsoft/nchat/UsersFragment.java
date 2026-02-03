package com.nsoft.nchat;

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
import com.nsoft.nchat.databinding.FragmentUsersBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {
    private FragmentUsersBinding binding;
    private UsersListAdapter myAdapter;
    private List<UserModelClass> usersList;
    private MyMethodsClass myMethodsClass;
    private UserModelClass userModelClass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(inflater, container, false);

        usersList = new ArrayList<>();
        myAdapter = new UsersListAdapter(getActivity(), usersList);
        myMethodsClass = new MyMethodsClass(getContext());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // my code here ----------------------------------------------------------------------------
        binding.progressBar.setVisibility(View.VISIBLE);
        getAllUsers();
        binding.userRecyclerView.setAdapter(myAdapter);
        binding.userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void getAllUsers() {
        myMethodsClass.allUsersList(new MyMethodsClass.JsonArrayCallback() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                if (jsonArray != null && jsonArray.length() > 0) {
                    usersList.clear();
                    binding.progressBar.setVisibility(View.GONE);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject person = jsonArray.optJSONObject(i);
                        if (person != null && person.length() > 0) {

                            String name = person.optString("name");
                            String online_status = person.optString("online_status");
                            String last_seen = person.optString("last_seen");
                            String bio = person.optString("bio");
                            String verification_badge = person.optString("verification_badge");

                            userModelClass = new UserModelClass(name, online_status, last_seen, bio, verification_badge);
                            usersList.add(userModelClass);
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getContext(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}