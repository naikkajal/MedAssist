package com.example.login1;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AlarmActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private String medicineName;
    private TextView tvCurrentTime;
    private android.os.Handler handler = new android.os.Handler();
    private Runnable timeUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Show on lock screen and turn screen on
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (keyguardManager != null) {
                keyguardManager.requestDismissKeyguard(this, null);
            }
        } else {
            getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            );
        }

        setContentView(R.layout.activity_alarm);

        // Get medicine name from intent
        medicineName = getIntent().getStringExtra("medicineName");
        if (medicineName == null) medicineName = "Medicine";

        // Set up UI
        TextView tvAlarmMessage = findViewById(R.id.tvAlarmMessage);
        TextView tvMedicineName = findViewById(R.id.tvMedicineName);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        Button btnDismiss = findViewById(R.id.btnDismiss);
        Button btnSnooze = findViewById(R.id.btnSnooze);

        tvAlarmMessage.setText("Time to take your medicine!");
        tvMedicineName.setText(medicineName);

        // Start live clock
        startClock();

        // Start alarm sound and vibration
        startAlarm();

        // Dismiss button
        btnDismiss.setOnClickListener(v -> {
            stopAlarm();
            finish();
        });

        // Snooze button (5 minutes)
        btnSnooze.setOnClickListener(v -> {
            stopAlarm();
            scheduleSnooze();
            finish();
        });
    }

    private void startClock() {
        timeUpdater = new Runnable() {
            @Override
            public void run() {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm:ss a", java.util.Locale.getDefault());
                if (tvCurrentTime != null) {
                    tvCurrentTime.setText(sdf.format(new java.util.Date()));
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(timeUpdater);
    }

    private void startAlarm() {
        // Start vibration
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            long[] pattern = {0, 1000, 500, 1000, 500, 1000};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
            } else {
                vibrator.vibrate(pattern, 0);
            }
        }

        // Start alarm sound
        try {
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, alarmUri);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopAlarm() {
        if (vibrator != null) {
            vibrator.cancel();
        }

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void scheduleSnooze() {
        long snoozeTime = System.currentTimeMillis() + (5 * 60 * 1000);
        
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);
        android.content.Intent intent = new android.content.Intent(this, AlarmReceiver.class);
        intent.putExtra("medicineName", medicineName);
        
        int requestCode = (int) System.currentTimeMillis();
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(
            this, requestCode, intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
        );
        
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent);
                } else {
                    alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent);
                }
            } else {
                alarmManager.setExact(android.app.AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent);
            }
        }
        
        android.widget.Toast.makeText(this, "Snoozed for 5 minutes", android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAlarm();
        if (handler != null && timeUpdater != null) {
            handler.removeCallbacks(timeUpdater);
        }
    }

    @Override
    public void onBackPressed() {
        // Prevent back button from dismissing alarm
    }
}
