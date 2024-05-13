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

import com.go.sgm_android.Adapter.DistributorAdapter;
import com.go.sgm_android.Adapter.HistoryDistributorAdapter;
import com.go.sgm_android.databinding.ActivityDistributorListBinding;
import com.go.sgm_android.databinding.ActivityPowerPlantListBinding;
import com.go.sgm_android.model.Distributor;
import com.go.sgm_android.model.PowerPlant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DistributorListActivity extends AppCompatActivity {

    private ActivityDistributorListBinding binding;
    private Handler handler;
    private Runnable updateTimeRunnable;
    private DistributorAdapter distributorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDistributorListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Change notification color
        getWindow().setStatusBarColor(getResources().getColor(R.color.glitter_lake));

        // Set the title of the activity
        setTitle("List of Running Distributors");

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
        // This is for Distributor Recycler View
        // Initialize RecyclerView
        RecyclerView recyclerView2 = binding.distributorList;
        recyclerView2.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // Initialize adapters
        distributorAdapter = new DistributorAdapter(new ArrayList<>());
        // Set up adapter
        recyclerView2.setAdapter(distributorAdapter);

        fetchDataFromFirebase2();
    }

    private void fetchDataFromFirebase2() {
        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        DatabaseReference distributorRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("Distributor");
        distributorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Distributor> distributors = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Distributor keys mapping
                    Map<String, String> distributorKeysMap = new HashMap<>();
                    distributorKeysMap.put("BPDB", "Bangladesh Power Development Board");
                    distributorKeysMap.put("BREB", "Bangladesh Rural Electrification Board");
                    distributorKeysMap.put("DESCO", "Dhaka Electric Supply Company Limited");
                    distributorKeysMap.put("DPDC", "Dhaka Power Distribution Company Limited");
                    distributorKeysMap.put("NESCO", "Northern Electricity Supply Company PLC");
                    distributorKeysMap.put("WZPDCL", "West Zone Power Distribution Company Limited");

                    // Extract distributor key and name
                    String distributorKey = snapshot.getKey();
                    String distributorName = distributorKeysMap.get(distributorKey);

                    if (snapshot.child("Date").child(currentDate).exists()) {
                        float currentDemand = snapshot.child("Date").child(currentDate).child("demand").child("ddcurrentDemand").getValue(float.class);
                        float targetDemand = snapshot.child("Date").child(currentDate).child("demand").child("ddtargetdemand").getValue(float.class);
                        // You can also fetch other fields similarly
                        Distributor distributor = new Distributor(distributorKey, currentDemand, targetDemand);
                        distributors.add(distributor);
                    }
                }
                // Now you have all PowerPlant objects from powerPlantRef, update your adapter or UI here
                // If needed, add logic here to merge or process data from both references
                distributorAdapter.setDistributors(distributors);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", "Failed to fetch distributor data from distributorRef: " + databaseError.getMessage());
            }
        });
    }
}