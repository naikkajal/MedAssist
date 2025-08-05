package com.example.login1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
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


        // Get intent data
        String productname = getIntent().getStringExtra("productname");
        String uses = getIntent().getStringExtra("uses");
        String sideeffects = getIntent().getStringExtra("sideeffects");
        String manufacturer = getIntent().getStringExtra("manufacturer");
        String imageurl = getIntent().getStringExtra("imageurl");

        // Set values
        tvName.setText("Name: " + productname);
        tvUses.setText("Uses: " + uses);
        tvSideEffects.setText("Side Effects: " + sideeffects);
        tvManufacturer.setText("Manufacturer: " + manufacturer);

        tvImageUrl.setText("Image URL: " + imageurl);
        Linkify.addLinks(tvImageUrl, Linkify.WEB_URLS);
        tvImageUrl.setMovementMethod(LinkMovementMethod.getInstance());

        Log.d("DESCRIPTION_ACTIVITY", "Product Name: " + productname);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(SAVED_ITEMS_KEY, new HashSet<>());
        Set<String> updatedSet = new HashSet<>(set);

        // Check if already saved
        if (set.contains(productname)) {
            btnSave.setText("Saved");
            btnSave.setEnabled(false);
        } else {
            btnSave.setOnClickListener(v -> {
                updatedSet.add(productname);
                prefs.edit().putStringSet(SAVED_ITEMS_KEY, updatedSet).apply();

                // 👇 Save full product info using unique keys
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(productname + "_uses", uses);
                editor.putString(productname + "_sideeffects", sideeffects);
                editor.putString(productname + "_manufacturer", manufacturer);
                editor.putString(productname + "_imageurl", imageurl);
                editor.apply();

                Toast.makeText(this, "Saved to Favorites!", Toast.LENGTH_SHORT).show();
                btnSave.setText("Saved");
                btnSave.setEnabled(false);
            });
        }
        btnSaveReminder.setOnClickListener(v -> showReminderDialog(productname));
    }

    private void showReminderDialog(String medicineName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reminder, null);
        builder.setView(dialogView);
        builder.setTitle("Set Reminder Date and Time");

        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);

        builder.setPositiveButton("Save Reminder", (dialog, which) -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1;
            int year = datePicker.getYear();

            String reminderTime = String.format("%02d:%02d", hour, minute);
            String reminderDate = String.format("%02d/%02d/%04d", day, month, year);
            String reminder = reminderDate + " at " + reminderTime;

            SharedPreferences prefs = getSharedPreferences("Reminders", MODE_PRIVATE);
            Set<String> reminders = prefs.getStringSet(medicineName, new HashSet<>());

            Set<String> updatedReminders = new HashSet<>(reminders);
            updatedReminders.add(reminder);

            prefs.edit().putStringSet(medicineName, updatedReminders).apply();

            Toast.makeText(this, "Reminder saved for " + medicineName, Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


}


