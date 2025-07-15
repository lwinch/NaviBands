package com.example.maptest.uiListeners;

import android.widget.CompoundButton;

import com.example.maptest.settings.PushTextRadioOption;
import com.example.maptest.settings.PushTitleRadioOption;
import com.example.maptest.settings.RadioOption;
import com.example.maptest.settings.SettingsDataStore;

public class PushRadioListener implements CompoundButton.OnCheckedChangeListener {
    private final RadioOption option;
    private final SettingsDataStore settingsDataStore;

    public PushRadioListener(RadioOption option, SettingsDataStore settingsDataStore) {
        this.option = option;
        this.settingsDataStore = settingsDataStore;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {

            if (option instanceof PushTitleRadioOption) {
                settingsDataStore.updatePushTitleRadioSetting((PushTitleRadioOption) option);
            }
            if (option instanceof PushTextRadioOption) {
                settingsDataStore.updatePushTextRadioSetting((PushTextRadioOption) option);
            }
        }
    }
}
