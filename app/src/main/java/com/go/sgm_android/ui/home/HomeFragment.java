package com.go.sgm_android.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.go.sgm_android.Adapter.DistributorAdapter;
import com.go.sgm_android.Adapter.PowerPlantAdapter;
import com.go.sgm_android.databinding.FragmentHomeBinding;
import com.go.sgm_android.model.Distributor;
import com.go.sgm_android.model.PowerPlant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Handler handler;
    private Runnable updateTimeRunnable;
    private PowerPlantAdapter powerPlantAdapter;
    private DistributorAdapter distributorAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

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
        RecyclerView recyclerView1 = binding.powerPlantRecyclerView;
        recyclerView1.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize adapters
        powerPlantAdapter = new PowerPlantAdapter(new ArrayList<>());
        // Set up adapter
        recyclerView1.setAdapter(powerPlantAdapter);
        // Fetch power plant data from Firebase
        fetchPowerPlantData();


        //==========================================================================================
        // This is for Distributor Recycler View
        // Initialize RecyclerView
        RecyclerView recyclerView2 = binding.distributorsRecyclerView;
        recyclerView2.setLayoutManager(new LinearLayoutManager(requireContext()));


        // Initialize adapters
        distributorAdapter = new DistributorAdapter(new ArrayList<>());
        // Set up adapter
        recyclerView2.setAdapter(distributorAdapter);
        // Fetch Distributor data from Firebase
        fetchDistributorData();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchPowerPlantData() {
        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Construct the Firebase reference path for the current date
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("SGM").child(currentDate).child("power_plants");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<PowerPlant> powerPlants = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.getKey();
                    long currentCapacity = snapshot.child("current_capacity").getValue(Long.class);
                    long targetCapacity = snapshot.child("target_capacity").getValue(Long.class);
                    powerPlants.add(new PowerPlant(name, currentCapacity, targetCapacity));
                }
                powerPlantAdapter.setPowerPlants(powerPlants);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

    private void fetchDistributorData() {
        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Construct the Firebase reference path for the current date
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("SGM").child(currentDate).child("distributors");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Distributor> distributors = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.getKey();
                    long currentDemand = snapshot.child("current_demand").getValue(Long.class);
                    long targetDemand = snapshot.child("target_demand").getValue(Long.class);
                    distributors.add(new Distributor(name, currentDemand, targetDemand));
                }
                distributorAdapter.setDistributors(distributors);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }
}