package com.example.login1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.login1.R;

import org.json.JSONObject;
import org.json.JSONException;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail, tvMobile, tvDob, tvBloodGroup;
    private View btnEditProfile, btnChangePassword;
    private Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Views
        tvName = view.findViewById(R.id.tvProfileName);
        tvEmail = view.findViewById(R.id.tvProfileEmail);
        tvMobile = view.findViewById(R.id.tvProfileMobile);
        tvDob = view.findViewById(R.id.tvProfileDob);
        tvBloodGroup = view.findViewById(R.id.tvProfileBloodGroup);
        
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Load cached data from SharedPreferences immediately
        SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        tvName.setText(prefs.getString("name", "Loading..."));
        tvEmail.setText(prefs.getString("email", "Loading..."));
        tvMobile.setText(prefs.getString("mobile", "Loading..."));
        tvDob.setText(prefs.getString("dob", "Loading..."));
        tvBloodGroup.setText(prefs.getString("bloodgroup", "Loading..."));

        // Logout logic
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Edit Profile navigation
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        // Change Password navigation
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        // Fetch and display user data
        fetchUserDetails();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchUserDetails();
    }

    private void fetchUserDetails() {
        Context context = getContext();
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);

        if (userId == null) {
            Log.e("ProfileFragment", "User ID not found in SharedPreferences.");
            return;
        }

        String url = "http://10.0.2.2:8081/api/user/" + userId;

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        tvName.setText(response.optString("name", "N/A"));
                        tvEmail.setText(response.optString("email", "N/A"));
                        tvMobile.setText(response.optString("mobile", "N/A"));
                        tvDob.setText(response.optString("dob", "N/A"));
                        
                        String bloodGroup = response.optString("bloodGroup", "");
                        if (bloodGroup.isEmpty()) {
                            bloodGroup = response.optString("bloodgroup", "Not specified");
                        }
                        
                        if (bloodGroup == null || bloodGroup.equals("null") || bloodGroup.isEmpty()) {
                            bloodGroup = "Not specified";
                        }
                        tvBloodGroup.setText(bloodGroup);

                        // Save updated data to prefs for later use if needed
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("name", response.optString("name"));
                        editor.putString("email", response.optString("email"));
                        editor.putString("mobile", response.optString("mobile"));
                        editor.putString("dob", response.optString("dob"));
                        editor.putString("bloodgroup", bloodGroup);
                        editor.apply();

                    } catch (Exception e) {
                        Log.e("ProfileFragment", "Error updating UI: ", e);
                    }
                },
                error -> {
                    Log.e("ProfileFragment", "API Error: ", error);
                    Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }
}
