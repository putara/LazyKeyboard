package com.example.tatsu.lazykeyboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class TesterFragment extends DialogFragment {

    public TesterFragment() {
    }

    public static TesterFragment newInstance() {
        TesterFragment fragment = new TesterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // what a bunch of hacks
        View layout = LayoutInflater
                .from(getActivity())
                .inflate(R.layout.tester, (ViewGroup)getView(), false);

        EditText userText = layout.findViewById(R.id.userText);
        EditText passText = layout.findViewById(R.id.passText);

        InputMethodManager imeManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(layout)
                .setTitle(R.string.tester_title)
                .setPositiveButton(R.string.tester_submit, (di, which) -> {
                    Dialog d = getDialog();
                    if (imeManager != null && d != null) {
                        View view = d.getCurrentFocus();
                        if (view != null) {
                            imeManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.tester_result_title)
                            .setMessage(String.format(getString(R.string.tester_result_format), userText.getText().toString(), passText.getText().toString()))
                            .create()
                            .show();
                })
                .create();

        dialog.setOnShowListener(di -> {
            userText.requestFocus();
            if (imeManager != null) {
                imeManager.showInputMethodPicker();
            }
        });

        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        userText.setOnEditorActionListener((v, actionId, event) -> {
            switch (actionId) {
                case EditorInfo.IME_ACTION_DONE:
                case EditorInfo.IME_ACTION_NEXT:
                case EditorInfo.IME_ACTION_GO:
                    passText.requestFocus();
                    return true;
            }
            return false;
        });
        passText.setOnEditorActionListener((v, actionId, event) -> {
            switch (actionId) {
                case EditorInfo.IME_ACTION_DONE:
                case EditorInfo.IME_ACTION_NEXT:
                case EditorInfo.IME_ACTION_GO:
                    Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    button.callOnClick();
                    return true;
            }
            return false;
        });

        return dialog;
    }
}