package com.example.tatsu.lazykeyboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends Activity implements Preference.OnPreferenceClickListener {
    private static final String TAG = "SettingsActivity";
    private static int REQUEST_CODE = 1000;
    private PermissionPreference mPermissionPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment().setListener(this))
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean imeEnabled = ImePreference.isImeEnabled(this);
        boolean permissionGranted = PermissionPreference.isPermissionGranted(this);
        int message;
        if (imeEnabled) {
            if (permissionGranted) {
                return;
            } else {
                message = R.string.grant_permission_message;
            }
        } else {
            if (permissionGranted) {
                message = R.string.enable_ime_message;
            } else {
                message = R.string.both_message;
            }
        }
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (di, which) -> {})
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != REQUEST_CODE) {
            return;
        }
        if (mPermissionPreference == null) {
            return;
        }
        mPermissionPreference.updateChecked();
        mPermissionPreference = null;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.pref_permissions_key))) {
            PermissionPreference permissionPreference = (PermissionPreference) preference;
            if (PermissionPreference.isPermissionGranted(this)) {
                return false;
            }
            mPermissionPreference = permissionPreference;
            permissionPreference.requestPermissions(this, REQUEST_CODE);
            return true;
        }
        if (preference.getKey().equals(getString(R.string.pref_ime_key))) {
            startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
            return true;
        }
        if (preference.getKey().equals(getString(R.string.pref_test_key))) {
            TesterFragment.newInstance().show(getFragmentManager(), "?");
            return true;
        }
        return false;
    }

    public static class SettingsFragment extends PreferenceFragment {
        private final List<Preference> mPreferences = new ArrayList<>();
        private Preference.OnPreferenceClickListener mListener;

        private void attachListener() {
            for (Preference preference : mPreferences) {
                preference.setOnPreferenceClickListener(mListener);
            }
        }

        public SettingsFragment setListener(Preference.OnPreferenceClickListener listener) {
            mListener = listener;
            attachListener();
            return this;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            mPreferences.add(findPreference(getString(R.string.pref_permissions_key)));
            mPreferences.add(findPreference(getString(R.string.pref_ime_key)));
            mPreferences.add(findPreference(getString(R.string.pref_test_key)));
            attachListener();
        }
    }
}