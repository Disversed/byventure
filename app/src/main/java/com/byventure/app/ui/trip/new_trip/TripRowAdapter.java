package com.byventure.app.ui.trip.new_trip;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.byventure.app.databinding.ItemAddEntryBinding;
import com.byventure.app.databinding.ItemDateHeaderBinding;
import com.byventure.app.databinding.ItemTripEntryBinding;

import java.time.LocalDate;

public class TripRowAdapter extends ListAdapter<TripRow, RecyclerView.ViewHolder> {

    public interface Listener {
        void onAddClick(LocalDate date);
    }

    private final Listener listener;

    public TripRowAdapter(Listener listener) {
        super(new DiffUtil.ItemCallback<TripRow>() {
            @Override
            public boolean areItemsTheSame(@NonNull TripRow a, @NonNull TripRow b) {
                return a.getKey().equals(b.getKey());
            }

            @Override
            public boolean areContentsTheSame(@NonNull TripRow a, @NonNull TripRow b) {
                // записи неизменяемы и имеют уникальный id -> одинаковый ключ = одинаковое содержимое
                return a.getKey().equals(b.getKey());
            }
        });
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TripRow.TYPE_HEADER:
                return new HeaderHolder(ItemDateHeaderBinding.inflate(inflater, parent, false));
            case TripRow.TYPE_ENTRY:
                return new EntryHolder(ItemTripEntryBinding.inflate(inflater, parent, false));
            default:
                return new AddHolder(ItemAddEntryBinding.inflate(inflater, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TripRow row = getItem(position);
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).bind((TripRow.DateHeader) row);
        } else if (holder instanceof EntryHolder) {
            ((EntryHolder) holder).bind(((TripRow.Entry) row).entry);
        } else {
            ((AddHolder) holder).bind((TripRow.AddButton) row);
        }
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        private final ItemDateHeaderBinding b;

        HeaderHolder(ItemDateHeaderBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        void bind(TripRow.DateHeader row) {
            b.tvDate.setText(row.date.format(TripFormats.DAY_HEADER));
        }
    }

    static class EntryHolder extends RecyclerView.ViewHolder {
        private final ItemTripEntryBinding b;

        EntryHolder(ItemTripEntryBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        void bind(TripEntry e) {
            BudgetCategory c = e.getCategory();
            b.tvTitle.setText(e.getTitle());
            b.ivCategory.setImageResource(c.iconRes);
            b.ivCategory.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(itemView.getContext(), c.colorRes)));

            b.tvFromAddress.setText(e.getFromAddress());
            b.tvFromTime.setText(e.getFromTime().format(TripFormats.TIME));

            if (e.isTransport()) {
                b.tvToAddress.setText(e.getToAddress());
                b.tvToTime.setText(e.getToTime().format(TripFormats.TIME));
                b.groupTo.setVisibility(View.VISIBLE);
            } else {
                b.groupTo.setVisibility(View.GONE);
            }
        }
    }

    class AddHolder extends RecyclerView.ViewHolder {
        private final ItemAddEntryBinding b;

        AddHolder(ItemAddEntryBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        void bind(TripRow.AddButton row) {
            b.getRoot().setOnClickListener(v -> listener.onAddClick(row.date));
        }
    }
}