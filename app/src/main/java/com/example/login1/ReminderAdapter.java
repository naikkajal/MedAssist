package com.example.login1;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private List<String> reminderList; // Stores raw strings "Name|TimeInMillis|RequestCode"

    private OnReminderDeleteListener deleteListener;

    public interface OnReminderDeleteListener {
        void onDelete(String reminderData, int position);
    }

    public ReminderAdapter(List<String> reminderList, OnReminderDeleteListener listener) {
        this.reminderList = reminderList;
        this.deleteListener = listener;
    }

    public void updateList(List<String> newList) {
        this.reminderList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String data = reminderList.get(position);
        String[] parts = data.split("\\|");

        if (parts.length >= 2) {
            String name = parts[0];
            long timeInMillis = Long.parseLong(parts[1]);

            holder.tvMedicineName.setText(name);

            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            holder.tvTime.setText(timeFormat.format(timeInMillis));
            holder.tvDate.setText(dateFormat.format(timeInMillis));

            // Delete button click
            holder.btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDelete(data, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedicineName, tvTime, tvDate;
        Button btnDelete; // Add this to your reminder_item.xml

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicineName = itemView.findViewById(R.id.tvMedicineName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnDelete = itemView.findViewById(R.id.btnDelete); // Add Button id="btnDelete" to XML
        }
    }
}
