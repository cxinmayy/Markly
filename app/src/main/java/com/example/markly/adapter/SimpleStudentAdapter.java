package com.example.markly.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.markly.R;
import com.example.markly.model.Student;

import java.util.ArrayList;

public class SimpleStudentAdapter extends RecyclerView.Adapter<SimpleStudentAdapter.ViewHolder> {

    ArrayList<Student> list;

    public SimpleStudentAdapter(ArrayList<Student> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, reg;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(android.R.id.text1);
            reg = itemView.findViewById(android.R.id.text2);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Student s = list.get(position);
        holder.name.setText(s.getName());
        holder.reg.setText(s.getRegNo());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}