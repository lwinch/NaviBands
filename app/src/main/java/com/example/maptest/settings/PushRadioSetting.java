package com.example.maptest.settings;

import android.content.SharedPreferences;
import android.util.Log;

public class PushRadioSetting {
    private static final String TAG = "PushRadioSettings";
    public static final String PUSH_RADIO_KEY = "push_notification_radio_key";
    public final PushRadioOption pushRadioDefault;

    public PushRadioSetting() {
        this.pushRadioDefault = PushRadioOption.OFF;
    }

    public PushRadioOption get(SharedPreferences sharedPreferences) {
        return PushRadioOption.fromString(sharedPreferences.getString(PUSH_RADIO_KEY, pushRadioDefault.storeId));
    }

    public boolean get(SharedPreferences sharedPreferences, PushRadioOption option) {
        boolean bool = option == PushRadioOption.fromString(sharedPreferences.getString(PUSH_RADIO_KEY, pushRadioDefault.storeId));
        Log.d(TAG, "get called option: " + option.storeId);
        Log.d(TAG, "get called current value: " + sharedPreferences.getString(PUSH_RADIO_KEY, pushRadioDefault.storeId));
        Log.d(TAG, "get called match: " + bool);
        return bool;
    }

    public void set(SharedPreferences sharedPreferences, PushRadioOption pushRadioOption) {
        Log.d(TAG, "set called: " + pushRadioOption.storeId);
        sharedPreferences.edit().putString(PUSH_RADIO_KEY, pushRadioOption.storeId).apply();
    }

}
