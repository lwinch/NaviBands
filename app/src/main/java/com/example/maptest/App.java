package com.example.maptest;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

public class App extends Application {
    public static final String FOREGROUND_CHANNEL_ID = "Process Received Notifications";
    protected static Context context = null;
    protected static final int DEVICE_CONNECTED = 167;
    protected static final int DEVICE_DISCONNECTED = 546;
    protected static final int DEVICE_NULL = 938;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        context = getApplicationContext();
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                FOREGROUND_CHANNEL_ID,
                "Display foreground service notification",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);

    }

}
