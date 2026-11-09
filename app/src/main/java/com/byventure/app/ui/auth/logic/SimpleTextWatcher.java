package com.byventure.app.ui.auth.logic;

import android.text.Editable;
import android.text.TextWatcher;

/** Упрощённый TextWatcher: реагирует только на изменение текста. */
public abstract class SimpleTextWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        onChanged(s == null ? "" : s.toString());
    }

    protected abstract void onChanged(String text);
}