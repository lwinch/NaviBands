package com.example.maptest.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.maptest.R;

public class MajorDistSetting {
    private static final String MAJOR_DIST_THRESHOLD_KEY = "major_dist_threshold_key";
    private final int majorDistThresholdDefault;
    private final SharedPreferences sharedPreferences;

    public MajorDistSetting(Context context, SharedPreferences sharedPreferences) {
        this.majorDistThresholdDefault = context.getResources().getInteger(R.integer.second_unit_max_x10);
        this.sharedPreferences = sharedPreferences;
    }

    public double get() {
        return sharedPreferences.getInt(MAJOR_DIST_THRESHOLD_KEY, majorDistThresholdDefault) / 10.;
    }

    public int get_x10() {
        return sharedPreferences.getInt(MAJOR_DIST_THRESHOLD_KEY, majorDistThresholdDefault);
    }

    public void update_x10(int distThreshold) {
        sharedPreferences.edit().putInt(MAJOR_DIST_THRESHOLD_KEY, distThreshold).apply();
    }
}
