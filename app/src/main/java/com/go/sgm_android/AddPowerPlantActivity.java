package com.go.sgm_android;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.go.sgm_android.databinding.ActivityAddPowerPlantBinding;

public class AddPowerPlantActivity extends AppCompatActivity {

    private ActivityAddPowerPlantBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPowerPlantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Change notification color
        getWindow().setStatusBarColor(getResources().getColor(R.color.glitter_lake));

        // Set the title of the activity
        setTitle("Add Power Plant");

        binding.addPowerPlantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.addPowerPlantEditText.getText().toString().trim();
                Toast.makeText(AddPowerPlantActivity.this, "Hello! "+name, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
