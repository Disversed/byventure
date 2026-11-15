package com.byventure.app.ui.trip.new_trip.summary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.byventure.app.R;
import com.byventure.app.databinding.ItemSummaryRowBinding;
import com.byventure.app.ui.trip.new_trip.common.DateHeaderHolder;
import com.byventure.app.ui.trip.new_trip.common.TripFormats;
import com.byventure.app.ui.trip.new_trip.model.SummaryRow;

public class SummaryAdapter extends ListAdapter<SummaryRow, RecyclerView.ViewHolder> {

    public SummaryAdapter() {
        super(new DiffUtil.ItemCallback<SummaryRow>() {
            @Override
            public boolean areItemsTheSame(@NonNull SummaryRow a, @NonNull SummaryRow b) {
                return a.getKey().equals(b.getKey());
            }

            @Override
            public boolean areContentsTheSame(@NonNull SummaryRow a, @NonNull SummaryRow b) {
                if (a instanceof SummaryRow.Point && b instanceof SummaryRow.Point) {
                    return a.equals(b);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SummaryRow.TYPE_HEADER) return DateHeaderHolder.create(parent);
        return new PointHolder(ItemSummaryRowBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SummaryRow row = getItem(position);
        if (holder instanceof DateHeaderHolder) {
            ((DateHeaderHolder) holder).bind(((SummaryRow.DateHeader) row).date);
        } else {
            ((PointHolder) holder).bind((SummaryRow.Point) row);
        }
    }

    static class PointHolder extends RecyclerView.ViewHolder {
        private final ItemSummaryRowBinding b;

        PointHolder(ItemSummaryRowBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        void bind(SummaryRow.Point p) {
            b.tvAddress.setText(p.address);
            b.tvTime.setText(p.time.format(TripFormats.TIME));

            // строки одного дня склеиваются в одну карточку: скругляем только первую и последнюю
            int background = p.first && p.last ? R.drawable.bg_card
                    : p.first ? R.drawable.bg_card_top
                    : p.last ? R.drawable.bg_card_bottom
                    : R.drawable.bg_card_middle;
            b.getRoot().setBackgroundResource(background);

            // линия таймлайна: у первой точки нет линии сверху, у последней — снизу
            b.lineTop.setVisibility(p.first ? View.GONE : View.VISIBLE);
            b.lineBottom.setVisibility(p.last ? View.GONE : View.VISIBLE);
        }
    }
}