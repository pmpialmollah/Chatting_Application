package com.nsoft.nchat;

import android.app.Application;
import android.content.ContentProvider;
import android.os.Build;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SocketManager.getInstance().connect();
    }
}
