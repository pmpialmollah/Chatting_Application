package com.nsoft.nchat;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.nsoft.nchat.databinding.ActivityChatBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private MyMethodsClass myMethodsClass;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private List<MessageModel> messages;
    private MyAdapter adapter;
    private Socket socket;
    private String userId;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // initialize here -------------------------------------------------------------------------
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        // -----------------------------------------------------------------------------------------

        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());                                  // my code -------------
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        // my code starts here ---------------------------------------------------------------------
        myMethodsClass = new MyMethodsClass(getApplicationContext());
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String sender = sharedPreferences.getString("name", "Anonymous");
        userId = sharedPreferences.getString("user_id", "null");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_colour));
        }

        IO.Options options = new IO.Options();
        options.auth = new HashMap<>();
        options.auth.put("user_id", sender);
        try {
            socket = IO.socket("https://pial.nsoftcompany.xyz/", options);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        messages = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);

        adapter = new MyAdapter(ChatActivity.this, messages, userId);
        binding.recyclerView.setAdapter(adapter);


        socket.on("received_message", onMessage);
        socket.on("typing", onTyping);

        binding.sentButton.setOnClickListener(v -> {
            String message = binding.editText.getText().toString().trim();
            if (!message.isEmpty()) {
                JSONObject messageBundle = new JSONObject();
                try {
                    messageBundle.put("sender_id", userId);
                    messageBundle.put("sender_name", sender);
                    messageBundle.put("receiver_id", "others");
                    messageBundle.put("receiver_name", "Web");
                    messageBundle.put("message", message);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                socket.emit("send_message", messageBundle);
                binding.editText.setText("");
            }
        });

        binding.logoutButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm logout")
                    .setMessage("Do you really want to log out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        editor.putBoolean("is_logged_in", false).apply();
                        editor.putString("user_id", "");
                        startActivity(new Intent(ChatActivity.this, SignInActivity.class));
                        finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });


    }

    // on create end here --------------------------------------------------------------------------
    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject messageBundle = (JSONObject) args[0];
            try {
                String sender = messageBundle.getString("sender_id");
                String receiver = messageBundle.getString("receiver_id");
                String message = messageBundle.getString("message");

                runOnUiThread(() -> {
                    messages.add(new MessageModel(sender, receiver, message));
                    adapter.notifyItemInserted(messages.size() - 1);
                    binding.recyclerView.scrollToPosition(messages.size() - 1);
                });
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
    };

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0) {

                String response = args[0].toString();
                boolean isTyping = Boolean.parseBoolean(response);

                Log.d("RESPONSE", "call: " + response);
                runOnUiThread(() -> {
                    if (isTyping) {
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(getApplicationContext(), "Someone is typing...", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                    } else {
                        if (toast != null) {
                            toast.cancel();
                        }

                        toast = Toast.makeText(getApplicationContext(), "Typing end.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });

            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        socket.connect();

        myMethodsClass.userDetailsPostRequest(userId, new MyMethodsClass.ResponseCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                boolean status = Boolean.parseBoolean(jsonObject.optString("status"));
                if (status) {
                    String name = jsonObject.optString("name");
                    binding.nameTextView.setText(name);
                } else {
                    Toast.makeText(ChatActivity.this, "Somethings went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(ChatActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        socket.disconnect();
    }
}