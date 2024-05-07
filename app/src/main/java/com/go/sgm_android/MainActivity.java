package com.go.sgm_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.Toast;
import com.go.sgm_android.ui.add.AddFragment;
import com.go.sgm_android.ui.home.HomeFragment;
import com.go.sgm_android.ui.slideshow.SlideshowFragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.go.sgm_android.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String UPLOAD_WORK_TAG = "upload_work";
    private DatabaseReference powerPlantRef;
    private String currentDate;

    // Declare a global variable to store the previous value of ppcurrentCapacity for each power plant
    private Map<String, Float> previousCapacities = new HashMap<>();
    private Map<String, Float> previousDemands = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Set the title of the activity
        setTitle("Real Time Grid Information");

        // Schedule the data upload task using WorkManager
        scheduleDailyDataUploadTask();

        // Continuous Data Change
        continuousDataUpdateIntoFirebase();
        continuousDataUpdateIntoFirebase2();

        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.item_1) {
                    selectedFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.item_2) {
                    selectedFragment = new AddFragment();
                } else if (item.getItemId() == R.id.item_3) {
                    selectedFragment = new SlideshowFragment();
                }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return true;
        });

        // Set default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();


    }

    private void continuousDataUpdateIntoFirebase() {
        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        DatabaseReference powerPlantRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("PowerPlant");
        DatabaseReference distributorRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("Distributor");
        powerPlantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String powerPlantKey = snapshot.getKey();
                    if (snapshot.child("Date").child(currentDate).child("capacity").child("ppcurrentCapacity").exists()) {
                        float currentCapacity = snapshot.child("Date").child(currentDate).child("capacity").child("ppcurrentCapacity").getValue(Float.class);

                        // Get the previous capacity for this power plant
                        float previousCapacity = previousCapacities.getOrDefault(powerPlantKey, -1f);

                        // Check if current capacity is different from previous capacity
                        if (currentCapacity != previousCapacity) {
                            // Capacity has changed, show toast
//                            showToast("Power Plant " + powerPlantKey + " - Capacity changed to: " + currentCapacity);

                            // Convert to BigDecimal for precise arithmetic
                            BigDecimal currentCapacityBigDecimal = new BigDecimal(Float.toString(currentCapacity));
                            BigDecimal previousCapacityBigDecimal = new BigDecimal(Float.toString(previousCapacity));

                            // Calculate total capacity
                            BigDecimal totalCapacityBigDecimal = currentCapacityBigDecimal.add(previousCapacityBigDecimal);
                            // Round the total capacity to two decimal places
                            totalCapacityBigDecimal = totalCapacityBigDecimal.setScale(2, RoundingMode.HALF_UP);
                            float totalCapacity = totalCapacityBigDecimal.floatValue();

                            // Get reference to the location to set the value
                            DatabaseReference totalCapacityRef = snapshot.child("Date").child(currentDate).child("total").child("pptotalCurrentCapacity").getRef();

                            // Update total capacity using transaction
                            totalCapacityRef.runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                    if (mutableData.getValue() == null) {
                                        mutableData.setValue(totalCapacity);
                                    } else {
                                        float currentValue = mutableData.getValue(Float.class);
                                        mutableData.setValue(currentValue + currentCapacity);
                                    }
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                    if (databaseError != null) {
                                        Log.e("PowerPlantListActivity", "Transaction failed: " + databaseError.getMessage());
                                    }
                                }
                            });

                            // Update previousCapacity to currentCapacity for this power plant
                            previousCapacities.put(powerPlantKey, currentCapacity);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PowerPlantListActivity", "Failed to fetch power plant data from powerPlantRef: " + databaseError.getMessage());
            }
        });

        distributorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Distributor keys mapping
                    Map<String, String> distributorKeysMap = new HashMap<>();
                    distributorKeysMap.put("BPDB", "Bangladesh Power Development Board");
//                    distributorKeysMap.put("BREB", "Bangladesh Rural Electrification Board");
                    distributorKeysMap.put("DESCO", "Dhaka Electric Supply Company Limited");
