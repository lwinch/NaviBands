package com.example.maptest;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicReference;

public class SettingsDataStore {
    private static final String USER_PREFERENCES_NAME = "user_preferences";
    private static final String DIST_THRESHOLD_KEY = "dist_threshold_key";
    private final SharedPreferences sharedPreferences;
    //private final MutableLiveData<Integer> _distThresholdFlow = new MutableLiveData<>();
    //public LiveData<Integer> distThresholdFlow = _distThresholdFlow;
    private final int distThresholdDefault;

    private SettingsDataStore(Context context) {
        this.distThresholdDefault = context.getResources().getInteger(R.integer.first_unit_max);
        sharedPreferences = context.getApplicationContext().getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE);
        //_distThresholdFlow.setValue(getDistanceThreshold());
    }

    public int getDistanceThreshold() {
        return sharedPreferences.getInt(DIST_THRESHOLD_KEY, distThresholdDefault);
    }


    public void updateDistanceThreshold(int distThreshold) {
        sharedPreferences.edit().putInt(DIST_THRESHOLD_KEY, distThreshold).apply();
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
