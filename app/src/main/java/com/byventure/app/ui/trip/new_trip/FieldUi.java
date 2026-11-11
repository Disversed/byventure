package com.byventure.app.ui.trip.new_trip;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.MaterialColors;

public final class FieldUi {
    private FieldUi() {}

    /** value == null -> показываем подсказку серым цветом. */
    public static void setPickerValue(MaterialButton button, @Nullable String value, CharSequence placeholder) {
        button.setText(value != null ? value : placeholder);
        button.setTextColor(MaterialColors.getColor(button, value != null
                ? com.google.android.material.R.attr.colorOnSurface
                : com.google.android.material.R.attr.colorOnSurfaceVariant));
    }

    /** error == null -> обычная рамка и скрытый текст ошибки. Красную рамку задаёт состояние activated. */
    public static void setError(MaterialButton button, TextView errorView, @Nullable String error) {
        button.setActivated(error != null);
        errorView.setText(error);
        errorView.setVisibility(error == null ? View.GONE : View.VISIBLE);
    }
}