package com.go.sgm_android;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

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
    private Handler handler;
    private Runnable updateTimeRunnable;
    private PowerPlantAdapter powerPlantAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPowerPlantListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Change notification color
        getWindow().setStatusBarColor(getResources().getColor(R.color.glitter_lake));

        // Set the title of the activity
        setTitle("List of Running Power Plant");

        //==========================================================================================
        // This is for Time and Date
        // Initialize TextViews for displaying current time and date
        final TextView currentTimeTextView = binding.currentTime;
        final TextView currentDateTextView = binding.currentDate;

        // Initialize Handler for updating time and date every second
        handler = new Handler();
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                // Get current time and date
                long currentTimeMillis = System.currentTimeMillis();
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a", Locale.getDefault());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                String currentTimeString = timeFormat.format(new Date(currentTimeMillis));
                String currentDateString = dateFormat.format(new Date(currentTimeMillis));

                // Update TextViews
                currentTimeTextView.setText("Time: "+currentTimeString);
                currentDateTextView.setText("Date: "+currentDateString);

                // Schedule the next update after 1 second
                handler.postDelayed(this, 1000);
            }
        };

        // Start updating time and date
        handler.post(updateTimeRunnable);

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
                    if (snapshot.child("ppname").exists() && snapshot.child("Date").child(currentDate).exists()) {
                        String name = snapshot.child("ppname").getValue(String.class);
                        float currentCapacity = snapshot.child("Date").child(currentDate).child("capacity").child("ppcurrentCapacity").getValue(float.class);
                        float targetCapacity = snapshot.child("Date").child(currentDate).child("capacity").child("pptargetCapacity").getValue(float.class);
                        float totalCapacity = snapshot.child("Date").child(currentDate).child("total").child("pptotalCurrentCapacity").getValue(float.class);
                        // You can also fetch other fields similarly
                        PowerPlant powerPlant = new PowerPlant(name, currentCapacity, targetCapacity, totalCapacity);
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