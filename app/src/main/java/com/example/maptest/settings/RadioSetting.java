package com.example.maptest.settings;

import android.content.SharedPreferences;
import android.util.Log;

public class RadioSetting<T extends RadioOption> {

    private final String TAG;
    private final String PUSH_RADIO_KEY;
    private final RadioOption pushRadioDefault;

    RadioSetting(String tag, String key, T defaultOption) {
        this.TAG = tag;
        this.PUSH_RADIO_KEY = key;
        this.pushRadioDefault = defaultOption;
    }

    public RadioOption get(SharedPreferences sharedPreferences) {
        return this.pushRadioDefault.fromString(sharedPreferences.getString(PUSH_RADIO_KEY, pushRadioDefault.getStoreId()));
    }

    public boolean get(SharedPreferences sharedPreferences, RadioOption option) {
        boolean bool = option == this.pushRadioDefault.fromString(sharedPreferences.getString(PUSH_RADIO_KEY, pushRadioDefault.getStoreId()));
        Log.d(TAG, "get called option: " + option.getStoreId());
        Log.d(TAG, "get called current value: " + sharedPreferences.getString(PUSH_RADIO_KEY, pushRadioDefault.getStoreId()));
        Log.d(TAG, "get called match: " + bool);
        return bool;
    }

    public void set(SharedPreferences sharedPreferences, RadioOption radioOption) {
        Log.d(TAG, "set called: " + radioOption.getStoreId());
        sharedPreferences.edit().putString(PUSH_RADIO_KEY, radioOption.getStoreId()).apply();
    }
}
