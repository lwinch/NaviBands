package com.example.maptest.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.maptest.R;

public class MinorDistSetting {

    private static final String MINOR_DIST_THRESHOLD_KEY = "minor_dist_threshold_key";
    private final int minorDistThresholdDefault;
    private final SharedPreferences sharedPreferences;

    public MinorDistSetting(Context context, SharedPreferences sharedPreferences) {
        this.minorDistThresholdDefault = context.getResources().getInteger(R.integer.first_unit_max);
        this.sharedPreferences = sharedPreferences;
    }

    public int get() {
        return sharedPreferences.getInt(MINOR_DIST_THRESHOLD_KEY, minorDistThresholdDefault);
    }

    public void update(int distThreshold) {
        sharedPreferences.edit().putInt(MINOR_DIST_THRESHOLD_KEY, distThreshold).apply();
    }
}


