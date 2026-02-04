package com.nsoft.nchat;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nsoft.nchat.databinding.ActivityChatBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private MyMethodsClass myMethodsClass;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private List<MessageModel> messages;
    private List<MessageModel> privateMessages;
    private MyAdapter adapter;
    private PrivateMessageAdapter privateMessageAdapter;
    private Socket socket;
    private String userId;
    private boolean previousTrue = false;
    private Handler typingHandler = new Handler(Looper.getMainLooper());
    private Runnable typingEndRunnable;
    private String receiver_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        binding = ActivityChatBinding.inflate(getLayoutInflater());         // my code -------------
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
        userId = sharedPreferences.getString("user_id", null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_colour));
        }

        String receiver_name = getIntent().getStringExtra("receiver_name");
        receiver_user_id = getIntent().getStringExtra("receiver_user_id");

        if (receiver_name != null && receiver_user_id != null) {
            binding.nameTextView.setText(receiver_name);
        }

        socket = SocketManager.getInstance(getApplicationContext()).getSocket();

        messages = new ArrayList<>();
        privateMessages = new ArrayList<>();


        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d("SOCKET", "Connected: " + socket.id());
        });

        socket.on(Socket.EVENT_DISCONNECT, args -> {
            Log.d("SOCKET", "Disconnected");
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
            Log.e("SOCKET", "Connect error: " + args[0]);
        });

        if (receiver_user_id != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setStackFromEnd(true);
            binding.recyclerView.setLayoutManager(layoutManager);

            privateMessageAdapter = new PrivateMessageAdapter(ChatActivity.this, privateMessages, userId);
            binding.recyclerView.setAdapter(privateMessageAdapter);

            binding.sentButton.setOnClickListener(v -> {
                String message = binding.editText.getText().toString().trim();
                if (!message.isEmpty()) {
                    JSONObject messageBundle = new JSONObject();
                    try {
                        messageBundle.put("sender_id", userId);
                        messageBundle.put("receiver_id", receiver_user_id);
                        messageBundle.put("message", message);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    socket.emit("private_message", messageBundle);
                    binding.editText.setText("");
                }
            });
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setStackFromEnd(true);
            binding.recyclerView.setLayoutManager(layoutManager);

            adapter = new MyAdapter(ChatActivity.this, messages, userId);
            binding.recyclerView.setAdapter(adapter);

            binding.sentButton.setOnClickListener(v -> {
                String message = binding.editText.getText().toString().trim();
                if (!message.isEmpty()) {
                    JSONObject messageBundle = new JSONObject();
                    try {
                        messageBundle.put("sender_id", userId);
                        messageBundle.put("receiver_id", "others");
                        messageBundle.put("message", message);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    socket.emit("send_message", messageBundle);
                    binding.editText.setText("");
                }
            });
        }

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

        binding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                socket.emit("typing");

                if (typingEndRunnable != null) {
                    typingHandler.removeCallbacks(typingEndRunnable);
                }
                typingEndRunnable = () -> {
                    socket.emit("typingend");
                };

                typingHandler.postDelayed(typingEndRunnable, 2000);
            }
        });

    }

    // on create end here --------------------------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        if (socket.connected()) {
            socket.on("received_message", onMessage);
            socket.on("typing", onTyping);
            socket.on("private_message", onPrivateMessage);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        socket.off("received_message", onMessage);
        socket.off("typing", onTyping);
        socket.off("private_message", onPrivateMessage);
    }

    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject messageBundle = (JSONObject) args[0];
            try {
                String sender_id = messageBundle.getString("sender_id");
                String receiver_id = messageBundle.getString("receiver_id");
                String message = messageBundle.getString("message");

                runOnUiThread(() -> {
                    messages.add(new MessageModel(sender_id, receiver_id, message));
                    adapter.notifyItemInserted(messages.size() - 1);
                    binding.recyclerView.scrollToPosition(messages.size() - 1);
                });
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
    };

    private Emitter.Listener onPrivateMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject messageBundle = (JSONObject) args[0];
            Log.d("PIAL", "call: " + messageBundle.toString());
            try {
                String sender_id = messageBundle.getString("sender_id");
                String receiver_id = messageBundle.getString("receiver_id");
                String message = messageBundle.getString("message");

                runOnUiThread(() -> {
                    privateMessages.add(new MessageModel(sender_id, receiver_id, message));
                    privateMessageAdapter.notifyItemInserted(privateMessages.size() - 1);
                    binding.recyclerView.scrollToPosition(privateMessages.size() - 1);
                });
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
    };

    // typing indicator section --------------------------------------------------------------------
    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0) {

                String response = args[0].toString();
                boolean isTyping = Boolean.parseBoolean(response);

                Log.d("RESPONSE", "call: " + response);
                runOnUiThread(() -> {
                    if (receiver_user_id != null && privateMessageAdapter != null) {
                        privateMessageAdapter.showTypingIndication(isTyping);
                        binding.recyclerView.scrollToPosition(privateMessageAdapter.getItemCount() - 1);
                    } else if (adapter != null) {
                        adapter.showTypingIndication(isTyping);
                        binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    }

                });

            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}