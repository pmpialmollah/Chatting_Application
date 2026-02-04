package com.nsoft.nchat;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.net.URISyntaxException;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {
    private static SocketManager instance;
    private Socket socket;
    private SharedPreferences sharedPreferences;

    private SocketManager(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE);
        String user_id = sharedPreferences.getString("user_id", "null");

        IO.Options options = new IO.Options();
        options.reconnection = true;
        options.reconnectionAttempts = Integer.MAX_VALUE;
        options.reconnectionDelay = 1000;

        options.auth = new HashMap<>();
        options.auth.put("user_id", user_id);


        try {
            socket = IO.socket(context.getString(R.string.socket_server_url), options);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized SocketManager getInstance(Context context) {
        if (instance == null) {
            instance = new SocketManager(context.getApplicationContext());
        }
        return instance;
    }

    public Socket getSocket() {
        return socket;
    }

    public void connect() {
        if (socket != null && !socket.connected()) {
            socket.connect();
        }
    }

    public void disconnect() {
        if (socket != null && socket.connected()) {
            socket.disconnect();
        }
    }
}
