package com.example.maptest.broadcastReceivers;

import static com.example.maptest.Constants.DIRECTION_BROADCAST;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.util.Log;
import android.widget.Toast;

import com.example.maptest.IconService.IconData;
import com.example.maptest.IconService.IconService;
import com.example.maptest.R;
import com.example.maptest.notifications.DirectionsNotificationChannel;
import com.example.maptest.settings.PushTextRadioOption;
import com.example.maptest.settings.PushTitleRadioOption;

import java.util.Arrays;

public class MapsNotificationBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";
    private final DirectionsNotificationChannel directionsNotificationChannel;
    private final IconService iconService;
    private int minorThreshold = 0;
    private double majorThreshold = 0.;
    private PushTitleRadioOption pushTitleRadioOption;
    private PushTextRadioOption pushTextRadioOption;
    private double lastMinorUnitDist = 0.;

    public MapsNotificationBroadcastReceiver(Context context) {
        this.directionsNotificationChannel = new DirectionsNotificationChannel(context);
        this.iconService = new IconService(context);
    }

    public void updateSettings(int minorThreshold, double majorThreshold, PushTitleRadioOption titleOption, PushTextRadioOption textOption) {
        this.minorThreshold = minorThreshold;
        this.majorThreshold = majorThreshold;
        this.pushTitleRadioOption = titleOption;
        this.pushTextRadioOption = textOption;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Received NOTIFICATION_RECEIVED intent");
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        Icon icon = intent.getParcelableExtra("icon", Icon.class);

        //process the intent with pixel details and get result intent
        IconData iconData;
        if (icon != null) {
            try {
                Log.d(TAG, "icon res pack: " + icon.loadDrawable(context));
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                Log.d(TAG, Arrays.toString(e.getStackTrace()));
            }
            iconData = iconService.getDirection(icon.loadDrawable(context));
        } else {
            iconData = new IconData(R.drawable.notification_icon, null);
        }
        String direction;
        switch (this.pushTitleRadioOption) {
            case EMOJI:
                direction = iconData.getDirection().getEmoji();
                break;
            case LONG_TXT:
                direction = iconData.getDirection().getLongName();
                break;
            case CHAR:
                direction = iconData.getDirection().getSymbol();
                break;
            case SHORT_TXT:
            default:
                direction = iconData.getDirection().getShortName();
                break;
        }
        String directionBodyInfo;
        switch (this.pushTextRadioOption) {
            case CHAR:
                directionBodyInfo = iconData.getDirection().getSymbol();
                break;
            case INSTINCT2:
                directionBodyInfo = iconData.getDirection().getBigSymbol();
                break;
            case STREET:
                directionBodyInfo = text;
                break;
            default:
                directionBodyInfo = "";
        }

        int iconRes = iconData.getResId();
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
                double minorUnitDistance = 0;
                int conversionFactor = context.getResources().getInteger(R.integer.conversion_factor);
                String minorUnit = context.getString(R.string.first_distance_unit);
                String majorUnit = context.getString(R.string.second_distance_unit);
                if (unit.equals(minorUnit)) {
                    minorUnitDistance = distance;
                } else if (unit.equals(majorUnit)) {
                    minorUnitDistance = distance * conversionFactor;
                }

                if (this.pushTitleRadioOption != PushTitleRadioOption.OFF &&
                        ((minorUnitDistance > lastMinorUnitDist) ||
                        (minorUnitDistance <= minorThreshold) ||
                        (minorUnitDistance <= majorThreshold * conversionFactor))) {

                    directionsNotificationChannel.sendNotification(context, msg, directionBodyInfo, iconRes, icon);
                    Toast.makeText(context, "<< directions sent >>", Toast.LENGTH_SHORT).show();
                    lastMinorUnitDist = minorUnitDistance;
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                Log.e(TAG, Arrays.toString(e.getStackTrace()));
            }
        } else if (title != null) {
            if (this.pushTitleRadioOption != PushTitleRadioOption.OFF) {
                directionsNotificationChannel.sendNotification(context, title, text, iconRes, icon);
                newData.append(title).append("\n").append(text).append("\n\n");
            }
        }
        //I think this sends logging back to the main app
        //TODO: use  DataUpdateListener?
        context.sendBroadcast(new Intent(DIRECTION_BROADCAST).putExtra("newData", newData.toString()));
    }
}
