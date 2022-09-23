package com.example.tatsu.lazykeyboard;

import android.content.Context;
import android.preference.SwitchPreference;
import android.util.AttributeSet;

public abstract class AutoSwitchPreference extends SwitchPreference {
    public AutoSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInternal();
    }

    public AutoSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInternal();
    }

    public AutoSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInternal();
    }

    public AutoSwitchPreference(Context context) {
        super(context);
        initInternal();
    }

    private void initInternal() {
        super.setPersistent(false);
        super.setDefaultValue(-1);
    }

    @Override
    public void setDefaultValue(Object defaultValue) {
    }

    @Override
    public void setPersistent(boolean persistent) {
    }

    @Override
    public void setChecked(boolean checked) {
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        updateChecked();
    }

    protected abstract boolean canBeChecked();

    public void updateChecked() {
        boolean checked = canBeChecked();
        super.setChecked(checked);
        super.setEnabled(checked == false);
    }
}
