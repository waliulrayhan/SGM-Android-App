package com.go.sgm_android.ui.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.go.sgm_android.Adapter.DistributorAdapter;
import com.go.sgm_android.Adapter.PowerPlantAdapter;
import com.go.sgm_android.AddPowerPlantActivity;
import com.go.sgm_android.DistributorListActivity;
import com.go.sgm_android.MainActivity;
import com.go.sgm_android.PowerPlantListActivity;
import com.go.sgm_android.R;
import com.go.sgm_android.databinding.FragmentHomeBinding;
import com.go.sgm_android.model.PowerPlant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

        fetchDataFromFirebase();

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

        binding.PP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PowerPlantListActivity.class);
                // Start SecondActivity
                startActivity(intent);
            }
        });

        binding.DD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DistributorListActivity.class);
                // Start SecondActivity
                startActivity(intent);
            }
        });

        binding.addCentralCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the dialog layout
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_layout, null);

                // Find views in the dialog layout
                EditText editTextInput = dialogView.findViewById(R.id.editTextInput);
                Button buttonSave = dialogView.findViewById(R.id.buttonSave);
                Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);

                // Create the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                // Set click listener for Save button
                buttonSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get input from EditText
                        String inputData = editTextInput.getText().toString().trim();

                        // Handle saving logic here
                        // For example, you can save data to Firebase or perform other actions

                        if (!inputData.isEmpty()){
                            Toast.makeText(getContext(), "Hello "+inputData, Toast.LENGTH_SHORT).show();
                            uploadCentralCommandToFirebase(inputData);
                        }

                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });

                // Set click listener for Cancel button
                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Dismiss the dialog without saving
                        dialog.dismiss();
                    }
                });

                // Show the dialog
                dialog.show();
            }
        });


//        //==========================================================================================
//        // This is for Power Plant Recycler View
//        // Initialize RecyclerView
//        RecyclerView recyclerView1 = binding.powerPlantRecyclerView;
//        recyclerView1.setLayoutManager(new LinearLayoutManager(requireContext()));
//
//        // Initialize adapters
//        powerPlantAdapter = new PowerPlantAdapter(new ArrayList<>());
//        // Set up adapter
//        recyclerView1.setAdapter(powerPlantAdapter);
//        // Fetch power plant data from Firebase
////        fetchPowerPlantData();
//
//
//        //==========================================================================================
//        // This is for Distributor Recycler View
//        // Initialize RecyclerView
//        RecyclerView recyclerView2 = binding.distributorsRecyclerView;
//        recyclerView2.setLayoutManager(new LinearLayoutManager(requireContext()));
//
//
//        // Initialize adapters
//        distributorAdapter = new DistributorAdapter(new ArrayList<>());
//        // Set up adapter
//        recyclerView2.setAdapter(distributorAdapter);
//        // Fetch Distributor data from Firebase
//        fetchDistributorData();

