package com.nsoft.nchat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nsoft.nchat.databinding.ActivityDashboardBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class DashboardActivity extends AppCompatActivity {
    private ActivityDashboardBinding binding;
    private SharedPreferences sharedPreferences;
    private Socket socket;
    private List<String> activeUsersList;
    private MyListAdapter myListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());        // my code ---------
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());                                      // my code ---------
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // my code starts here ---------------------------------------------------------------------
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        String userName = sharedPreferences.getString("name", "");

        IO.Options options = new IO.Options();
        options.auth = new HashMap<>();
        options.auth.put("username", userName);
        try {
            socket = IO.socket("https://pial.nsoftcompany.xyz/", options);
            socket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        activeUsersList = new ArrayList<>();

        socket.on("activeUsers", onActiveUsers);

        myListAdapter = new MyListAdapter();
        binding.activeUsersRecyclerView.setAdapter(myListAdapter);
        binding.activeUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this));


    }   // on create ends here ---------------------------------------------------------------------

    private Emitter.Listener onActiveUsers = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONArray userList = (JSONArray) args[0];
            if (userList == null || userList.length() == 0) {
                return;
            }
            runOnUiThread(() -> {
                activeUsersList.clear();
                for (int i = 0; i < userList.length(); i++) {
                    JSONObject user = userList.optJSONObject(i);
                    if (user != null) {
                        String name = user.optString("username");
                        activeUsersList.add(name);
                    }
                }
                myListAdapter.notifyDataSetChanged();
            });
        }
    };


    public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyViewHolder> {
        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView userNameTextView;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                userNameTextView = itemView.findViewById(R.id.userNameTextView);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View myView = getLayoutInflater().inflate(R.layout.user_list_layout, parent, false);
            return new MyViewHolder(myView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            String userName = activeUsersList.get(position);
            holder.userNameTextView.setText(userName);
        }

        @Override
        public int getItemCount() {
            return activeUsersList.size();
        }

    }

}   // main class ends here ------------------------------------------------------------------------