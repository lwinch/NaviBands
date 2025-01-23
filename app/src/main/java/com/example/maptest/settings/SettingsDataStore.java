package com.example.maptest.settings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.maptest.R;

import java.util.concurrent.atomic.AtomicReference;

public class SettingsDataStore {
    private static final String USER_PREFERENCES_NAME = "user_preferences";
    private static final String MINOR_DIST_THRESHOLD_KEY = "minor_dist_threshold_key";
    private static final String MAJOR_DIST_THRESHOLD_KEY = "major_dist_threshold_key";
    private final SharedPreferences sharedPreferences;
    //private final MutableLiveData<Integer> _distThresholdFlow = new MutableLiveData<>();
    //public LiveData<Integer> distThresholdFlow = _distThresholdFlow;
    private final int minorDistThresholdDefault;
    private final int majorDistThresholdDefault;
    private final PushRadioSettings pushRadioSettings;

    private SettingsDataStore(Context context) {
        this.minorDistThresholdDefault = context.getResources().getInteger(R.integer.first_unit_max);
        this.majorDistThresholdDefault = context.getResources().getInteger(R.integer.second_unit_max_x10);
        this.pushRadioSettings = new PushRadioSettings();
        sharedPreferences = context.getApplicationContext().getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE);
        //_distThresholdFlow.setValue(getDistanceThreshold());
    }

    public int getMinorDistanceThreshold() {
        return sharedPreferences.getInt(MINOR_DIST_THRESHOLD_KEY, minorDistThresholdDefault);
    }

    public double getMajorDistanceThreshold() {
        return sharedPreferences.getInt(MAJOR_DIST_THRESHOLD_KEY, majorDistThresholdDefault) / 10.;
    }

    public int getMajorDistanceThreshold_x10() {
        return sharedPreferences.getInt(MAJOR_DIST_THRESHOLD_KEY, majorDistThresholdDefault);
    }

    public PushRadioOption getPushRadioOption() {
        return pushRadioSettings.get(sharedPreferences);
    }

    public void updateMinorDistanceThreshold(int distThreshold) {
        sharedPreferences.edit().putInt(MINOR_DIST_THRESHOLD_KEY, distThreshold).apply();
    }

    public void updateMajorDistanceThreshold_x10(int distThreshold) {
        sharedPreferences.edit().putInt(MAJOR_DIST_THRESHOLD_KEY, distThreshold).apply();
    }

    public void updatePushRadioSetting(PushRadioOption option) {
        pushRadioSettings.set(sharedPreferences, option);
    }

    private static final AtomicReference<SettingsDataStore> INSTANCE = new AtomicReference<>();

    public static SettingsDataStore getInstance(@NonNull Context context) {
        SettingsDataStore instance = INSTANCE.get();
        if (instance == null) {
            synchronized (SettingsDataStore.class) {
                instance = INSTANCE.get();
                if (instance == null) {
                    instance = new SettingsDataStore(context);
                    INSTANCE.set(instance);
                }
            }
        }
        return instance;
    }


}
