package com.example.maptest.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.maptest.MainActivity;

public class DirectionsNotificationChannel {
    private static final String TAG = "DirectionsNotificationChannel";
    private final NotificationManager notificationManager;
    public final String CHANNEL_ID = "naviBands maps push";
    public final String CHANNEL_NAME  = "directions_notification_channel";
    public final String CHANNEL_DESCRIPTION = "notification channel for navigation push";
    int notification_id = 0;

    public DirectionsNotificationChannel(Context context) {
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
        notificationChannel.setDescription(CHANNEL_DESCRIPTION);
        this.notificationManager = context.getSystemService(NotificationManager.class);
        this.notificationManager.createNotificationChannel(notificationChannel);
    }

    public void sendNotification(Context context, String title, String text, int smallIcon, Icon largeIcon) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText("Navigating : " + text)
                .setSmallIcon(smallIcon)
                .setLargeIcon(largeIcon)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .build();
        notificationManager.cancelAll();
        notificationManager.notify(notification_id, notification);
        notification_id += 1;
        Log.d(TAG, "notification sent: " + title);
    }
}
