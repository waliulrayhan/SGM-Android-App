package com.go.sgm_android;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.go.sgm_android.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private Boolean isAllFabVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        // Set the title of the activity
        setTitle("Real Time Grid Information");

        binding.appBarMain.fabAddPowerPlant.setVisibility(View.GONE);
        binding.appBarMain.fabAddDistributor.setVisibility(View.GONE);
        binding.appBarMain.AddPowerPlantText.setVisibility(View.GONE);
        binding.appBarMain.AddDistributorText.setVisibility(View.GONE);

        // Set click listener for the main FAB
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFABs();
            }
        });

        binding.appBarMain.fabAddPowerPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Hello, Add Power Plant", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, AddPowerPlantActivity.class);
                // Start SecondActivity
                startActivity(intent);
            }
        });

        binding.appBarMain.AddPowerPlantText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Hello, Add Power Plant", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, AddPowerPlantActivity.class);
                // Start SecondActivity
                startActivity(intent);
            }
        });

        binding.appBarMain.fabAddDistributor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Hello, Add Distributor", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, AddDistributorActivity.class);
                // Start SecondActivity
                startActivity(intent);
            }
        });

        binding.appBarMain.AddDistributorText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Hello, Add Distributor", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, AddDistributorActivity.class);
                // Start SecondActivity
                startActivity(intent);
            }
        });

        // Set touch listener for the blur overlay
        binding.appBarMain.blurOverlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideFABs();
                return true;
            }
        });


//        DrawerLayout drawer = binding.drawerLayout;
//        NavigationView navigationView = binding.navView;
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
//                .setOpenableLayout(drawer)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
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




//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

    // Method to toggle visibility of additional FABs
    private void toggleFABs() {
        if (!isAllFabVisible) {
            showFABs();
        } else {
            hideFABs();
        }
    }

    // Method to show additional FABs and blur overlay with animation
    private void showFABs() {
        binding.appBarMain.fabAddDistributor.setVisibility(View.VISIBLE);
        binding.appBarMain.fabAddPowerPlant.setVisibility(View.VISIBLE);
        binding.appBarMain.AddDistributorText.setVisibility(View.VISIBLE);
        binding.appBarMain.AddPowerPlantText.setVisibility(View.VISIBLE);
        binding.appBarMain.blurOverlay.setVisibility(View.VISIBLE);

        // Animate translationY
        float translationY = getResources().getDimension(R.dimen.fab_translation_y);
        binding.appBarMain.fabAddDistributor.animate().translationY(-translationY).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        binding.appBarMain.fabAddPowerPlant.animate().translationY(-translationY).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        binding.appBarMain.AddDistributorText.animate().translationY(-translationY).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        binding.appBarMain.AddPowerPlantText.animate().translationY(-translationY).setInterpolator(new AccelerateDecelerateInterpolator()).start();

        isAllFabVisible = true;
    }

    // Method to hide additional FABs and blur overlay with animation
    private void hideFABs() {
        binding.appBarMain.fabAddDistributor.setVisibility(View.GONE);
        binding.appBarMain.fabAddPowerPlant.setVisibility(View.GONE);
        binding.appBarMain.AddDistributorText.setVisibility(View.GONE);
        binding.appBarMain.AddPowerPlantText.setVisibility(View.GONE);
        binding.appBarMain.blurOverlay.setVisibility(View.GONE);

        // Animate translationY
        binding.appBarMain.fabAddDistributor.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        binding.appBarMain.fabAddPowerPlant.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        binding.appBarMain.AddDistributorText.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        binding.appBarMain.AddPowerPlantText.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).start();

        isAllFabVisible = false;
    }
}