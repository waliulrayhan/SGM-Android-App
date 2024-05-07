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
    private Map<String, Integer> previousCapacities = new HashMap<>();
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

        powerPlantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String powerPlantKey = snapshot.getKey();
                    if (snapshot.child("Date").child(currentDate).child("capacity").child("ppcurrentCapacity").exists()) {
                        int currentCapacity = snapshot.child("Date").child(currentDate).child("capacity").child("ppcurrentCapacity").getValue(Integer.class);

                        // Get the previous capacity for this power plant
                        int previousCapacity = previousCapacities.getOrDefault(powerPlantKey, -1);

                        // Check if current capacity is different from previous capacity
                        if (currentCapacity != previousCapacity) {
                            // Capacity has changed, show toast
                            showToast("Power Plant " + powerPlantKey + " - Capacity changed to: " + currentCapacity);

                            // Calculate total capacity
                            int totalCapacity = currentCapacity + previousCapacity;

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
                                        int currentValue = mutableData.getValue(Integer.class);
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
        calendar.set(Calendar.MINUTE, 2);
        calendar.set(Calendar.SECOND, 0);
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