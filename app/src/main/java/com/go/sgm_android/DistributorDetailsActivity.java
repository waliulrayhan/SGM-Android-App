package com.go.sgm_android;

import android.os.Bundle;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.go.sgm_android.Adapter.SingleDDAdapter;
import com.go.sgm_android.databinding.ActivityDistributorDetailsBinding;
import com.go.sgm_android.model.Distributor;
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

public class DistributorDetailsActivity extends AppCompatActivity {

    ActivityDistributorDetailsBinding binding;
    private SingleDDAdapter distributorAdapter;
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

        //==========================================================================================
        // This is for Distributor Recycler View
        // Initialize RecyclerView
        RecyclerView recyclerView = binding.ddSingleNameList;
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // Initialize adapters
        distributorAdapter = new SingleDDAdapter(new ArrayList<>());
        // Set up adapter
        recyclerView.setAdapter(distributorAdapter);

        fetchDistributorDetailsData(distributorName);
    }

    private void fetchDistributorDetailsData(String distributorName) {
        binding.ddName.setText(distributorName);

        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        DatabaseReference distributorRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("Distributor").child(distributorName);
        distributorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Distributor> distributors = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (snapshot.exists() && snapshot.hasChild("ddname") && snapshot.hasChild("ddzone") && snapshot.hasChild("ddcircle")) {
                        String name = snapshot.child("ddname").getValue(String.class);
                        String zone = snapshot.child("ddzone").getValue(String.class);
                        String circle = snapshot.child("ddcircle").getValue(String.class);

                        if (name != null && zone != null && circle != null) {
                            Distributor distributor = new Distributor(name, zone, circle);
                            distributors.add(distributor);
                        }
                    }
                }
                // Update the adapter with the new list of distributors
                distributorAdapter.setDistributors(distributors);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", "Failed to fetch distributor data from distributorRef: " + databaseError.getMessage());
            }
        });
    }
}