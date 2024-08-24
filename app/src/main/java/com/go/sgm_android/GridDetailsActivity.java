package com.go.sgm_android;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.go.sgm_android.Adapter.DistributorAdapter;
import com.go.sgm_android.Adapter.GridAdapter;
import com.go.sgm_android.databinding.ActivityGridDetailsBinding;
import com.go.sgm_android.model.Distributor;
import com.go.sgm_android.model.Grid;
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

public class GridDetailsActivity extends AppCompatActivity {

    private ActivityGridDetailsBinding binding;
    private Handler handler;
    private Runnable updateTimeRunnable;
    private GridAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGridDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Change notification color
        getWindow().setStatusBarColor(getResources().getColor(R.color.glitter_lake));

        // Set the title of the activity
        setTitle("Grid Details");

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
        // This is for Grid Recycler View
        // Initialize RecyclerView
        RecyclerView recyclerView = binding.GridList;
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // Initialize adapters
        gridAdapter = new GridAdapter(new ArrayList<>());
        // Set up adapter
        recyclerView.setAdapter(gridAdapter);

        fetchDataFromFirebase();
    }

    private void fetchDataFromFirebase() {
        DatabaseReference gridRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("Grid");
        gridRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Grid> grids = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (snapshot.exists() && snapshot.hasChild("name") && snapshot.hasChild("nid") && snapshot.hasChild("phone") && snapshot.hasChild("gridtotalCurrentSupply")) {
                        String name = snapshot.child("name").getValue(String.class);
                        String nid = snapshot.child("nid").getValue(String.class);
                        String phone = snapshot.child("phone").getValue(String.class);
                        float gridtotalCurrentSupply = snapshot.child("gridtotalCurrentSupply").getValue(float.class);

                        if (name != null && nid != null && phone != null) {
                            Grid grid = new Grid(name, nid, phone, gridtotalCurrentSupply);
                            grids.add(grid);
                        }
                    }
                }
                // Update the adapter with the new list of distributors
                gridAdapter.setGrids(grids);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", "Failed to fetch distributor data from distributorRef: " + databaseError.getMessage());
            }
        });
    }
}