//
//        //Fetch Total Data
//        fetchTotalData();

        return root;
    }

    private void uploadCentralCommandToFirebase(String inputData) {
        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Reference to "SGM/Date/currentDate/comments"
        DatabaseReference commentRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("SGM")
                .child("Date")
                .child(currentDate)
                .child("comments");

        // Push the comment to generate a unique key
        String commentKey = commentRef.push().getKey();

        // Create a map to hold the comment data
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("comment", inputData);

        // Upload the comment to Firebase
        if (commentKey != null) {
            commentRef.child(commentKey).setValue(commentData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Comment uploaded successfully
                            Log.d("UploadCentralCommand", "Comment uploaded successfully");
                            // You can add any additional actions you want to perform on success
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to upload comment
                            Log.e("UploadCentralCommand", "Failed to upload comment: " + e.getMessage());
                            // Handle the error, if needed
                        }
                    });
        }
    }


    private void fetchDataFromFirebase() {
        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Construct the Firebase reference path for the current date
        DatabaseReference ppReference = FirebaseDatabase.getInstance().getReference()
                .child("SGM").child("Date").child(currentDate);
        ppReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve total node
                    DataSnapshot totalsSnapshot = dataSnapshot.child("total");

                    String totalCurrentCapacityValue = totalsSnapshot.child("AllppcurrentCapacity").getValue(String.class);

                    // Update UI with fetched values
                    binding.ppTotalCurrentCapacity.setText("Total Current Capacity\n"+String.valueOf(totalCurrentCapacityValue)+" MW");
                } else {
                    // Handle case when data doesn't exist
                    // You can display a message or take appropriate action
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });

        // Construct the Firebase reference path for the current date
        DatabaseReference ddReference = FirebaseDatabase.getInstance().getReference()
                .child("SGM").child("Date").child(currentDate);
        ppReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve total node
                    DataSnapshot totalsSnapshot = dataSnapshot.child("total");

                    String totalCurrentCapacityValue = totalsSnapshot.child("AllddcurrentDemand").getValue(String.class);

                    // Update UI with fetched values
                    binding.ddTotalCurrentDemand.setText("Total Current Demand\n"+String.valueOf(totalCurrentCapacityValue)+" MW");
                } else {
                    // Handle case when data doesn't exist
                    // You can display a message or take appropriate action
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

//    private void fetchTotalData() {
//        TextView totalCurrentCapacity = binding.totalCurrentCapacity;
//        TextView totalTargetCapacity = binding.totalTargetCapacity;
//        TextView totalCurrentDemand = binding.totalCurrentDemand;
//        TextView totalTargetDemand = binding.totalTargetDemand;
//        TextView currentFrequency = binding.currentFrequency;
//        TextView fixedFrequency = binding.fixedFrequencyBD;
//
//        // Get the current date
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
//        String currentDate = dateFormat.format(new Date());
//
//        // Construct the Firebase reference path for the current date
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
//                .child("SGM").child(currentDate);
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    // Retrieve totals node
//                    DataSnapshot totalsSnapshot = dataSnapshot.child("totals");
//
//                    // Retrieve capacity node
//                    DataSnapshot capacitySnapshot = totalsSnapshot.child("capacity");
//                    int totalCurrentCapacityValue = capacitySnapshot.child("total_current_capacity").getValue(Integer.class);
//                    int totalTargetCapacityValue = capacitySnapshot.child("total_target_capacity").getValue(Integer.class);
//
//                    // Retrieve demands node
//                    DataSnapshot demandsSnapshot = totalsSnapshot.child("demands");
//                    int totalCurrentDemandValue = demandsSnapshot.child("total_current_demand").getValue(Integer.class);
//                    int totalTargetDemandValue = demandsSnapshot.child("total_target_demand").getValue(Integer.class);
//
//                    // Retrieve frequency node
//                    DataSnapshot frequencySnapshot = totalsSnapshot.child("frequency");
//                    int currentFrequencyValue = frequencySnapshot.child("current_frequency").getValue(Integer.class);
//                    int fixedFrequencyBDValue = frequencySnapshot.child("fixed_frequency_BD").getValue(Integer.class);
//
//                    // Update UI with fetched values
//                    totalCurrentCapacity.setText(String.valueOf(totalCurrentCapacityValue));
//                    totalTargetCapacity.setText(String.valueOf(totalTargetCapacityValue));
//                    totalCurrentDemand.setText(String.valueOf(totalCurrentDemandValue));
//                    totalTargetDemand.setText(String.valueOf(totalTargetDemandValue));
//                    currentFrequency.setText(String.valueOf(currentFrequencyValue));
//                    fixedFrequency.setText(String.valueOf(fixedFrequencyBDValue));
//                } else {
//                    // Handle case when data doesn't exist
//                    // You can display a message or take appropriate action
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle onCancelled
//            }
//        });
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

//    private void fetchPowerPlantData() {
//        // Get the current date
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
//        String currentDate = dateFormat.format(new Date());
//
//        // Construct the Firebase reference path for the current date
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
//                .child("SGM").child(currentDate).child("power_plants");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                List<PowerPlant> powerPlants = new ArrayList<>();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String name = snapshot.getKey();
//                    String currentCapacity = snapshot.child("current_capacity").getValue(String.class);
//                    String targetCapacity = snapshot.child("target_capacity").getValue(String.class);
//                    powerPlants.add(new PowerPlant(name, currentCapacity, targetCapacity));
//                }
//                powerPlantAdapter.setPowerPlants(powerPlants);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle onCancelled
//            }
//        });
//    }

//    private void fetchDistributorData() {
//        // Get the current date
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
//        String currentDate = dateFormat.format(new Date());
//
//        // Construct the Firebase reference path for the current date
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
//                .child("SGM").child(currentDate).child("distributors");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                List<Distributor> distributors = new ArrayList<>();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String name = snapshot.getKey();
//                    long currentDemand = snapshot.child("current_demand").getValue(Long.class);
//                    long targetDemand = snapshot.child("target_demand").getValue(Long.class);
//                    distributors.add(new Distributor(name, currentDemand, targetDemand));
//                }
//                distributorAdapter.setDistributors(distributors);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle onCancelled
//            }
//        });
//    }

}