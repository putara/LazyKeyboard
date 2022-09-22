package com.example.tatsu.lazykeyboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.util.AttributeSet;

public class TestPreference extends Preference {
    private static final String TAG = "TestPreference";

    public TestPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TestPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TestPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestPreference(Context context) {
        super(context);
    }
}
