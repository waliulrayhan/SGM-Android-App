package com.go.sgm_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.go.sgm_android.databinding.ActivityPowerPlantDetailsBinding;
import com.go.sgm_android.databinding.ActivityPowerPlantListBinding;

public class PowerPlantDetailsActivity extends AppCompatActivity {

    ActivityPowerPlantDetailsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPowerPlantDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Change notification color
        getWindow().setStatusBarColor(getResources().getColor(R.color.glitter_lake));

        // Set the title of the activity
        setTitle("Power Plant Details");

        // Get data passed from PowerPlantAdapter
        Intent intent = getIntent();
        String powerPlantName = intent.getStringExtra("POWER_PLANT_NAME");
        float currentCapacity = intent.getFloatExtra("CURRENT_CAPACITY", 0);
        float targetCapacity = intent.getFloatExtra("TARGET_CAPACITY", 0);

        // Set data to the views
        TextView textViewPowerPlantName = findViewById(R.id.textViewPowerPlantName);
        TextView textViewCurrentCapacity = findViewById(R.id.textViewCurrentCapacity);
        TextView textViewTargetCapacity = findViewById(R.id.textViewTargetCapacity);

        textViewPowerPlantName.setText(powerPlantName);
        textViewCurrentCapacity.setText("Current Capacity: " + currentCapacity + " MW");
        textViewTargetCapacity.setText("Target Capacity: " + targetCapacity + " MW");
    }
}