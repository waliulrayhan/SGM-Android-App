package com.go.sgm_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.go.sgm_android.model.PowerPlant;
import com.go.sgm_android.ui.add.AddFragment;
import com.go.sgm_android.ui.history.HistoryFragment;
import com.go.sgm_android.ui.home.HomeFragment;
import com.go.sgm_android.ui.slideshow.SlideshowFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import com.go.sgm_android.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Set the title of the activity
        setTitle("Real Time Grid Information");

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
        if (id == R.id.test_menu) {
            // Handle click on Messenger menu item
//            Intent intent = new Intent(MainActivity.this, TestActivity.class);
//            startActivity(intent);

            Toast.makeText(MainActivity.this, "Test: Data Uploading", Toast.LENGTH_LONG).show();
            uploadDataToFirebase1();
            uploadDataToFirebase2();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void uploadDataToFirebase1() {
        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Reference to "SGM/PowerPlant"
        DatabaseReference powerPlantRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("PowerPlant");

        powerPlantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.exists()) {
                        String uniqueKey = snapshot.getKey();
                        DatabaseReference dateRef = snapshot.child("PP-Date").child(currentDate).getRef();

                        // Create data map for dateRef
                        Map<String, Object> dataMap = new HashMap<>();
                        dataMap.put("capacity", new HashMap<String, Object>() {{
                            put("ppcurrentCapacity", "null");
                            put("pptargetCapacity", "null");
                        }});
                        dataMap.put("history", new HashMap<String, Object>() {{
                            put("pptotalCurrentCapacity", "null");
                            put("last_update_time", "11:59:59 PM");
                        }});
                        dataMap.put("alert", "false");

                        // Set data to dateRef
                        dateRef.setValue(dataMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Data uploaded successfully
                                            // Handle success if needed
                                        } else {
                                            // Data upload failed
                                            // Handle failure if needed
                                        }
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event if needed
            }
        });
    }

    private void uploadDataToFirebase2() {
        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Reference to "SGM/PowerPlant/Date/currentDate/total"
        DatabaseReference totalRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("PowerPlant").child("Date").child(currentDate).child("total");

        // Create data map for totalRef
        Map<String, Object> totalMap = new HashMap<>();
        totalMap.put("AllppcurrentCapacity", "null");
        totalMap.put("AllpptargetCapacity", "null");

        // Set data to totalRef
        totalRef.setValue(totalMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Data uploaded successfully
                            // Handle success if needed
                        } else {
                            // Data upload failed
                            // Handle failure if needed
                        }
                    }
                });
    }

}