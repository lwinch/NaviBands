package com.example.maptest.uiListeners;

import android.widget.CompoundButton;

import com.example.maptest.settings.RadioOption;

import java.util.function.Consumer;

public class PushRadioListener<T extends RadioOption> implements CompoundButton.OnCheckedChangeListener {
    private final T option;
    private final Consumer<T> updateFunction;

    public PushRadioListener(T option, Consumer<T> updateFunction) {
        this.option = option;
        this.updateFunction = updateFunction;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            updateFunction.accept(option);
        }
    }
}
