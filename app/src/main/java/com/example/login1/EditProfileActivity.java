package com.example.login1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    EditText etName, etMobile, etDob, etBloodGroup;
    Button btnSave;
    String userId, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etName = findViewById(R.id.etEditName);
        etMobile = findViewById(R.id.etEditMobile);
        etDob = findViewById(R.id.etEditDob);
        etBloodGroup = findViewById(R.id.etEditBloodGroup);
        btnSave = findViewById(R.id.btnSaveProfile);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        email = prefs.getString("email", "");
        
        // Pre-fill fields
        etName.setText(prefs.getString("name", ""));
        etMobile.setText(prefs.getString("mobile", ""));
        etDob.setText(prefs.getString("dob", ""));
        etBloodGroup.setText(prefs.getString("bloodgroup", ""));

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();
            String dob = etDob.getText().toString().trim();
            String bloodGroup = etEditBloodGroup().toString().trim(); // Wait, typo in XML ID? etEditBloodGroup

            if (name.isEmpty() || mobile.isEmpty()) {
                Toast.makeText(this, "Name and Mobile are required", Toast.LENGTH_SHORT).show();
                return;
            }

            updateProfile(name, mobile, dob, bloodGroup);
        });
    }
    
    // Helper to fix potential typo in my XML earlier
    private String etEditBloodGroup() {
        return etBloodGroup.getText().toString();
    }

    private void updateProfile(String name, String mobile, String dob, String bloodGroup) {
        CreateUserRequest request = new CreateUserRequest(name, null, dob, email, mobile, bloodGroup);
        
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<Void> call = apiService.updateProfile(userId, request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    
                    // Update SharedPreferences
                    SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit();
                    editor.putString("name", name);
                    editor.putString("mobile", mobile);
                    editor.putString("dob", dob);
                    editor.putString("bloodgroup", bloodGroup);
                    editor.apply();
                    
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Update Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
