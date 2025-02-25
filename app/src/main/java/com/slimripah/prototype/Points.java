package com.slimripah.prototype;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class Points extends AppCompatActivity {

    private TextInputEditText ageInput, stepsInput;
    private TextView resultText;
    private Button calculateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_points);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        ageInput = findViewById(R.id.ageInput);
        stepsInput = findViewById(R.id.stepsInput);
        resultText = findViewById(R.id.inputTitle3);
        calculateButton = findViewById(R.id.points);

        // Button Click Listener
        calculateButton.setOnClickListener(v -> calculatePoints());

    }

    private void calculatePoints() {
        // Get user input
        String ageStr = ageInput.getText().toString();
        String stepsStr = stepsInput.getText().toString();

        // Validate input
        if (ageStr.isEmpty() || stepsStr.isEmpty()) {
            Toast.makeText(this, "Please enter both age and steps", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);
        int steps = Integer.parseInt(stepsStr);

        // Calculate points per 10,000 steps
        int pointsPer10k = 0;

        if (age >= 20 && age <= 25) {
            pointsPer10k = 1;
        } else if (age >= 26 && age <= 36) {
            pointsPer10k = 3;
        } else if (age >= 37 && age <= 47) {
            pointsPer10k = 5;
        } else if (age >= 48 && age <= 58) {
            pointsPer10k = 7;
        } else if (age >= 59 && age <= 79) {
            pointsPer10k = 10;
        } else if (age >= 80) {
            pointsPer10k = 15;
        }

        // Calculate total points
        int totalPoints = (steps / 10000) * pointsPer10k;

        // Update the TextView with the result
        resultText.setText("Points Awarded: " + totalPoints);
    }

}