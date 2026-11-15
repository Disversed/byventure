package com.byventure.app.ui.trip.new_trip.common;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.byventure.app.databinding.ItemDateHeaderBinding;

import java.time.LocalDate;

/** Заголовок дня («24 декабря») для всех списков «по дням». */
public class DateHeaderHolder extends RecyclerView.ViewHolder {
    private final ItemDateHeaderBinding b;

    private DateHeaderHolder(ItemDateHeaderBinding b) {
        super(b.getRoot());
        this.b = b;
    }

    public static DateHeaderHolder create(ViewGroup parent) {
        return new DateHeaderHolder(
                ItemDateHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    public void bind(LocalDate date) {
        b.tvDate.setText(date.format(TripFormats.DAY_HEADER));
    }
}