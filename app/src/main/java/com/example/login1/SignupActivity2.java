package com.example.login1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity2 extends AppCompatActivity {

    EditText etEmail, etMobile, etPassword;
    Button btnSignup;
    TextView tvLoginRedirect;
    
    String name, dob, bloodGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        // Retrieve data from previous activity
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("name");
            dob = intent.getStringExtra("dob");
            bloodGroup = intent.getStringExtra("bloodGroup");
        }

        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);
        tvLoginRedirect = findViewById(R.id.tvLoginRedirect);

        btnSignup.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || mobile.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignupActivity2.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (name == null) name = "";
            if (dob == null) dob = "";
            if (bloodGroup == null) bloodGroup = "";

            CreateUserRequest request = new CreateUserRequest(name, password, dob, email, mobile, bloodGroup);

            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            Call<CreateUserResponse> call = apiService.signupUser(request);

            call.enqueue(new Callback<CreateUserResponse>() {
                @Override
                public void onResponse(Call<CreateUserResponse> call, Response<CreateUserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(SignupActivity2.this, "Signup Successful!", Toast.LENGTH_SHORT).show();
                        
                        // Save user details to SharedPreferences so profile page works immediately
                        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("userId", response.body().getUserid());
                        editor.putString("name", name);
                        editor.putString("email", email);
                        editor.putString("mobile", mobile);
                        editor.putString("dob", dob);
                        editor.putString("bloodgroup", bloodGroup);
                        editor.apply();

                        Intent homeIntent = new Intent(SignupActivity2.this, HomeActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(homeIntent);
                        finish();
                    } else {
                        Toast.makeText(SignupActivity2.this, "Signup Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CreateUserResponse> call, Throwable t) {
                    Toast.makeText(SignupActivity2.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvLoginRedirect.setOnClickListener(view -> {
            Intent loginIntent = new Intent(SignupActivity2.this, LoginActivity.class);
            startActivity(loginIntent);
        });
    }
}
