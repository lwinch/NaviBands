package com.example.maptest.settings;

import com.example.maptest.R;

public enum PushRadioOption {
    OFF("OFF", R.id.radio_off),
    EMOJI("EMOJI", R.id.radio_emoji),
    LONG_TXT("LONG_TXT", R.id.radio_long_text),
    SHORT_TXT("SHORT_TXT", R.id.radio_short_text),
    CHAR("CHAR", R.id.radio_character);
    public final String storeId;
    public final int resId;

    PushRadioOption(String storeId, int resId) {
        this.storeId = storeId;
        this.resId = resId;
    }

    public static PushRadioOption fromString(String storeId) {
        for (PushRadioOption pushRadioOption : PushRadioOption.values()) {
            if (pushRadioOption.storeId.equals(storeId)) {
                return pushRadioOption;
            }
        }
        throw new IllegalArgumentException("No constant with text " + storeId + " found");
    }
}
