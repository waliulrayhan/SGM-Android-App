package com.go.sgm_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.Toast;
import com.go.sgm_android.ui.add.AddFragment;
import com.go.sgm_android.ui.history.HistoryFragment;
import com.go.sgm_android.ui.home.HomeFragment;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.go.sgm_android.databinding.ActivityMainBinding;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String UPLOAD_WORK_TAG = "upload_work";
    private String currentDate;

    // Declare a global variable to store the previous value of ppcurrentCapacity for each power plant
    private Map<String, Float> previousCapacities = new HashMap<>();
    private Map<String, Float> previousDemands = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            setSupportActionBar(binding.toolbar);

            // Set the title of the activity
            setTitle("Real Time Grid Info.");

            // Schedule the data upload task using WorkManager
            scheduleDailyDataUploadTask();

            // Continuous Data Change
//            continuousDataUpdateIntoFirebase();
//            continuousDataUpdateIntoFirebase2();

            // Start the foreground service
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(new Intent(this, ContinuousUpdateService.class));
//        }

            binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
                Fragment selectedFragment = null;
                if (item.getItemId() == R.id.item_1) {
                    selectedFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.item_2) {
                    selectedFragment = new AddFragment();
                } else if (item.getItemId() == R.id.item_3) {
                    selectedFragment = new HistoryFragment();
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }
                return true;
            });

            // Set default fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } catch (Exception e) {
            // Example: Displaying a toast message to the user
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
        calendar.set(Calendar.HOUR_OF_DAY, 3); // 10 AM
        calendar.set(Calendar.MINUTE, 21);
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