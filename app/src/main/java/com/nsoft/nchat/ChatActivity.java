package com.nsoft.nchat;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nsoft.nchat.databinding.ActivityChatBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private SharedPreferences sharedPreferences;
    private List<MessageModel> messages;
    private MyAdapter adapter;
    private String sender = "";
    private Socket socket;

    {
        try {
            String url = "http://pial.nsoftcompany.xyz/";
            socket = IO.socket(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

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
        sharedPreferences = getSharedPreferences(String.valueOf(R.string.app_name), MODE_PRIVATE);

        sender = sharedPreferences.getString("name", "Anonymous");
        messages = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);

        adapter = new MyAdapter(ChatActivity.this, messages, sender);
        binding.recyclerView.setAdapter(adapter);

        socket.on("received_message", onMessage);

        binding.sentButton.setOnClickListener(v -> {
            String message = binding.editText.getText().toString().trim();
            if (!message.isEmpty()) {
                JSONObject messageBundle = new JSONObject();
                try {
                    messageBundle.put("sender", sender);
                    messageBundle.put("receiver", "web");
                    messageBundle.put("message", message);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                socket.emit("send_message", messageBundle);
                binding.editText.setText("");
            }
        });


    }

    // on create end here --------------------------------------------------------------------------
    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject messageBundle = (JSONObject) args[0];
            try {
                String sender = messageBundle.getString("sender");
                String receiver = messageBundle.getString("receiver");
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


    @Override
    protected void onStart() {
        super.onStart();
        socket.connect();
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