package com.nsoft.nchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.nsoft.nchat.databinding.ActivityDashboardBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

public class DashboardActivity extends AppCompatActivity {
    private ActivityDashboardBinding binding;
    private SharedPreferences sharedPreferences;
    private Socket socket;
    private MyMethodsClass myMethodsClass;
    private String userId;
    private List<String> receiverList;
    private ChatListAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());        // my code ---------
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());                                      // my code ---------
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        // my code starts here ---------------------------------------------------------------------
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        myMethodsClass = new MyMethodsClass(getApplicationContext());
        receiverList = new ArrayList<>();

        userId = sharedPreferences.getString("user_id", "null");

        try {
            socket = IO.socket("https://pial.nsoftcompany.xyz/");
            socket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_colour));
        }

        myAdapter = new ChatListAdapter(DashboardActivity.this, receiverList);
        binding.chatListRecyclerView.setAdapter(myAdapter);
        binding.chatListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.chatButton.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, ChatActivity.class));
        });

        binding.allUsersButton.setOnClickListener(v -> {
            startActivity(new Intent(this, UserlistActivity.class));
        });

    }   // on create ends here ---------------------------------------------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        myMethodsClass.getChatList(userId, new MyMethodsClass.JsonArrayCallback() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                if (jsonArray != null && jsonArray.length() > 0) {
                    receiverList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        if (jsonObject != null) {
                            String receiverName = jsonObject.optString("receiver_name");
                            receiverList.add(receiverName);
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(DashboardActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }
}   // main class ends here ------------------------------------------------------------------------