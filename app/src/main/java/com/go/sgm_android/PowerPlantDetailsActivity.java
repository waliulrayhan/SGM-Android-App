package com.go.sgm_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.go.sgm_android.databinding.ActivityPowerPlantDetailsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

        fetchPowerPlantDetailsData(powerPlantName);
    }

    private void fetchPowerPlantDetailsData(String powerPlantName) {
        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        DatabaseReference powerPlantRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("PowerPlant");

        powerPlantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    if (snapshot.child("ppname").exists() && snapshot.child("ppname").getValue(String.class).equals(powerPlantName)) {
                        String name = snapshot.child("ppname").getValue(String.class);
                        String division = snapshot.child("ppdivision").getValue(String.class);
                        String district = snapshot.child("ppdistrict").getValue(String.class);
                        String upazilla = snapshot.child("ppupazilla").getValue(String.class);
                        String operator = snapshot.child("ppoperator").getValue(String.class);
                        String ownership = snapshot.child("ppownership").getValue(String.class);
                        String fuelType = snapshot.child("ppfuelType").getValue(String.class);
                        String method = snapshot.child("ppmethod").getValue(String.class);
                        String output = snapshot.child("ppoutput").getValue(String.class);

                        binding.ppName.setText(name);
                        binding.ppDivision.setText(division);
                        binding.ppDistrict.setText(district);
                        binding.ppUpazilla.setText(upazilla);
                        binding.ppOperator.setText(operator);
                        binding.ppOwnership.setText(ownership);
                        binding.ppFuelType.setText(fuelType);
                        binding.ppMethod.setText(method);
                        binding.ppOutput.setText(output+" MW");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PowerPlantListActivity", "Failed to fetch power plant data from powerPlantRef: " + databaseError.getMessage());
            }
        });
    }
}