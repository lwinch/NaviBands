package com.example.maptest.uiListeners;

import android.content.res.Resources;
import android.graphics.Color;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.math.MathUtils;

import com.example.maptest.R;
import com.example.maptest.SettingsDataStore;

public class MinorSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
    private final Resources res;
    private final SettingsDataStore settingsDataStore;
    private final TextView thresholdTvMinor;

    public MinorSeekBarChangeListener(Resources resources, SettingsDataStore settingsDataStore, TextView thresholdTvMinor) {
        this.res = resources;
        this.settingsDataStore = settingsDataStore;
        this.thresholdTvMinor = thresholdTvMinor;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        progress = MathUtils.clamp(progress, res.getInteger(R.integer.first_unit_min), res.getInteger(R.integer.first_unit_max));
        int currentThreshold = roundTo(progress, res.getInteger(R.integer.first_unit_interval));
        settingsDataStore.updateMinorDistanceThreshold(currentThreshold);
        String thresholdText = currentThreshold + res.getString(R.string.first_distance_unit);
        thresholdTvMinor.setText(thresholdText);
        thresholdTvMinor.setTextColor(Color.RED);
    }

    private int roundTo(int i, int r) {
        r = Math.max(1, r);
        return (int) Math.max(r * (Math.round((double) i / r)), 0);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

}