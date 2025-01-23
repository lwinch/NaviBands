package com.example.maptest.uiListeners;

import android.widget.CompoundButton;

import com.example.maptest.settings.PushRadioOption;
import com.example.maptest.settings.SettingsDataStore;

public class PushRadioListener implements CompoundButton.OnCheckedChangeListener {
    private final PushRadioOption option;
    private final SettingsDataStore settingsDataStore;

    public PushRadioListener(PushRadioOption option, SettingsDataStore settingsDataStore) {
        this.option = option;
        this.settingsDataStore = settingsDataStore;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            settingsDataStore.updatePushRadioSetting(option);
        }
    }
}
