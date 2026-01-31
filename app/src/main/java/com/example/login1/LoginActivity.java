package com.example.login1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity {

    EditText etName, etPassword;
    Button btnLogin;
    TextView tvSignupRedirect;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 🔒 Force Light Mode (Disable Dark Mode completely)
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);

        // Check if user is already logged in
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        if (prefs.getString("userId", null) != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        requestNotificationPermission();

        setContentView(R.layout.activity_login);

        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignupRedirect = findViewById(R.id.tvSignupRedirect);

        btnLogin.setOnClickListener(view -> {
            String name = etName.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            LoginRequest request = new LoginRequest(name, password);
            ApiService api = RetrofitClient.getInstance().create(ApiService.class);
            Call<LoginResponse> call = api.loginUser(request);

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();

                        // ✅ Save to SharedPreferences
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("userId", loginResponse.getUserid());
                        editor.putString("name", loginResponse.getName());
                        editor.putString("email", loginResponse.getEmail());
                        editor.putString("mobile", loginResponse.getMobile());
                        editor.putString("dob", loginResponse.getDob());
                        editor.putString("bloodgroup", loginResponse.getBloodGroup());
                        editor.apply();

                        // ✅ Start HomeActivity
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();

                        // Logging for debugging
                        Log.e("LoginFailed", "Response code: " + response.code());
                        Log.e("LoginFailed", "Response body: " + response.body());
                        Log.e("LoginFailed", "Error body: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvSignupRedirect.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }
}
