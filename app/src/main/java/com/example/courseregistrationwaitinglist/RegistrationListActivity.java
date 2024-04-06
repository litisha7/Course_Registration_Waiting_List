package com.example.courseregistrationwaitinglist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RegistrationListActivity extends RecyclerView.Adapter<RegistrationListActivity.MyViewHolder> {

    // Private members of class
    private Context context;
    private List<DbHandler.Student> studentList;

    // Nested class
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView priority;

        // Constructor of MyViewHolder
        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            priority = view.findViewById(R.id.priority);
        }
    }

    // Constructor of RegistrationListActivity
    public RegistrationListActivity(Context context, List<DbHandler.Student> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.waiting_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DbHandler.Student item = studentList.get(position);
        holder.name.setText(item.getName());
        holder.priority.setText(item.getPriority());
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }
}
