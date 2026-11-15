package com.byventure.app.ui.trip.new_trip.events;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.byventure.app.R;
import com.byventure.app.databinding.ItemAddEntryBinding;
import com.byventure.app.databinding.ItemTripEventBinding;
import com.byventure.app.ui.trip.new_trip.common.DateHeaderHolder;
import com.byventure.app.ui.trip.new_trip.common.TripFormats;
import com.byventure.app.ui.trip.new_trip.model.TripEvent;
import com.byventure.app.ui.trip.new_trip.model.TripRow;

import java.time.LocalDate;

public class TripEventAdapter extends ListAdapter<TripRow, RecyclerView.ViewHolder> {

    public interface Listener {
        void onAddClick(LocalDate date);
        void onEventClick(TripEvent event);
    }

    private final Listener listener;

    public TripEventAdapter(Listener listener) {
        super(new DiffUtil.ItemCallback<TripRow>() {
            @Override
            public boolean areItemsTheSame(@NonNull TripRow a, @NonNull TripRow b) {
                return a.getKey().equals(b.getKey());
            }

            @Override
            public boolean areContentsTheSame(@NonNull TripRow a, @NonNull TripRow b) {
                if (a instanceof TripRow.EventItem && b instanceof TripRow.EventItem) {
                    return ((TripRow.EventItem) a).event.equals(((TripRow.EventItem) b).event);
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
            case TripRow.TYPE_EVENT:
                return new EventHolder(ItemTripEventBinding.inflate(inflater, parent, false));
            default:
                return new AddHolder(ItemAddEntryBinding.inflate(inflater, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TripRow row = getItem(position);
        if (holder instanceof DateHeaderHolder) {
            ((DateHeaderHolder) holder).bind(((TripRow.DateHeader) row).date);
        } else if (holder instanceof EventHolder) {
            ((EventHolder) holder).bind(((TripRow.EventItem) row).event);
        } else {
            ((AddHolder) holder).bind((TripRow.AddButton) row);
        }
    }

    class EventHolder extends RecyclerView.ViewHolder {
        private final ItemTripEventBinding b;

        EventHolder(ItemTripEventBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        void bind(TripEvent e) {
            b.tvTitle.setText(e.getTitle());
            b.tvTime.setText(e.getTime().format(TripFormats.TIME));
            b.tvAddress.setText(e.getAddress());
            b.getRoot().setOnClickListener(v -> listener.onEventClick(e));
        }
    }

    class AddHolder extends RecyclerView.ViewHolder {
        private final ItemAddEntryBinding b;

        AddHolder(ItemAddEntryBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        void bind(TripRow.AddButton row) {
            b.tvAdd.setText(R.string.events_add);
            b.getRoot().setOnClickListener(v -> listener.onAddClick(row.date));
        }
    }
}