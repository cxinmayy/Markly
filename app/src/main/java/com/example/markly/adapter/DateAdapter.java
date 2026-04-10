package com.example.markly.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.markly.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {

    public interface OnDateClickListener {
        void onDateClick(int position, String date);
    }

    public static class DateItem {
        public String dayName; // e.g. "MON"
        public String dayNum;  // e.g. "14"
        public String fullDate; // e.g. "2026-04-14"

        public DateItem(String dayName, String dayNum, String fullDate) {
            this.dayName = dayName;
            this.dayNum = dayNum;
            this.fullDate = fullDate;
        }
    }

    private final List<DateItem> items;
    private int selectedPosition;
    private final OnDateClickListener listener;

    public DateAdapter(List<DateItem> items, int selectedPosition, OnDateClickListener listener) {
        this.items = items;
        this.selectedPosition = selectedPosition;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView tvDayName, tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            card = (MaterialCardView) itemView;
            tvDayName = itemView.findViewById(R.id.tvDayName);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_date, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DateItem item = items.get(position);
        holder.tvDayName.setText(item.dayName);
        holder.tvDate.setText(item.dayNum);

        if (position == selectedPosition) {
            // Selected state: primary background, white text
            holder.card.setCardBackgroundColor(
                    holder.itemView.getContext().getColor(R.color.primary));
            holder.tvDayName.setTextColor(
                    holder.itemView.getContext().getColor(R.color.on_primary));
            holder.tvDate.setTextColor(
                    holder.itemView.getContext().getColor(R.color.on_primary));
        } else {
            // Unselected: white card, dark text
            holder.card.setCardBackgroundColor(
                    holder.itemView.getContext().getColor(R.color.surface_container_lowest));
            holder.tvDayName.setTextColor(
                    holder.itemView.getContext().getColor(R.color.on_surface_variant));
            holder.tvDate.setTextColor(
                    holder.itemView.getContext().getColor(R.color.primary_dim));
        }

        holder.itemView.setOnClickListener(v -> {
            int prev = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prev);
            notifyItemChanged(selectedPosition);
            listener.onDateClick(selectedPosition, item.fullDate);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
