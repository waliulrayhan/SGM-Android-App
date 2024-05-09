package com.go.sgm_android;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.go.sgm_android.databinding.ActivityDistributorDetailsBinding;
import com.go.sgm_android.databinding.ActivityPowerPlantDetailsBinding;

public class DistributorDetailsActivity extends AppCompatActivity {

    ActivityDistributorDetailsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDistributorDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Change notification color
        getWindow().setStatusBarColor(getResources().getColor(R.color.glitter_lake));

        // Set the title of the activity
        setTitle("Distributor Details");

        // Get data passed from DistributorAdapter
        String distributorName = getIntent().getStringExtra("DISTRIBUTOR_NAME");
        float currentDemand = getIntent().getFloatExtra("CURRENT_DEMAND", 0);
        float targetDemand = getIntent().getFloatExtra("TARGET_DEMAND", 0);

        // Set data to the views
        TextView textViewDistributorName = findViewById(R.id.textViewDistributorName);
        TextView textViewCurrentDemand = findViewById(R.id.textViewCurrentDemand);
        TextView textViewTargetDemand = findViewById(R.id.textViewTargetDemand);

        textViewDistributorName.setText(distributorName);
        textViewCurrentDemand.setText("Current Demand: " + currentDemand + " MW");
        textViewTargetDemand.setText("Target Demand: " + targetDemand + " MW");
    }
}