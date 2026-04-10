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
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;

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
    private final String todayDate;
    private final OnDateClickListener listener;

    public DateAdapter(List<DateItem> items, int selectedPosition, String todayDate, OnDateClickListener listener) {
        this.items = items;
        this.selectedPosition = selectedPosition;
        this.todayDate = todayDate;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView tvDayName, tvDate;
        AnimatorSet pulseAnimator;

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

        // Today Glow Pulse Animation
        if (item.fullDate != null && item.fullDate.equals(todayDate)) {
            if (holder.pulseAnimator == null) {
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(holder.card, View.SCALE_X, 1f, 1.05f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(holder.card, View.SCALE_Y, 1f, 1.05f);
                scaleX.setRepeatCount(ValueAnimator.INFINITE);
                scaleY.setRepeatCount(ValueAnimator.INFINITE);
                scaleX.setRepeatMode(ValueAnimator.REVERSE);
                scaleY.setRepeatMode(ValueAnimator.REVERSE);

                holder.pulseAnimator = new AnimatorSet();
                holder.pulseAnimator.playTogether(scaleX, scaleY);
                holder.pulseAnimator.setDuration(900);
            }
            if (!holder.pulseAnimator.isStarted()) {
                holder.pulseAnimator.start();
            }
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.pulseAnimator != null) {
            holder.pulseAnimator.cancel();
        }
        holder.card.setScaleX(1f);
        holder.card.setScaleY(1f);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
