package com.example.tatsu.lazykeyboard;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;

public class PermissionPreference extends AutoSwitchPreference {
    private static final String[] PERMISSIONS = {
        Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public PermissionPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PermissionPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PermissionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PermissionPreference(Context context) {
        super(context);
    }

    @Override
    protected boolean canBeChecked() {
        return isPermissionGranted(getContext());
    }

    public static boolean isPermissionGranted(Context context) {
        int result = PackageManager.PERMISSION_GRANTED;
        for (String permission : PERMISSIONS) {
            result |= context.checkSelfPermission(permission);
        }
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissions(Activity activity, int requestCode) {
        activity.requestPermissions(PERMISSIONS, requestCode);
    }
}
