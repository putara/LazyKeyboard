package com.example.tatsu.lazykeyboard;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;

public class PermissionPreference extends CheckBoxPreference {
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public PermissionPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PermissionPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PermissionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PermissionPreference(Context context) {
        super(context);
    }

    @Override
    public void setChecked(boolean checked) {
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        updatePermissions();
    }

    public boolean isPermissionGranted() {
        int result = PackageManager.PERMISSION_GRANTED;
        Context context = getContext();
        for (String permission : PERMISSIONS) {
            result |= context.checkSelfPermission(permission);
        }
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissions(Activity activity, int requestCode) {
        activity.requestPermissions(PERMISSIONS, requestCode);
    }

    public void updatePermissions() {
        boolean granted = isPermissionGranted();
        super.setChecked(granted);
        super.setEnabled(granted == false);
    }
}
