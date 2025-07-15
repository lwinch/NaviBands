package com.example.maptest.settings;

import android.util.Log;

public interface RadioOption {

    String getStoreId();

    int getResId();

    RadioOption fromString(String s);

}
