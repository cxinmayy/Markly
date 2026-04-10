package com.example.markly.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.view.HapticFeedbackConstants;
import android.animation.ObjectAnimator;
import android.view.animation.DecelerateInterpolator;

import androidx.recyclerview.widget.RecyclerView;

import com.example.markly.R;
import com.example.markly.database.DatabaseHelper;
import com.example.markly.model.Student;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    ArrayList<Student> list;
    DatabaseHelper db;
    String date;
    int sectionId;
    OnStudentLongClickListener longClickListener;
    private int lastAnimatedPosition = -1;

    public interface OnStudentLongClickListener {
        void onStudentLongClick(Student student, int position);
    }

    public void setOnStudentLongClickListener(OnStudentLongClickListener listener) {
        this.longClickListener = listener;
    }

    public StudentAdapter(ArrayList<Student> list, DatabaseHelper db, String date, int sectionId) {
        this.list = list;
        this.db = db;
        this.date = date;
        this.sectionId = sectionId;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, tvId, tvAvatar, tvStatus, tvAttendancePct, tvWarning;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            tvId = itemView.findViewById(R.id.tvId);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvAttendancePct = itemView.findViewById(R.id.tvAttendancePct);
            tvWarning = itemView.findViewById(R.id.tvWarning);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Student s = list.get(position);

        holder.name.setText(s.getName());
        if (holder.tvId != null) {
            holder.tvId.setText("ID: " + s.getRegNo());
        }
        if (holder.tvAvatar != null && s.getName() != null && !s.getName().isEmpty()) {
            holder.tvAvatar.setText(String.valueOf(s.getName().charAt(0)).toUpperCase());
        }

        int status = db.getAttendanceStatus(s.getId(), date);
        applyStatusState(holder, status);

        // Attendance percentage
        float pct = db.getAttendancePercentage(s.getId(), sectionId);
        if (pct < 0) {
            holder.tvAttendancePct.setText("No records yet");
            holder.tvAttendancePct.setTextColor(holder.itemView.getContext().getColor(R.color.outline));
            holder.tvWarning.setVisibility(View.GONE);
        } else {
            String pctText = String.format("Attendance: %.0f%%", pct);
            holder.tvAttendancePct.setText(pctText);
            if (pct < 75f) {
                holder.tvAttendancePct.setTextColor(android.graphics.Color.parseColor("#BA1A1A"));
                holder.tvWarning.setVisibility(View.VISIBLE);
            } else {
                holder.tvAttendancePct.setTextColor(android.graphics.Color.parseColor("#386a20"));
                holder.tvWarning.setVisibility(View.GONE);
            }
        }

        holder.itemView.setOnLongClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            if (longClickListener != null) {
                longClickListener.onStudentLongClick(s, position);
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

    private void applyStatusState(ViewHolder holder, int status) {
        if (status == 1) { // Present
            holder.tvStatus.setText("P");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_present_selected);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.on_primary));
        } else if (status == 0) { // Absent
            holder.tvStatus.setText("A");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_absent_selected);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.on_error_container));
        } else { // Unmarked (-1)
            holder.tvStatus.setText("-");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_toggle_unselected);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.outline));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(ArrayList<Student> filtered) {
        this.list = filtered;
        notifyDataSetChanged();
    }
}