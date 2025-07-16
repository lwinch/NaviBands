package com.example.maptest.settings;

import com.example.maptest.R;

public enum PushTitleRadioOption implements RadioOption {
    OFF("OFF", R.id.radio_title_off),
    EMOJI("EMOJI", R.id.radio_title_emoji),
    LONG_TXT("LONG_TXT", R.id.radio_title_long_text),
    SHORT_TXT("SHORT_TXT", R.id.radio_title_short_text),
    CHAR("CHAR", R.id.radio_title_character);
    private final String storeId;
    private final int resId;

    PushTitleRadioOption(String storeId, int resId) {
        this.storeId = storeId;
        this.resId = resId;
    }

    public static PushTitleRadioOption fromString(String storeId) {
        for (PushTitleRadioOption option : values()) {
            if (option.storeId.equals(storeId)) {
                return option;
            }
        }
        throw new IllegalArgumentException("No constant with text " + storeId + " found");
    }

    public static PushTitleRadioOption defaultValue() {
        return OFF;
    }

    @Override
    public String getStoreId() {
        return storeId;
    }

    @Override
    public int getResId() {
        return resId;
    }
}
