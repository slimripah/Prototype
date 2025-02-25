package com.slimripah.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find buttons by ID
        Button wearableButton = findViewById(R.id.wearable);
        Button pointsButton = findViewById(R.id.points);

        // Set click listener for Wearable button
        wearableButton.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, Wearable.class);
            startActivity(intent);
        });

        // Set click listener for Points button
        pointsButton.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, Points.class);
            startActivity(intent);
        });

    }
}