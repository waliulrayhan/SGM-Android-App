package com.go.sgm_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.go.sgm_android.ui.add.AddFragment;
import com.go.sgm_android.ui.history.HistoryFragment;
import com.go.sgm_android.ui.home.HomeFragment;
import com.go.sgm_android.ui.slideshow.SlideshowFragment;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import com.go.sgm_android.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Boolean isAllFabVisible = false;
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


//        binding.fabAddPowerPlant.setVisibility(View.GONE);
//        binding.fabAddDistributor.setVisibility(View.GONE);
//        binding.AddPowerPlantText.setVisibility(View.GONE);
//        binding.AddDistributorText.setVisibility(View.GONE);
//
//        // Set click listener for the main FAB
//        binding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggleFABs();
//            }
//        });
//
//        binding.fabAddPowerPlant.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Toast.makeText(MainActivity.this, "Hello, Add Power Plant", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this, AddPowerPlantActivity.class);
//                // Start SecondActivity
//                startActivity(intent);
//            }
//        });
//
//        binding.AddPowerPlantText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Toast.makeText(MainActivity.this, "Hello, Add Power Plant", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this, AddPowerPlantActivity.class);
//                // Start SecondActivity
//                startActivity(intent);
//            }
//        });
//
//        binding.fabAddDistributor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Toast.makeText(MainActivity.this, "Hello, Add Distributor", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this, AddDistributorActivity.class);
//                // Start SecondActivity
//                startActivity(intent);
//            }
//        });
//
//        binding.AddDistributorText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Toast.makeText(MainActivity.this, "Hello, Add Distributor", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this, AddDistributorActivity.class);
//                // Start SecondActivity
//                startActivity(intent);
//            }
//        });
//
//        // Set touch listener for the blur overlay
//        binding.blurOverlay.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                hideFABs();
//                return true;
//            }
//        });
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
            Intent intent = new Intent(MainActivity.this, TestActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    // Method to toggle visibility of additional FABs
//    private void toggleFABs() {
//        if (!isAllFabVisible) {
//            showFABs();
//        } else {
//            hideFABs();
//        }
//    }

//    // Method to show additional FABs and blur overlay with animation
//    private void showFABs() {
//        binding.fabAddDistributor.setVisibility(View.VISIBLE);
//        binding.fabAddPowerPlant.setVisibility(View.VISIBLE);
//        binding.AddDistributorText.setVisibility(View.VISIBLE);
//        binding.AddPowerPlantText.setVisibility(View.VISIBLE);
//        binding.blurOverlay.setVisibility(View.VISIBLE);
//
//        // Animate translationY
//        float translationY = getResources().getDimension(R.dimen.fab_translation_y);
//        binding.fabAddDistributor.animate().translationY(-translationY).setInterpolator(new AccelerateDecelerateInterpolator()).start();
//        binding.fabAddPowerPlant.animate().translationY(-translationY).setInterpolator(new AccelerateDecelerateInterpolator()).start();
//        binding.AddDistributorText.animate().translationY(-translationY).setInterpolator(new AccelerateDecelerateInterpolator()).start();
//        binding.AddPowerPlantText.animate().translationY(-translationY).setInterpolator(new AccelerateDecelerateInterpolator()).start();
//
//        isAllFabVisible = true;
//    }
//
//    // Method to hide additional FABs and blur overlay with animation
//    private void hideFABs() {
//        binding.fabAddDistributor.setVisibility(View.GONE);
//        binding.fabAddPowerPlant.setVisibility(View.GONE);
//        binding.AddDistributorText.setVisibility(View.GONE);
//        binding.AddPowerPlantText.setVisibility(View.GONE);
//        binding.blurOverlay.setVisibility(View.GONE);
//
//        // Animate translationY
//        binding.fabAddDistributor.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).start();
//        binding.fabAddPowerPlant.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).start();
//        binding.AddDistributorText.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).start();
//        binding.AddPowerPlantText.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).start();
//
//        isAllFabVisible = false;
//    }
}