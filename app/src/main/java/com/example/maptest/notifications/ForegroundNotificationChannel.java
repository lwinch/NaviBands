package com.example.maptest.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.maptest.MainActivity;
import com.example.maptest.R;

public class ForegroundNotificationChannel {
    private static final String FOREGROUND_CHANNEL_ID = "foreground_notification_channel";

    public static void createChannel(Context context) {
        NotificationChannel serviceChannel = new NotificationChannel(
                FOREGROUND_CHANNEL_ID,
                "Display foreground service notification",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(serviceChannel);
    }

    public static Notification getForegroundNotification(Context context, String title, String text) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(context, FOREGROUND_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .build();
    }

}
