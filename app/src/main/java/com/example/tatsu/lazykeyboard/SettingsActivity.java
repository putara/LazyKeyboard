package com.example.tatsu.lazykeyboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != REQUEST_CODE) {
            return;
        }
        if (mPermissionPreference == null) {
            return;
        }
        mPermissionPreference.updatePermissions();
        mPermissionPreference = null;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference instanceof PermissionPreference) {
            PermissionPreference permissionPreference = (PermissionPreference) preference;
            if (permissionPreference.isPermissionGranted()) {
                return false;
            }
            mPermissionPreference = permissionPreference;
            permissionPreference.requestPermissions(this, REQUEST_CODE);
        }
        if (preference instanceof  TestPreference) {
            TesterFragment.newInstance().show(getFragmentManager(), "?");
        }
        return false;
    }

    public static class SettingsFragment extends PreferenceFragment {
        private PermissionPreference mPermissionPreference;
        private TestPreference mTestPreference;
        private Preference.OnPreferenceClickListener mListener;

        public SettingsFragment setListener(Preference.OnPreferenceClickListener listener) {
            mListener = listener;
            if (mListener != null) {
                if (mPermissionPreference != null) {
                    mPermissionPreference.setOnPreferenceClickListener(mListener);
                }
                if (mTestPreference != null) {
                    mTestPreference.setOnPreferenceClickListener(mListener);
                }
            }
            return this;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            mPermissionPreference = (PermissionPreference) findPreference(getString(R.string.pref_permissions_key));
            mTestPreference = (TestPreference) findPreference(getString(R.string.pref_test_key));
            if (mListener != null) {
                mPermissionPreference.setOnPreferenceClickListener(mListener);
                mTestPreference.setOnPreferenceClickListener(mListener);
            }
        }
    }
}