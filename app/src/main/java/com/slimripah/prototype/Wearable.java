package com.slimripah.prototype;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Wearable extends AppCompatActivity {

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;
    private TextView stepCountTextView, heartRateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wearable);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        stepCountTextView = findViewById(R.id.stepsTextView);
        heartRateTextView = findViewById(R.id.heartRateTextView);

        requestGoogleFitPermissions();

    }

    private void requestGoogleFitPermissions() {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .build();

        GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(this, fitnessOptions);

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    account,
                    fitnessOptions
            );
        } else {
            fetchGoogleFitData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            fetchGoogleFitData();
        }
    }

    private void fetchGoogleFitData() {
        GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(this, FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .build());

        if (account == null) {
            Toast.makeText(this, "Google Fit not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_STEP_COUNT_DELTA)
                .read(DataType.TYPE_HEART_RATE_BPM)
                .setTimeRange(1, System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(this, account)
                .readData(readRequest)
                .addOnSuccessListener(response -> displayFitData(response))
                .addOnFailureListener(e -> Log.e("GoogleFit", "Failed to read data", e));
    }

    private void displayFitData(DataReadResponse response) {
        Executors.newSingleThreadExecutor().execute(() -> {
            int totalSteps = 0;
            float heartRate = 0;

            if (response.getDataSet(DataType.TYPE_STEP_COUNT_DELTA) != null) {
                for (DataPoint dp : response.getDataSet(DataType.TYPE_STEP_COUNT_DELTA).getDataPoints()) {
                    totalSteps += dp.getValue(dp.getDataType().getFields().get(0)).asInt();
                }
            }

            if (response.getDataSet(DataType.TYPE_HEART_RATE_BPM) != null) {
                for (DataPoint dp : response.getDataSet(DataType.TYPE_HEART_RATE_BPM).getDataPoints()) {
                    heartRate = dp.getValue(dp.getDataType().getFields().get(0)).asFloat();
                }
            }

            final int finalSteps = totalSteps;
            final float finalHeartRate = heartRate;

            runOnUiThread(() -> {
                stepCountTextView.setText("Steps: " + finalSteps);
                heartRateTextView.setText("Heart Rate: " + finalHeartRate + " bpm");
            });
        });
    }

}