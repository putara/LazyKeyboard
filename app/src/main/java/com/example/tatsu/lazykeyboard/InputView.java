package com.example.tatsu.lazykeyboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class InputView extends RelativeLayout implements SharedPreferences.OnSharedPreferenceChangeListener {
    public interface OnKeyboardActionListener {
        void onPress(String email, String password);
        void onSwitchIme();
        void onEnter();
        void onRefresh(InputView inputView);
    }

    private static final String TAG = "InputView";

    private OnKeyboardActionListener mListener;
    private ListView mListView;
    private LinearLayout mOverlayLayout;
    private TextView mErrorView;
    private Adapter mAdapter;
    private ImageButton mSwitchButton;
    private ImageButton mRefreshButton;
    private ImageButton mEnterButton;
    private SharedPreferences mPreferences;
    private boolean mLayoutSwapped;

    public InputView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InputView setOnKeyboardActionListener(OnKeyboardActionListener listener) {
        mListener = listener;
        if (mSwitchButton == null) {
            mSwitchButton = findViewById(R.id.switchIme);
            mSwitchButton.setOnClickListener(v -> fireSwitchEvent());
        }
        if (mRefreshButton == null) {
            mRefreshButton = findViewById(R.id.refresh);
            mRefreshButton.setOnClickListener(v-> fireRefreshEvent());
        }
        if (mEnterButton == null) {
            mEnterButton = findViewById(R.id.enter);
            mEnterButton.setOnClickListener(v-> fireEnterEvent());
        }
        return this;
    }

    public InputView setCredentials(Credential[] credentials) {
        setAdapter(new Adapter(getContext(), credentials));
        setError(null);
        return this;
    }

    public InputView setError(String text) {
        if (mOverlayLayout == null) {
            mOverlayLayout = findViewById(R.id.overlay);
        }
        if (text == null) {
            mOverlayLayout.setVisibility(View.GONE);
            return this;
        }
        if (mErrorView == null) {
            mErrorView = findViewById(R.id.error);
        }
        setAdapter(new Adapter(getContext(), new Credential[0]));
        mErrorView.setText(text);
        mOverlayLayout.setVisibility(View.VISIBLE);
        return this;
    }

    private void setAdapter(Adapter adapter) {
        if (mListView == null) {
            mListView = findViewById(R.id.list);
            loadSettings();
        }
        mAdapter = adapter;
        mListView.setAdapter(mAdapter);
    }

    private void firePressEvent(String email, String password) {
        if (mListener != null) {
            mListener.onPress(email, password);
        }
    }

    private void fireSwitchEvent() {
        if (mListener != null) {
            mListener.onSwitchIme();
        }
    }

    private void fireRefreshEvent() {
        if (mListener != null) {
            mListener.onRefresh(this);
        }
    }

    private void fireEnterEvent() {
        if (mListener != null) {
            mListener.onEnter();
        }
    }

    private void loadSettings() {
        if (mPreferences == null) {
            mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            mPreferences.registerOnSharedPreferenceChangeListener(this);
        }
        mLayoutSwapped = mPreferences.getBoolean(getContext().getString(R.string.pref_swap_key), false);
        Log.d(TAG, "layout = " + (mLayoutSwapped ? "left" : "right"));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged");
        loadSettings();
        if (mListView != null) {
            mListView.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isInEditMode()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int wmode = MeasureSpec.getMode(widthMeasureSpec);
        int wsize = MeasureSpec.getSize(widthMeasureSpec);
        int hmode = MeasureSpec.getMode(heightMeasureSpec);
        int hsize = MeasureSpec.getSize(heightMeasureSpec);
        String swmode = wmode == MeasureSpec.AT_MOST ? "atMost" : (wmode == MeasureSpec.EXACTLY ? "exactly" : (wmode == MeasureSpec.UNSPECIFIED ? "unspecified" : String.valueOf(wmode)));
        String shmode = hmode == MeasureSpec.AT_MOST ? "atMost" : (hmode == MeasureSpec.EXACTLY ? "exactly" : (hmode == MeasureSpec.UNSPECIFIED ? "unspecified" : String.valueOf(hmode)));
        Log.d(TAG, String.format("onMeasure: width = %s/%d, height = %s/%d", swmode, wsize, shmode, hsize));
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(metrics.heightPixels * 2 / 5, MeasureSpec.EXACTLY));
    }

    private static Credential getCredential(View view) {
        Object tag = view.getTag();
        if (tag instanceof Credential) {
            return (Credential) tag;
        }
        ViewParent parent = view.getParent();
        if (parent instanceof View) {
            return getCredential(((View) parent));
        }
        return null;
    }

    class Adapter extends ArrayAdapter<Credential> {
        public Adapter(Context context, Credential[] credentials) {
            super(context, 0, credentials);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            boolean init = convertView == null;
            if (init) {
                convertView = LayoutInflater.from(getContext())
                    .inflate(mLayoutSwapped ? R.layout.row2 : R.layout.row, parent, false);
            }
            convertView.setTag(getItem(position));
            TextView info = convertView.findViewById(R.id.info);
            Button submit = convertView.findViewById(R.id.submit);
            if (init) {
                submit.setOnClickListener(v -> {
                    Credential credential = getCredential(v);
                    if (credential != null) {
                        firePressEvent(credential.email, credential.password);
                    }
                });
                submit.setOnLongClickListener(v -> {
                    Credential credential = getCredential(v);
                    if (credential != null) {
                        Toast.makeText(getContext(), credential.email, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                });
                convertView.setOnClickListener(v -> {
                    Credential credential = getCredential(v);
                    if (credential != null) {
                        Toast.makeText(getContext(), credential.email, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            Credential credential = (Credential) convertView.getTag();
            info.setText(credential.info);
            submit.setText(credential.name);
            return convertView;
        }
    }
}
