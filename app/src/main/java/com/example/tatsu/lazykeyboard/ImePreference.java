package com.example.tatsu.lazykeyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

public class ImePreference extends AutoSwitchPreference {
    public ImePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ImePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ImePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImePreference(Context context) {
        super(context);
    }

    @Override
    protected boolean canBeChecked() {
        return isImeEnabled(getContext());
    }

    public static boolean isImeEnabled(Context context) {
        try {
            InputMethodManager imeManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            for (InputMethodInfo info : imeManager.getEnabledInputMethodList()) {
                if (info.getPackageName().equals(context.getPackageName())) {
                    return true;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }
}
