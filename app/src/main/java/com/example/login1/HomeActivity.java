package com.example.login1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.login1.R;



import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment selected = null;

            if (id == R.id.nav_search) {
                selected = new SearchFragment();
            } else if (id == R.id.nav_saved) {
                selected = new SavedFragment();
            } else if (id == R.id.nav_reminder) {
                selected = new ReminderFragment();
            } else if (id == R.id.nav_profile) {
                selected = new ProfileFragment();
            }

            if (selected != null) {
                loadFragment(selected);
                return true;
            }
            return false;
        });


        // Handle initial navigation
        handleIntent(getIntent());
        
        // Request notification permission on startup (Android 13+)
        PermissionHelper.requestNotificationPermission(this);
    }

    @Override
    protected void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(android.content.Intent intent) {
        if (intent == null) return;
        
        String target = intent.getStringExtra("targetFragment");
        android.util.Log.d("HomeActivity", "handleIntent target: " + target);
        
        if ("reminder".equals(target)) {
            android.widget.Toast.makeText(this, "Opening Reminders...", android.widget.Toast.LENGTH_SHORT).show();
            bottomNavigationView.setSelectedItemId(R.id.nav_reminder);
            loadFragment(new ReminderFragment());
        } else {
            bottomNavigationView.setSelectedItemId(R.id.nav_search);
            loadFragment(new SearchFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }
}



