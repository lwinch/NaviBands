package com.example.maptest.settings;

import com.example.maptest.R;

public enum PushTextRadioOption implements RadioOption {
    OFF("OFF",R.id.radio_text_off),
    STREET("STREET", R.id.radio_text_street),
    INSTINCT2("INSTINCT2", R.id.radio_text_instinct2),
    CHAR("CHAR", R.id.radio_text_char);
    public final String storeId;
    public final int resId;

    PushTextRadioOption(String storeId, int resId) {
        this.storeId = storeId;
        this.resId = resId;
    }

    public static PushTextRadioOption fromString(String storeId) {
        for (PushTextRadioOption option : values()) {
            if (option.storeId.equals(storeId)) {
                return option;
            }
        }
        throw new IllegalArgumentException("No constant with text " + storeId + " found");
    }

    public static PushTextRadioOption defaultValue() {
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
