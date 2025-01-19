package com.example.maptest;

import static com.example.maptest.Constants.DIRECTION_BROADCAST;
import static com.example.maptest.Constants.MAPS_PACKAGE;
import static com.example.maptest.Constants.NOTIFICATION_MONITOR_UNBIND;
import static com.example.maptest.Constants.NOTIFICATION_RECEIVED;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.NonNull;

public class NotificationMonitor extends NotificationListenerService {

    private static final String TAG = "NotificationMonitor";
    Context context;
    String notificationTitle = "";//title of previous notification
    private BindReceiver bindReceiver;

    @Override
    public void onListenerConnected() {
        Log.d(TAG, "onListenerConnected: Connected");
        super.onListenerConnected();
    }

    @Override
    public void onListenerDisconnected() {
        Log.d(TAG, "onListenerDisconnected: Disconnected");
        super.onListenerDisconnected();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        bindReceiver = new BindReceiver();
        IntentFilter unbindIntentFilter = new IntentFilter(NOTIFICATION_MONITOR_UNBIND);
        context.registerReceiver(bindReceiver, unbindIntentFilter, Context.RECEIVER_EXPORTED);
    }

    @Override
    public void onNotificationPosted(@NonNull StatusBarNotification sbn) {
        String pack = sbn.getPackageName();
        //Only process notifications from google maps app
        if (pack.equals(MAPS_PACKAGE)) {
            //Get the notification object
            Notification notification = sbn.getNotification();

            //To get the title and the Description text
            Bundle extras = notification.extras;

            //Extracting title, text and icon
            CharSequence titleCS = extras.getCharSequence("android.title");
            String title = titleCS == null ? "ANDROID_TITLE_NOT_FOUND" : titleCS.toString();
            CharSequence textCS = extras.getCharSequence("android.text");
            String text = textCS == null ? "ANDROID_TEXT_NOT_FOUND" : textCS.toString();
            Icon icon = notification.getLargeIcon();

            if (!notificationTitle.equals(title)) { // && icon != null
                notificationTitle = title;
                //create an intent object for broadcasting
                Intent msgrcv = createNotificationIntent(title, text, icon);

                //broadcast notification intent
                Log.d(TAG, "onNotificationPosted: title : " + title);
                Log.d(TAG, "onNotificationPosted: text : " + text);

                context.sendBroadcast(msgrcv);
                Log.d(TAG, "onNotificationPosted: broadcast " + title);
            }
//            else if (icon == null) {
//                Log.d(TAG, "onNotificationPosted: icon null for title" + title);
//                context.sendBroadcast(unknownNotificationIntent(title, text));
//            }
        }


    }

    Intent createNotificationIntent(String title, String text, Icon icon) {
        return new Intent(NOTIFICATION_RECEIVED)
                .putExtra("title", title)
                .putExtra("text", text)
                .putExtra("icon", icon);
    }

//    Intent unknownNotificationIntent(String title, String text) {
//        return new Intent(DIRECTION_BROADCAST)
//                .putExtra("newData", title + "\n" + text + "\n\n");
//    }

    class BindReceiver extends BroadcastReceiver {
        private static final String TAG = "NotificationMonitor:BindReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("action");
            if (action == null) {
                Log.d(TAG, "onReceive: received null action");
            } else if (action.equals("unbind")) {
                try {
                    NotificationMonitor.this.requestUnbind();
                } catch (Exception e) {
                    Log.d(TAG, "onReceive: un successful unbind");
                }
                Log.d(TAG, "onReceive: received unbind action");
            } else if (action.equals("rebind")) {
                NotificationMonitor.requestRebind(new ComponentName(context, NotificationMonitor.class));
                Log.d(TAG, "onReceive: received rebind action");
            } else {
                Log.d(TAG, "onReceive: received unknown action");
            }
        }
    }
}
