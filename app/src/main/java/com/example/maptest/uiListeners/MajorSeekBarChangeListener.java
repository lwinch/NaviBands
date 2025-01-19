package com.example.maptest.uiListeners;

import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.math.MathUtils;

import com.example.maptest.R;
import com.example.maptest.SettingsDataStore;

public class MajorSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "MajorSeekBarChangeListener";

    private final Resources res;
    private final SettingsDataStore settingsDataStore;
    private final TextView thresholdTvMajor;

    public MajorSeekBarChangeListener(Resources resources, SettingsDataStore settingsDataStore, TextView thresholdTvMajor) {
        this.res = resources;
        this.settingsDataStore = settingsDataStore;
        this.thresholdTvMajor = thresholdTvMajor;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        progress = MathUtils.clamp(progress, res.getInteger(R.integer.second_unit_min), res.getInteger(R.integer.second_unit_max));
        int currentThreshold = roundTo(progress, res.getInteger(R.integer.second_unit_interval));
        settingsDataStore.updateMajorDistanceThreshold(currentThreshold);
        String thresholdText = currentThreshold / 10. + res.getString(R.string.second_distance_unit);
        thresholdTvMajor.setText(thresholdText);
        thresholdTvMajor.setTextColor(Color.RED);
    }

    private int roundTo(int i, int r) {
        r = Math.max(1, r);
        return (int) Math.max(r * (Math.round((double) i / r)),0);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