//                    distributorKeysMap.put("DPDC", "Dhaka Power Distribution Company Limited");
//                    distributorKeysMap.put("WZPDCL", "West Zone Power Distribution Company");
//                    distributorKeysMap.put("NESCO", "Northern Electricity Supply Company PLC");

                    // Extract distributor key and name
                    String distributorKey = snapshot.getKey();
                    String distributorName = distributorKeysMap.get(distributorKey);

                    if (snapshot.child("Date").child(currentDate).child("demand").child("ddcurrentDemand").exists()) {
                        float currentDemand = snapshot.child("Date").child(currentDate).child("demand").child("ddcurrentDemand").getValue(Float.class);

                        // Get the previous demand for this distributor
                        float previousDemand = previousDemands.getOrDefault(distributorKey, -1f);

                        // Check if current demand is different from previous demand
                        if (currentDemand != previousDemand) {
                            // Demand has changed, show toast
//                            showToast("Distributor " + distributorName + " - Demand changed to: " + currentDemand);

                            // Convert to BigDecimal for precise arithmetic
                            BigDecimal currentDemandBigDecimal = new BigDecimal(Float.toString(currentDemand));
                            BigDecimal previousDemandBigDecimal = new BigDecimal(Float.toString(previousDemand));

                            // Calculate total demand
                            BigDecimal totalDemandBigDecimal = currentDemandBigDecimal.add(previousDemandBigDecimal);
                            // Round the total demand to two decimal places
                            totalDemandBigDecimal = totalDemandBigDecimal.setScale(2, RoundingMode.HALF_UP);
                            float totalDemand = totalDemandBigDecimal.floatValue();

                            // Get reference to the location to set the value
                            DatabaseReference totalDemandRef = snapshot.child("Date").child(currentDate).child("total").child("ddtotalCurrentdemand").getRef();

                            // Update total demand using transaction
                            totalDemandRef.runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                    if (mutableData.getValue() == null) {
                                        mutableData.setValue(totalDemand);
                                    } else {
                                        float currentValue = mutableData.getValue(Float.class);
                                        mutableData.setValue(currentValue + currentDemand);
                                    }
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                    if (databaseError != null) {
                                        Log.e("PowerPlantListActivity", "Transaction failed: " + databaseError.getMessage());
                                    }
                                }
                            });

                            // Update previousDemand to currentDemand for this distributor
                            previousDemands.put(distributorKey, currentDemand);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PowerPlantListActivity", "Failed to fetch distributor data from distributorRef: " + databaseError.getMessage());
            }
        });
    }

    private void continuousDataUpdateIntoFirebase2() {
        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        DatabaseReference totalpowerPlantRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("PowerPlant");
        DatabaseReference totaldistributorRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("Distributor");

        totalpowerPlantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Initialize total sum variable
                double totalCapacitySum = 0;

                // Iterate through each power plant
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the total current capacity for this power plant
                    Float totalCurrentCapacity = snapshot.child("Date").child(currentDate).child("total").child("pptotalCurrentCapacity").getValue(Float.class);

                    // If the total current capacity is not null, add it to the total sum
                    if (totalCurrentCapacity != null) {
                        totalCapacitySum += totalCurrentCapacity;
                    }
                }

                    // Show toast message with the total sum
                    showToast("Total Current Capacity: " + totalCapacitySum);

                    // Get reference to the location to set the value
                    DatabaseReference totalCapacityRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("Date").child(currentDate).child("total").child("AllppcurrentCapacity").getRef();

                    // Update total capacity using transaction
                    final double finalTotalCapacitySum = totalCapacitySum; // Declare as final
                    totalCapacityRef.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            // Get the current value
                            Double currentValue = mutableData.getValue(Double.class);

                            // If the current value is null, set it to 0
                            if (currentValue == null) {
                                mutableData.setValue(finalTotalCapacitySum);
                            } else {
                                // Add the total sum to the current value
                                mutableData.setValue(finalTotalCapacitySum);
                            }
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                            if (databaseError != null) {
                                Log.e("PowerPlantListActivity", "Transaction failed: " + databaseError.getMessage());
                            } else if (committed) {
                                Log.d("PowerPlantListActivity", "Transaction successful.");
                            } else {
                                Log.d("PowerPlantListActivity", "Transaction aborted.");
                            }
                        }
                    });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PowerPlantListActivity", "Failed to fetch power plant data from powerPlantRef: " + databaseError.getMessage());
            }
        });

        totaldistributorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Initialize total sum variable
                double totalDemandSum = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Distributor keys mapping
                    Map<String, String> distributorKeysMap = new HashMap<>();
                    distributorKeysMap.put("BPDB", "Bangladesh Power Development Board");
