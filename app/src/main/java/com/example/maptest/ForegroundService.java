package com.example.maptest;

import static com.example.maptest.Constants.DIRECTION_BROADCAST;
import static com.example.maptest.Constants.DIRECTION_UNKNOWN;
import static com.example.maptest.Constants.NOTIFICATION_RECEIVED;
import static com.example.maptest.Constants.REROUTING;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Icon;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Arrays;

/*
 * This class is for showing notifications while service is running
 * */
public class ForegroundService extends Service {
    private static final String TAG = "ForegroundService";
    //TODO: should this be static?
    private static NotificationReceiver notificationReceiver;
    Context context;
    NotificationManager notificationManager;
    NotificationChannel notificationChannel;
    final String CHANNEL_ID = "naviBands maps push";
    private int currentThreshold;
    int notification_id = 0;

    BroadcastReceiver test = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: Test received Notification");
        }
    };

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: Created Foreground Service");
        context = getApplicationContext();
        notificationReceiver = new NotificationReceiver();
        IntentFilter notificationIntentFilter = new IntentFilter(NOTIFICATION_RECEIVED);
        context.registerReceiver(notificationReceiver, notificationIntentFilter, Context.RECEIVER_EXPORTED);
        context.registerReceiver(test, notificationIntentFilter, Context.RECEIVER_NOT_EXPORTED);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Started Foreground Service");
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        Log.d(TAG, "onStartCommand: " + title + " | " + text);
        stopForeground(flags);
        currentThreshold = intent.getIntExtra("currentThreshold", 5);
        Notification notification = getForegroundNotification(this, title, text);
        startForeground(69, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        return START_STICKY;
    }

    private Notification getForegroundNotification(Context context, String title, String text) {
        Intent notificationIntent = new Intent(context, ForegroundService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text + "\nDistance threshold: " + currentThreshold)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .build();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Destroying Foreground service");
        context.unregisterReceiver(notificationReceiver);
        context.unregisterReceiver(test);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: Not using this method");
        return null;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        notificationChannel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this.
        notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private void sendNotification(Context context, String title, String text, int iconRes, Icon icon) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText("Navigating : " + text)
                .setSmallIcon(iconRes)
                .setLargeIcon(icon)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .build();
        // notificationId is a unique int for each notification that you must define.
        notificationManager.cancelAll();
        notificationManager.notify(notification_id, notification);
        notification_id += 1;
        Log.d(TAG, "notification sent");
    }

    class NotificationReceiver extends BroadcastReceiver {
        private static final String TAG = "NotificationReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: Received NOTIFICATION_RECEIVED intent");
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
            Icon icon = intent.getParcelableExtra("icon", Icon.class);

            //process the intent with pixel details and get result intent
            int iconRes = PixelProcessingService.getDirection(context, intent);
            StringBuilder newData = new StringBuilder();
            if (REROUTING.equals(title)) {
                Log.d(TAG, "onReceive: Intent is REROUTING");
                ForegroundService.this.sendNotification(context, title, text, iconRes, icon);
                newData.append(REROUTING).append("\n\n");
            }
            else if (iconRes == -1) {
                Log.d(TAG, "onReceive: Intent is ICON_NULL");
                newData.append(DIRECTION_UNKNOWN).append( "\n\n");
            }
            else {
                String direction = IconDataset.directionNames.get(iconRes);
                Log.d(TAG, "getDirection: DIRECTION DETECTED " + direction);
                //get unit and distance from title
                assert title != null;
                Log.d(TAG, "title: " + title);
                if (title.indexOf(" ") > 0) {
                    String t = title.substring(0, title.indexOf(" ")).trim().toLowerCase();
                    try {
                        int distance = Integer.parseInt(t);
                        String msg = direction + " in " + title;
                        newData.append(msg).append("\n").append(text).append("\n\n");
                        if (distance <= currentThreshold) {
                            ForegroundService.this.sendNotification(context, msg, text, iconRes, icon);
                            Toast.makeText(context, "<< directions sent >>", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                        Log.e(TAG, Arrays.toString(e.getStackTrace()));
                    }
                } else {
                    newData.append(title).append("\n").append(text).append("\n\n");
                }

                //TODO: use  DataUpdateListener?
                context.sendBroadcast(new Intent(DIRECTION_BROADCAST).putExtra("newData", newData.toString()));
            }
        }
    }

}
