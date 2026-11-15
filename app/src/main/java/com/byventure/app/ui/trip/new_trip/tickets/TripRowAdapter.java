package com.byventure.app.ui.trip.new_trip.tickets;

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
import com.byventure.app.databinding.ItemTripEntryBinding;
import com.byventure.app.ui.trip.new_trip.common.DateHeaderHolder;
import com.byventure.app.ui.trip.new_trip.model.TripEntry;
import com.byventure.app.ui.trip.new_trip.model.BudgetCategory;
import com.byventure.app.ui.trip.new_trip.common.TripFormats;
import com.byventure.app.ui.trip.new_trip.model.TripRow;

import java.time.LocalDate;

public class TripRowAdapter extends ListAdapter<TripRow, RecyclerView.ViewHolder> {

    public interface Listener {
        void onAddClick(LocalDate date);
        void onEntryClick(TripEntry entry);
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
                if (a instanceof TripRow.Entry && b instanceof TripRow.Entry) {
                    return ((TripRow.Entry) a).entry.equals(((TripRow.Entry) b).entry);
                }
                return true; // заголовок и кнопка определяются ключом
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
                return DateHeaderHolder.create(parent);
            case TripRow.TYPE_ENTRY:
                return new EntryHolder(ItemTripEntryBinding.inflate(inflater, parent, false));
            default:
                return new AddHolder(ItemAddEntryBinding.inflate(inflater, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TripRow row = getItem(position);
        if (holder instanceof DateHeaderHolder) {
            ((DateHeaderHolder) holder).bind(((TripRow.DateHeader) row).date);
        } else if (holder instanceof EntryHolder) {
            ((EntryHolder) holder).bind(((TripRow.Entry) row).entry);
        } else {
            ((AddHolder) holder).bind((TripRow.AddButton) row);
        }
    }



    class EntryHolder extends RecyclerView.ViewHolder {
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

            b.getRoot().setOnClickListener(v -> listener.onEntryClick(e));
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