//                    distributorKeysMap.put("BREB", "Bangladesh Rural Electrification Board");
                    distributorKeysMap.put("DESCO", "Dhaka Electric Supply Company Limited");
//                    distributorKeysMap.put("DPDC", "Dhaka Power Distribution Company Limited");
//                    distributorKeysMap.put("WZPDCL", "West Zone Power Distribution Company");
//                    distributorKeysMap.put("NESCO", "Northern Electricity Supply Company PLC");

                    // Extract distributor key and name
                    String distributorKey = snapshot.getKey();
                    String distributorName = distributorKeysMap.get(distributorKey);

                    if (snapshot.child("Date").child(currentDate).child("total").child("ddtotalCurrentdemand").exists()) {
                        float currentDemand = snapshot.child("Date").child(currentDate).child("total").child("ddtotalCurrentdemand").getValue(Float.class);

                        // If the total current capacity is not null, add it to the total sum
                        totalDemandSum += currentDemand;
                    }
                }

                // Show toast message with the total sum
                showToast("Total Current Demand: " + totalDemandSum);

                // Get reference to the location to set the value
                DatabaseReference totalDemandRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("Date").child(currentDate).child("total").child("AllddcurrentDemand").getRef();

                // Update total capacity using transaction
                final double finalTotalDemandSum = totalDemandSum; // Declare as final
                totalDemandRef.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        // Get the current value
                        Double currentValue = mutableData.getValue(Double.class);

                        // If the current value is null, set it to 0
                        if (currentValue == null) {
                            mutableData.setValue(finalTotalDemandSum);
                        } else {
                            // Add the total sum to the current value
                            mutableData.setValue(finalTotalDemandSum);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                        if (databaseError != null) {
                            Log.e("PowerPlantListActivity", "Transaction failed: " + databaseError.getMessage());
                        } else if (committed) {
                            Log.d("PowerPlantListActivity", "Transaction successful.");
                        } else {
                            Log.d("PowerPlantListActivity", "Transaction aborted.");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PowerPlantListActivity", "Failed to fetch distributor data from distributorRef: " + databaseError.getMessage());
            }
        });
    }




    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.search_menu) {
            // Handle click on Messenger menu item
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.profile_menu) {
            // Handle click on Messenger menu item
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void scheduleDailyDataUploadTask() {
        // Create input data if needed
        Data inputData = new Data.Builder().build();

        // Create a PeriodicWorkRequest for the daily upload task
        // Set the interval to 1 day and specify the time for daily upload
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0); // 10 AM
        calendar.set(Calendar.MINUTE, 11);
        long initialDelay = calendar.getTimeInMillis() - System.currentTimeMillis();
        if (initialDelay < 0) {
            initialDelay += TimeUnit.DAYS.toMillis(1); // If the time has already passed today, set it for tomorrow
        }

        PeriodicWorkRequest uploadWorkRequest = new PeriodicWorkRequest.Builder(
                UploadWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag(UPLOAD_WORK_TAG)
                .build();

        // Enqueue the periodic work request with WorkManager
        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(UPLOAD_WORK_TAG, ExistingPeriodicWorkPolicy.REPLACE, uploadWorkRequest);
    }
}