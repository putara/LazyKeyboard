package com.example.tatsu.lazykeyboard;

import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;

public class LazyKeyboard extends InputMethodService implements InputView.OnKeyboardActionListener {
    private int mDelay = 100;

    @Override
    public View onCreateInputView() {
        InputView inputView = (InputView) getLayoutInflater().inflate(R.layout.input, null);
        inputView.setOnKeyboardActionListener(this);
        reloadConfig(inputView);
        return inputView;
    }

    @Override
    public void onPress(String email, String password) {
        InputConnection ic = getCurrentInputConnection();
        clearText(ic);
        ic.commitText(email, 1);
        ic.performEditorAction(EditorInfo.IME_ACTION_NEXT);
        new Handler(Looper.getMainLooper()).postDelayed(()-> {
            InputConnection icc = getCurrentInputConnection();
            clearText(icc);
            icc.commitText(password, 1);
            icc.performEditorAction(EditorInfo.IME_ACTION_GO);
            onSwitchIme();
        }, mDelay);
    }

    @Override
    public void onSwitchIme() {
        switchToPreviousInputMethod();
    }

    @Override
    public void onRefresh(InputView inputView) {
        if (reloadConfig(inputView)) {
            Toast.makeText(this, "Reloaded", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEnter() {
        InputConnection ic = getCurrentInputConnection();
        ic.performEditorAction(EditorInfo.IME_ACTION_GO);
    }

    private static void clearText(InputConnection ic) {
        int currentLen = ic.getExtractedText(new ExtractedTextRequest(), 0).text.length();
        CharSequence beforeText = ic.getTextBeforeCursor(currentLen, 0);
        CharSequence afterText = ic.getTextAfterCursor(currentLen, 0);
        ic.deleteSurroundingText(beforeText.length(), afterText.length());
    }

    private boolean reloadConfig(InputView inputView) {
        try {
            Config config = Config.load();
            inputView.setCredentials(config.getCredentials());
            mDelay = config.getDelay();
            return true;
        } catch (FileNotFoundException e) {
            inputView.setError("Cannot read teretere.yml.");
        } catch (IOException e) {
            e.printStackTrace();
            inputView.setError("Something has gone wrong.");
        }
        return false;
    }
}
