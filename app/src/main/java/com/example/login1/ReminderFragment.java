package com.example.login1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.HashSet;
import java.util.Set;

public class ReminderFragment extends Fragment {

    public ReminderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        TextView reminderText = view.findViewById(R.id.tvReminders);

        SharedPreferences prefs = requireActivity().getSharedPreferences("Reminders", Context.MODE_PRIVATE);
        StringBuilder allReminders = new StringBuilder();

        for (String medicine : prefs.getAll().keySet()) {
            Set<String> reminders = prefs.getStringSet(medicine, new HashSet<>());
            if (reminders == null || reminders.isEmpty()) continue;

            allReminders.append("🔹 ").append(medicine).append("\n");
            for (String reminder : reminders) {
                allReminders.append("    • ").append(reminder).append("\n");
            }
            allReminders.append("\n");
        }

        reminderText.setText(allReminders.toString().trim());
        return view;
    }


}
