package com.byventure.app.ui.trip.details;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.byventure.app.R;
import com.google.android.material.color.MaterialColors;

import java.util.List;

/** Первый элемент списка — подсказка (серая, не выбирается). */
public class CategorySpinnerAdapter extends ArrayAdapter<String> {

    public CategorySpinnerAdapter(@NonNull Context context, @NonNull List<String> items) {
        super(context, R.layout.item_spinner, items);
        setDropDownViewResource(R.layout.item_spinner_dropdown);
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return tint(super.getView(position, convertView, parent), position);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return tint(super.getDropDownView(position, convertView, parent), position);
    }

    private View tint(View v, int position) {
        ((TextView) v).setTextColor(MaterialColors.getColor(v, position == 0
                ? com.google.android.material.R.attr.colorOnSurfaceVariant
                : com.google.android.material.R.attr.colorOnSurface));
        return v;
    }
}