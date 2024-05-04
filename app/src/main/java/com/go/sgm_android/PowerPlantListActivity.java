package com.go.sgm_android;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.go.sgm_android.Adapter.PowerPlantAdapter;
import com.go.sgm_android.databinding.ActivityAddPowerPlantBinding;
import com.go.sgm_android.databinding.ActivityPowerPlantListBinding;
import com.go.sgm_android.model.PowerPlant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PowerPlantListActivity extends AppCompatActivity {

    private ActivityPowerPlantListBinding binding;
    private PowerPlantAdapter powerPlantAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPowerPlantListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Change notification color
        getWindow().setStatusBarColor(getResources().getColor(R.color.glitter_lake));

        // Set the title of the activity
        setTitle("List of all Power Plant");

        //==========================================================================================
        // This is for Power Plant Recycler View
        // Initialize RecyclerView
        RecyclerView recyclerView1 = binding.powerPlantList;
        recyclerView1.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // Initialize adapters
        powerPlantAdapter = new PowerPlantAdapter(new ArrayList<>());
        // Set up adapter
        recyclerView1.setAdapter(powerPlantAdapter);
        // Fetch power plant data from Firebase
        fetchPowerPlantData();
    }

    private void fetchPowerPlantData() {
        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        DatabaseReference powerPlantRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("PowerPlant");

        powerPlantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<PowerPlant> powerPlants = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    if (snapshot.child("ppoutput").exists() && snapshot.child("ppname").exists() && snapshot.child("Date").child(currentDate).exists()) {
                        String name = snapshot.child("ppname").getValue(String.class);
                        String currentCapacity = snapshot.child("Date").child(currentDate).child("capacity").child("ppcurrentCapacity").getValue(String.class);
                        String ppOutput = snapshot.child("ppoutput").getValue(String.class);
                        // You can also fetch other fields similarly
                        PowerPlant powerPlant = new PowerPlant(name, currentCapacity, ppOutput);
                        powerPlants.add(powerPlant);
                    }
                }
                // Now you have all PowerPlant objects from powerPlantRef, update your adapter or UI here
                // If needed, add logic here to merge or process data from both references
                powerPlantAdapter.setPowerPlants(powerPlants);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PowerPlantListActivity", "Failed to fetch power plant data from powerPlantRef: " + databaseError.getMessage());
            }
        });
    }
}