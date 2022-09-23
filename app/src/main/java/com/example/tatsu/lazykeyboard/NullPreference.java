package com.example.tatsu.lazykeyboard;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

public class NullPreference extends Preference {
    private static final String TAG = "NullPreference";

    public NullPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public NullPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NullPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NullPreference(Context context) {
        super(context);
    }
}
