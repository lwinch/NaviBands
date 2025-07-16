package com.example.maptest.settings;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.function.Function;

public class RadioSetting<T extends RadioOption> {

    private final String TAG;
    private final String PUSH_RADIO_KEY;
    private final RadioOption pushRadioDefault;
    private final Function<String, T> fromStringFunction;

    RadioSetting(String tag, String key, T defaultOption, Function<String, T> fromStringFunction) {
        this.TAG = tag;
        this.PUSH_RADIO_KEY = key;
        this.pushRadioDefault = defaultOption;
        this.fromStringFunction = fromStringFunction;
    }

    public RadioOption get(SharedPreferences sharedPreferences) {
        String storedValue = sharedPreferences.getString(PUSH_RADIO_KEY, pushRadioDefault.getStoreId());
        return fromStringFunction.apply(storedValue);
    }

//    public boolean get(SharedPreferences sharedPreferences, RadioOption option) {
//        boolean bool = option == this.pushRadioDefault.fromString(sharedPreferences.getString(PUSH_RADIO_KEY, pushRadioDefault.getStoreId()));
//        Log.d(TAG, "get called option: " + option.getStoreId());
//        Log.d(TAG, "get called current value: " + sharedPreferences.getString(PUSH_RADIO_KEY, pushRadioDefault.getStoreId()));
//        Log.d(TAG, "get called match: " + bool);
//        return bool;
//    }

    public void set(SharedPreferences sharedPreferences, RadioOption radioOption) {
        Log.d(TAG, "set called: " + radioOption.getStoreId());
        sharedPreferences.edit().putString(PUSH_RADIO_KEY, radioOption.getStoreId()).apply();
    }
}
