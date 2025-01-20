package com.example.maptest.broadcastReceivers;

import static com.example.maptest.Constants.DIRECTION_BROADCAST;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.util.Log;
import android.widget.Toast;

import com.example.maptest.IconService.IconService;
import com.example.maptest.R;
import com.example.maptest.notifications.DirectionsNotificationChannel;

import java.util.Arrays;

public class MapsNotificationBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";
    private final DirectionsNotificationChannel directionsNotificationChannel;
    private final IconService iconService;
    private int minorThreshold = 0;
    private int majorThreshold = 0;
    private String lastDirection = "";

    public MapsNotificationBroadcastReceiver(Context context) {
        this.directionsNotificationChannel = new DirectionsNotificationChannel(context);
        this.iconService = new IconService(context);
    }

    public void updateThresholds(int minorThreshold, int majorThreshold) {
        this.minorThreshold = minorThreshold;
        this.majorThreshold = majorThreshold;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Received NOTIFICATION_RECEIVED intent");
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        Icon icon = intent.getParcelableExtra("icon", Icon.class);

        //process the intent with pixel details and get result intent
        int iconRes;
        if (icon != null) {
            try {
                Log.d(TAG, "icon res pack: " + icon.loadDrawable(context));
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                Log.d(TAG, Arrays.toString(e.getStackTrace()));
            }
            iconRes = iconService.getDirection(icon.loadDrawable(context));
        } else {
            iconRes = R.drawable.notification_icon;
        }
        String direction = iconService.getDirectionName(iconRes);
        StringBuilder newData = new StringBuilder();
        if (title != null && title.indexOf(" ") > 0) {
            Log.d(TAG, "getDirection: DIRECTION DETECTED " + direction);
            //get unit and distance from title

            String distStr = title.substring(0, title.indexOf(" ")).trim().toLowerCase();
            String unit = title.substring(title.indexOf(" ") + 1).trim().toLowerCase();
            Log.d(TAG, "unit: <" + unit + ">");
            Log.d(TAG, "dist unit: <" + context.getString(R.string.first_distance_unit) + ">");
            try {
                double distance = Double.parseDouble(distStr);
                String msg = direction + " in " + title;
                newData.append(msg).append("\n").append(text).append("\n\n");
                if ((!lastDirection.equals(direction)) ||
                        (distance <= minorThreshold && unit.equals(context.getString(R.string.first_distance_unit))) ||
                        (distance <= majorThreshold / 10. && unit.equals(context.getString(R.string.second_distance_unit)))) {
                    directionsNotificationChannel.sendNotification(context, msg, text, iconRes, icon);
                    Toast.makeText(context, "<< directions sent >>", Toast.LENGTH_SHORT).show();
                    lastDirection = direction;
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                Log.e(TAG, Arrays.toString(e.getStackTrace()));
            }
        } else if (title != null) {
            directionsNotificationChannel.sendNotification(context, title, text, iconRes, icon);
            newData.append(title).append("\n").append(text).append("\n\n");
        }

        //TODO: use  DataUpdateListener?
        context.sendBroadcast(new Intent(DIRECTION_BROADCAST).putExtra("newData", newData.toString()));
    }
}
