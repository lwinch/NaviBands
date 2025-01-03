package com.example.maptest;

import static com.example.maptest.Constants.MAPS_PACKAGE;
import static com.example.maptest.Constants.NOTIFICATION_RECEIVED;
import static com.example.maptest.Constants.REROUTING;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashSet;

public class NotificationMonitor extends NotificationListenerService {

    private static final String TAG = "NotificationMonitor";
    //static Drawable currIcon = null;
    Context context;
    String notificationTitle = "";//title of previous notification
    HashSet<String> titleSet;
    public static NotificationMonitor notificationMonitor;

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
        titleSet = new HashSet<>();
        notificationMonitor = this;
    }

    @Override
    public void onNotificationPosted(@NonNull StatusBarNotification sbn) {
        if (sbn != null) {
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
                //String type = REROUTING.equals(title) ? REROUTING : title;

                if (!notificationTitle.equals(title)) {
                    notificationTitle = title;
                    //create an intent object for broadcasting
                    Intent msgrcv = createNotificationIntent(title, text, icon);

                    //broadcast notification intent
                    Log.d(TAG, "onNotificationPosted: title : " + title);
                    Log.d(TAG, "onNotificationPosted: text : " + text);
//                    Log.d(TAG, "onNotificationPosted: type : " + type);

//                    LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
                    context.sendBroadcast(msgrcv);
                    Log.d(TAG, "onNotificationPosted: broadcast " + title);
                }
            }


        }
    }

    Intent createNotificationIntent(String title, String text, Icon icon) {
        Intent intent = new Intent(NOTIFICATION_RECEIVED)
                //.putExtra("type", type)
                .putExtra("title", title)
                .putExtra("text", text)
                .putExtra("icon", icon);
//        if (!REROUTING.equals(title)) {
//            intent.putExtra("icon", icon);
//        } else {
//            Log.d(TAG, "createNotificationIntent: Icon NULL");
//        }
        return intent;
    }
}
