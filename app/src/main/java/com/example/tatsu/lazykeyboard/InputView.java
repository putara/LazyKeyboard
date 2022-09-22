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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InputView extends ConstraintLayout implements SharedPreferences.OnSharedPreferenceChangeListener {
    public interface OnKeyboardActionListener {
        void onPress(String email, String password);
        void onSwitchIme();
        void onEnter();
        void onRefresh(InputView inputView);
    }

    private static final String TAG = "InputView";

    private OnKeyboardActionListener mListener;
    private RecyclerView mRecyclerView;
    private LinearLayout mOverlayLayout;
    private TextView mErrorView;
    private Adapter mAdapter;
    private ImageButton mSwitchButton;
    private ImageButton mRefreshButton;
    private ImageButton mEnterButton;
    private SharedPreferences mPreferences;
    private boolean mLayoutSwapped;

    public InputView(@NonNull Context context, @Nullable AttributeSet attrs) {
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
        setAdapter(new Adapter(credentials));
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
        setAdapter(new Adapter(new Credential[0]));
        mErrorView.setText(text);
        mOverlayLayout.setVisibility(View.VISIBLE);
        return this;
    }

    private void setAdapter(Adapter adapter) {
        if (mRecyclerView == null) {
            mRecyclerView = findViewById(R.id.list);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            loadSettings();
        }
        mAdapter = adapter;
        mRecyclerView.setAdapter(mAdapter);
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
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mAdapter);
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

    class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private final List<Credential> mCredentials = new ArrayList<>();

        public Adapter(Credential[] credentials) {
            Map<String, Credential> map = new LinkedHashMap<>();
            for (Credential credential : credentials) {
                map.put(credential.email, credential);
            }
            this.mCredentials.addAll(map.values());
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(mLayoutSwapped ? R.layout.row2 : R.layout.row, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(mCredentials.get(position));
        }

        @Override
        public int getItemCount() {
            return mCredentials.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mInfo;
        private final Button mSubmit;
        private Credential mCredential;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mInfo = itemView.findViewById(R.id.info);
            mSubmit = itemView.findViewById(R.id.submit);
            mSubmit.setOnClickListener(v -> {
                if (mCredential != null) {
                    firePressEvent(mCredential.email, mCredential.password);
                }
            });
            mSubmit.setOnLongClickListener(v -> {
                if (mCredential != null) {
                    Toast.makeText(getContext(), mCredential.email, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });
            itemView.setOnClickListener(v -> {
                if (mCredential != null) {
                    Toast.makeText(getContext(), mCredential.email, Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void bind(Credential credential) {
            mInfo.setText(credential.info);
            mSubmit.setText(credential.name);
            mCredential = credential;
        }
    }
}
