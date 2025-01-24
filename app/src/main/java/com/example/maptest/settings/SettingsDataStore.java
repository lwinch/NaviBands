package com.example.maptest.settings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicReference;

public class SettingsDataStore {
    private static final String USER_PREFERENCES_NAME = "user_preferences";
    private final SharedPreferences sharedPreferences;
    //private final MutableLiveData<Integer> _distThresholdFlow = new MutableLiveData<>();
    //public LiveData<Integer> distThresholdFlow = _distThresholdFlow;
    public final MinorDistSetting minorDistSetting;
    public final MajorDistSetting majorDistSetting;
    public final PushRadioSetting pushRadioSetting;

    private SettingsDataStore(Context context) {
        this.sharedPreferences = context.getApplicationContext().getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE);
        this.minorDistSetting = new MinorDistSetting(context, sharedPreferences);
        this.majorDistSetting = new MajorDistSetting(context, sharedPreferences);
        this.pushRadioSetting = new PushRadioSetting();
        //_distThresholdFlow.setValue(getDistanceThreshold());
    }

    public PushRadioOption getPushRadioOption() {
        return pushRadioSetting.get(sharedPreferences);
    }

    public void updatePushRadioSetting(PushRadioOption option) {
        pushRadioSetting.set(sharedPreferences, option);
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
