package com.byventure.app.ui.trip.new_trip.common;

import android.text.Editable;

import androidx.annotation.Nullable;

import com.byventure.app.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.time.LocalTime;
import java.util.function.Consumer;
import java.util.function.Predicate;

/** Общие хелперы для попапов-форм: чтение текста, показ ошибок, пикер времени. */
public abstract class FormBottomSheet extends ExpandedBottomSheet {

    protected String text(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    /** err == null -> ошибка снимается. Иначе показываем её только если show. */
    protected boolean check(TextInputLayout til, @Nullable String err, boolean show) {
        if (err == null) {
            til.setError(null);
            return true;
        }
        if (show) til.setError(err);
        return false;
    }

    /** Валидатор вызывается при вводе (без показа ошибки) и при потере фокуса (с показом). */
    protected void bindText(TextInputEditText et, Predicate<Boolean> validator, Runnable onChanged) {
        et.addTextChangedListener(new SimpleTextWatcher() {
            @Override public void afterTextChanged(Editable s) {
                validator.test(false);
                onChanged.run();
            }
        });
        et.setOnFocusChangeListener((v, hasFocus) -> { if (!hasFocus) validator.test(true); });
    }

    protected void pickTime(@Nullable LocalTime initial, Consumer<LocalTime> onPicked, Runnable onCancel) {
        LocalTime init = initial != null ? initial : LocalTime.of(12, 0);
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(init.getHour())
                .setMinute(init.getMinute())
                .setTitleText(R.string.picker_time_title)
                .build();
        picker.addOnPositiveButtonClickListener(v ->
                onPicked.accept(LocalTime.of(picker.getHour(), picker.getMinute())));
        picker.addOnNegativeButtonClickListener(v -> onCancel.run());
        picker.addOnCancelListener(d -> onCancel.run());
        picker.show(getChildFragmentManager(), "pick_time");
    }
}