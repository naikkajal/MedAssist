package com.example.login1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class SignupActivity extends AppCompatActivity {

    EditText etName, etDOB;
    Spinner spinnerBloodGroup;
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etName = findViewById(R.id.etName);
        etDOB = findViewById(R.id.etDOB);
        spinnerBloodGroup = findViewById(R.id.spinnerBloodGroup);
        btnNext = findViewById(R.id.btnnext);

        // Date Picker for DOB
        etDOB.setOnClickListener(view -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    SignupActivity.this,
                    (view1, year1, month1, dayOfMonth) -> {
                        String selectedDate = String.format("%04d-%02d-%02d", year1, (month1 + 1), dayOfMonth);
                        etDOB.setText(selectedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        // Spinner for Blood Group
        String[] bloodGroups = {"Select Blood Group", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bloodGroups);
        spinnerBloodGroup.setAdapter(adapter);

        btnNext.setOnClickListener(view -> {
            String name = etName.getText().toString().trim();
            String dob = etDOB.getText().toString().trim();
            String bloodGroup = spinnerBloodGroup.getSelectedItem().toString();

            if (name.isEmpty() || dob.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (bloodGroup.equals("Select Blood Group")) {
                Toast.makeText(SignupActivity.this, "Please select a valid blood group", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(SignupActivity.this, SignupActivity2.class);
            intent.putExtra("name", name);
            intent.putExtra("dob", dob);
            intent.putExtra("bloodGroup", bloodGroup);
            startActivity(intent);
        });
    }
}
