package com.go.sgm_android;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;

import com.go.sgm_android.ui.add.AddFragment;
import com.go.sgm_android.ui.home.HomeFragment;
import com.go.sgm_android.ui.slideshow.SlideshowFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.go.sgm_android.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String UPLOAD_WORK_TAG = "upload_work";
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

//        Toast.makeText(MainActivity.this, "Test: Data Uploading", Toast.LENGTH_LONG).show();
//        uploadDataToFirebase(getApplicationContext());

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

//    public static void uploadDataToFirebase(Context context) {
//        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("SGM");
//
//        // Get the current date
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
//        String currentDate = dateFormat.format(new Date());
//
//        // Upload PowerPlant data
//        DatabaseReference powerPlantRef = databaseRef.child("PowerPlant");
//
//        powerPlantRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String powerPlantKey = snapshot.getKey();
//                    DatabaseReference powerPlantDateRef = snapshot.child("Date").child(currentDate).getRef();
//                    powerPlantDateRef.child("capacity").child("ppcurrentCapacity").setValue("null");
//                    powerPlantDateRef.child("capacity").child("pptargetCapacity").setValue("null");
//                    powerPlantDateRef.child("total").child("pptotalCurrentCapacity").setValue("null");
//                    powerPlantDateRef.child("alert").setValue("false");
//                    powerPlantDateRef.child("history").child("pptotalCurrentCapacity").setValue("null");
//                    powerPlantDateRef.child("history").child("last_update_time").setValue("11.59.59 PM");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e("UploadDataToFirebase", "Failed to upload power plant data: " + databaseError.getMessage());
//            }
//        });
//
//        // Upload Distributor Data
//        DatabaseReference distributorRef = databaseRef.child("Distributor");
//        distributorRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot distributorSnapshot : dataSnapshot.getChildren()) {
//                    DatabaseReference distributorDateRef = distributorSnapshot.child("Date").child(currentDate).getRef();
//                    distributorDateRef.child("demand").child("ddcurrentDemand").setValue("null");
//                    distributorDateRef.child("demand").child("ddtargetdemand").setValue("null");
//                    distributorDateRef.child("total").child("ddtotalCurrentdemand").setValue("null");
//                    distributorDateRef.child("alert").setValue("false");
//                    distributorDateRef.child("history").child("ddtotalCurrentDemand").setValue("null");
//                    distributorDateRef.child("history").child("last_update_time").setValue("11.59.59 PM");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e("UploadDataToFirebase", "Failed to upload distributor data: " + databaseError.getMessage());
//            }
//        });
//
//        // Upload Date data
//        DatabaseReference dateRef = databaseRef.child("Date").child(currentDate).child("total");
//        dateRef.child("AllppcurrentCapacity").setValue("null");
//        dateRef.child("AllpptargetCapacity").setValue("null");
//        dateRef.child("AllddcurrentDemand").setValue("null");
//        dateRef.child("AllddtargetDemand").setValue("null");
//    }

//    public static void scheduleDataUploadAlarm(Context context) {
//        // Set the alarm to start at approximately 10:00 PM.
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 0); // 12:00 AM
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//
//        // If it's already past 10:00 PM, set it for the next day
//        if (Calendar.getInstance().after(calendar)) {
//            calendar.add(Calendar.DATE, 1);
//        }
//
//        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
//        alarmIntent.setAction("UPLOAD_DATA");
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
//
//        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        if (alarmMgr != null) {
//            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                    AlarmManager.INTERVAL_DAY, pendingIntent);
//        }
//    }

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