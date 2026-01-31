package com.example.login1;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReminderFragment extends Fragment {

    private RecyclerView rvReminders;
    private ReminderAdapter adapter;
    private List<String> reminderList = new ArrayList<>();
    private android.widget.TextView tvEmptyState;

    public ReminderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);
        rvReminders = view.findViewById(R.id.rvReminders);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        rvReminders.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadReminders();
    }

    private void loadReminders() {
        if (getContext() == null) return;

        SharedPreferences prefs = getContext().getSharedPreferences("Reminders", Context.MODE_PRIVATE);
        Set<String> remindersSet = prefs.getStringSet("AllReminders", new HashSet<>());

        Log.d("ReminderFragment", "Loading reminders. Count: " + remindersSet.size());

        Log.d("ReminderFragment", "Loading reminders from SharedPreferences...");
        reminderList.clear();
        for (String s : remindersSet) {
            Log.d("ReminderFragment", "Found Reminder: " + s);
            reminderList.add(s);
        }
        Log.d("ReminderFragment", "Total reminders in list: " + reminderList.size());

        if (adapter == null) {
            Log.d("ReminderFragment", "Creating new adapter");
            adapter = new ReminderAdapter(reminderList, this::deleteReminder);
            rvReminders.setAdapter(adapter);
        } else {
            Log.d("ReminderFragment", "Updating existing adapter");
            adapter.updateList(reminderList); // Call update method
        }
        
        // Show/hide empty state
        if (reminderList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvReminders.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvReminders.setVisibility(View.VISIBLE);
        }
    }

    private void deleteReminder(String reminderData, int position) {
        SharedPreferences prefs = requireContext().getSharedPreferences("Reminders", Context.MODE_PRIVATE);
        Set<String> reminders = new HashSet<>(prefs.getStringSet("AllReminders", new HashSet<>()));
        reminders.remove(reminderData);
        prefs.edit().putStringSet("AllReminders", reminders).apply();

        // Cancel alarm
        cancelAlarm(reminderData);

        // Reload the entire list to update empty state properly
        loadReminders();

        Toast.makeText(requireContext(), "Reminder deleted!", Toast.LENGTH_SHORT).show();
    }

    private void cancelAlarm(String reminderData) {
        String[] parts = reminderData.split("\\|");
        if (parts.length >= 3) {
            try {
                int requestCode = Integer.parseInt(parts[2]);
                AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(requireContext(), AlarmReceiver.class);
                intent.putExtra("medicineName", parts[0]);
                android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(
                        requireContext(), requestCode, intent,
                        android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
                if (alarmManager != null) {
                    alarmManager.cancel(pendingIntent);
                }
            } catch (NumberFormatException e) {
                Log.e("ReminderFragment", "Invalid requestCode: " + parts[2]);
            }
        }
    }
}
