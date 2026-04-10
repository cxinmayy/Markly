package com.example.markly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.markly.R;
import com.example.markly.model.Section;

import java.util.ArrayList;
import android.content.Intent;
import android.view.HapticFeedbackConstants;
import android.animation.ObjectAnimator;
import android.view.animation.DecelerateInterpolator;
import com.example.markly.ui.AttendanceActivity;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ViewHolder> {

    Context context;
    ArrayList<Section> list;
    OnSectionLongClickListener longClickListener;
    private int lastAnimatedPosition = -1;

    public interface OnSectionLongClickListener {
        void onSectionLongClick(Section section, int position);
    }

    public void setOnSectionLongClickListener(OnSectionLongClickListener listener) {
        this.longClickListener = listener;
    }

    public SectionAdapter(Context context, ArrayList<Section> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSection, tvSubject, tvInitial;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSection = itemView.findViewById(R.id.tvSection);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvInitial = itemView.findViewById(R.id.tvInitial);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_section, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Section s = list.get(position);

        holder.tvSection.setText(s.getDepartment() + " - " + s.getSection());
        holder.tvSubject.setText(s.getSubject());

        // First letter of department in squircle
        if (holder.tvInitial != null && s.getDepartment() != null && !s.getDepartment().isEmpty()) {
            holder.tvInitial.setText(String.valueOf(s.getDepartment().charAt(0)).toUpperCase());
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AttendanceActivity.class);

            // existing
            intent.putExtra("section_id", s.getId());

            // ✅ ADD THIS (IMPORTANT)
            String title = s.getDepartment() + " - " + s.getSection();
            String subtitle = s.getSubject();
            intent.putExtra("title", title);
            intent.putExtra("subtitle", subtitle);

            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            if (longClickListener != null) {
                longClickListener.onSectionLongClick(s, position);
            }
            return true;
        });

        // Staggered entrance animation
        if (position > lastAnimatedPosition) {
            holder.itemView.setTranslationY(40f);
            holder.itemView.setAlpha(0f);
            
            ObjectAnimator translation = ObjectAnimator.ofFloat(holder.itemView, View.TRANSLATION_Y, 40f, 0f);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(holder.itemView, View.ALPHA, 0f, 1f);
            
            translation.setInterpolator(new DecelerateInterpolator(1.5f));
            alpha.setInterpolator(new DecelerateInterpolator(1.5f));
            
            translation.setDuration(400);
            alpha.setDuration(400);
            
            long delay = Math.min(position, 6) * 60L;
            translation.setStartDelay(delay);
            alpha.setStartDelay(delay);
            
            translation.start();
            alpha.start();
            
            lastAnimatedPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(ArrayList<Section> filteredList) {
        this.list = filteredList;
        notifyDataSetChanged();
    }
}