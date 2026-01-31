package com.example.login1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class DescriptionActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SavedItemsPrefs";
    private static final String SAVED_ITEMS_KEY = "savedItems";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        TextView tvName = findViewById(R.id.tvProductName);
        TextView tvUses = findViewById(R.id.tvUses);
        TextView tvSideEffects = findViewById(R.id.tvSideEffects);
        TextView tvManufacturer = findViewById(R.id.tvManufacturer);
        TextView tvImageUrl = findViewById(R.id.tvImageUrl);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnSaveReminder = findViewById(R.id.btnSaveReminder);

        // Intent data
        String productname = getIntent().getStringExtra("productname");
        String uses = getIntent().getStringExtra("uses");
        String sideeffects = getIntent().getStringExtra("sideeffects");
        String manufacturer = getIntent().getStringExtra("manufacturer");
        String imageurl = getIntent().getStringExtra("imageurl");

        // Set UI
        tvName.setText(productname);
        tvUses.setText(formatListText(uses));
        tvSideEffects.setText(formatListText(sideeffects));
        tvManufacturer.setText(manufacturer);

        tvImageUrl.setText(imageurl);
        Linkify.addLinks(tvImageUrl, Linkify.WEB_URLS);
        tvImageUrl.setMovementMethod(LinkMovementMethod.getInstance());

        SharedPreferences prefs =
                getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Set<String> set =
                prefs.getStringSet(SAVED_ITEMS_KEY, new HashSet<>());

        Set<String> updatedSet = new HashSet<>();
        if (set != null) {
            updatedSet.addAll(set);
        }

        // Already saved
        if (updatedSet.contains(productname)) {
            btnSave.setText("Saved");
            btnSave.setEnabled(false);
        } else {
            btnSave.setOnClickListener(v -> {

                updatedSet.add(productname);
                prefs.edit()
                        .putStringSet(SAVED_ITEMS_KEY, updatedSet)
                        .apply();

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(productname + "_uses", uses);
                editor.putString(productname + "_sideeffects", sideeffects);
                editor.putString(productname + "_manufacturer", manufacturer);
                editor.putString(productname + "_imageurl", imageurl);
                editor.apply();

                btnSave.setText("Saved");
                btnSave.setEnabled(false);

                Toast.makeText(this,
                        "Saved to Favorites!",
                        Toast.LENGTH_SHORT).show();
            });
        }


        btnSaveReminder.setOnClickListener(v -> {
            // Check permissions first
            if (!PermissionHelper.hasNotificationPermission(this)) {
                PermissionHelper.requestNotificationPermission(this);
                Toast.makeText(this, "Please grant notification permission to set reminders", Toast.LENGTH_LONG).show();
                return;
            }
            
            if (!PermissionHelper.canScheduleExactAlarms(this)) {
                PermissionHelper.requestExactAlarmPermission(this);
                Toast.makeText(this, "Please grant exact alarm permission to set reminders", Toast.LENGTH_LONG).show();
                return;
            }
            
            showReminderDialog(productname);
        });
    }

    private void showReminderDialog(String medicineName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reminder, null);
        builder.setView(dialogView);

        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        timePicker.setIs24HourView(true);

        builder.setPositiveButton("Save Reminder", (dialog, which) -> {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                    timePicker.getHour(), timePicker.getMinute(), 0);

            long timeInMillis = calendar.getTimeInMillis();
            
            // Check if time is in the past
            if (timeInMillis <= System.currentTimeMillis()) {
                Toast.makeText(this, "Please select a future date and time!", Toast.LENGTH_LONG).show();
                return;
            }

            // Schedule Alarm
            android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);
            android.content.Intent intent = new android.content.Intent(this, AlarmReceiver.class);
            intent.putExtra("medicineName", medicineName);

            int requestCode = (int) System.currentTimeMillis(); // Unique ID
            android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(this, requestCode, intent,
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);

            if (alarmManager != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                     if (alarmManager.canScheduleExactAlarms()) {
                         alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                     } else {
                         // Fallback or request permission - for now just set inexact or try setExact
                         alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                     }
                } else {
                    alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                }
            }

            // Save to SharedPreferences
            // Format: "MedicineName|TimeInMillis|RequestCode"
            String reminderData = medicineName + "|" + timeInMillis + "|" + requestCode;
            
            SharedPreferences prefs = getSharedPreferences("Reminders", MODE_PRIVATE);
            Set<String> reminders = new HashSet<>(prefs.getStringSet("AllReminders", new HashSet<>()));
            reminders.add(reminderData);
            
            prefs.edit().putStringSet("AllReminders", reminders).commit();
            android.util.Log.d("DescriptionActivity", "Reminder saved | Data: " + reminderData);

            Toast.makeText(DescriptionActivity.this, "Reminder saved! Navigating to list...", Toast.LENGTH_SHORT).show();

            // Navigate to Reminders page in HomeActivity
            android.content.Intent homeIntent = new android.content.Intent(DescriptionActivity.this, HomeActivity.class);
            homeIntent.putExtra("targetFragment", "reminder");
            homeIntent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(homeIntent);
            finish();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private String formatListText(String data) {
        if (data == null || data.trim().isEmpty()) {
            return "Not available";
        }
        return data
                .replace(",", "\n")
                .replaceAll("(?<=\\D)(?=\\p{Upper})", "\n");
    }
}
