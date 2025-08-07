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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.login1.R;

import org.json.JSONObject;
import org.json.JSONException;

public class ProfileFragment extends Fragment {

    private TextView nameTextView, emailTextView, mobileTextView, dobTextView, bloodGroupTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize TextViews
        nameTextView = view.findViewById(R.id.nameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        mobileTextView = view.findViewById(R.id.mobileTextView);
        dobTextView = view.findViewById(R.id.dobTextView);
        bloodGroupTextView = view.findViewById(R.id.bloodGroupTextView);
        Button btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            // Clear SharedPreferences
            SharedPreferences prefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            // Go back to LoginActivity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear backstack
            startActivity(intent);
        });

        // Fetch and display user data
        fetchUserDetails();

        return view;

    }

    private void fetchUserDetails() {
        SharedPreferences prefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        String name = prefs.getString("name", null);


        Log.d("ProfileFragment", "Fetching user with ID: " + userId);

        if (userId == null) {
            Log.e("ProfileFragment", "User ID not found in SharedPreferences.");
            return;
        }

        String url = "http://10.0.2.2:8081/api/user/" + userId;  // if emulator
        // OR use local IP if physical device, like http://192.168.1.X:8080

        Log.d("ProfileFragment", "API URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("ProfileFragment", "API Response: " + response.toString());
                    try {
                        nameTextView.setText("Name: " + response.getString("name"));
                        emailTextView.setText("Email: " + response.getString("email"));
                        mobileTextView.setText("Mobile: " + response.getString("mobile"));
                        dobTextView.setText("DOB: " + response.getString("dob"));
                        bloodGroupTextView.setText("Blood Group: " + response.getString("bloodgroup"));
                    } catch (JSONException e) {
                        Log.e("ProfileFragment", "JSON parsing error: ", e);
                    }
                },
                error -> {
                    Log.e("ProfileFragment", "API Error: ", error);
                });

        queue.add(request);
    }

}
