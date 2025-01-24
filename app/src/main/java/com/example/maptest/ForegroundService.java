package com.example.maptest;

import static com.example.maptest.Constants.NOTIFICATION_RECEIVED;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.maptest.broadcastReceivers.MapsNotificationBroadcastReceiver;
import com.example.maptest.notifications.ForegroundNotificationChannel;
import com.example.maptest.settings.PushRadioOption;

import jashgopani.github.io.mibandsdk.MiBand;

/*
 * This class is for showing notifications while service is running
 * */
public class ForegroundService extends Service {
    private static final String TAG = "ForegroundService";
    private MapsNotificationBroadcastReceiver mapsNotificationBroadcastReceiver;
    MiBand miBand;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: Created Foreground Service");
        this.mapsNotificationBroadcastReceiver = new MapsNotificationBroadcastReceiver(this);
        ForegroundNotificationChannel.createChannel(this);
        IntentFilter notificationIntentFilter = new IntentFilter(NOTIFICATION_RECEIVED);
        getApplicationContext().registerReceiver(mapsNotificationBroadcastReceiver, notificationIntentFilter, Context.RECEIVER_EXPORTED);
        miBand = MiBand.getInstance(ForegroundService.this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Started Foreground Service");
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        Log.d(TAG, "onStartCommand: " + title + " | " + text);
        stopForeground(flags);
        int currentMinorThreshold = intent.getIntExtra("currentMinorThreshold", 500);
        double currentMajorThreshold = intent.getDoubleExtra("currentMajorThreshold", 1.);
        PushRadioOption pushOption = PushRadioOption.fromString(intent.getStringExtra("pushNotificationSetting"));
        this.mapsNotificationBroadcastReceiver.updateSettings(currentMinorThreshold, currentMajorThreshold, pushOption);
        String notificationText = text  + "\nDistance threshold: " + currentMinorThreshold;
        Notification notification = ForegroundNotificationChannel.getForegroundNotification(this, title, notificationText);
        startForeground(420, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Destroying Foreground service");
        getApplicationContext().unregisterReceiver(mapsNotificationBroadcastReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: Not using this method");
        return null;
    }
}
