package com.go.sgm_android;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.go.sgm_android.databinding.ActivityDistributorListBinding;
import com.go.sgm_android.databinding.ActivityPowerPlantListBinding;
import com.go.sgm_android.model.PowerPlant;
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

public class DistributorListActivity extends AppCompatActivity {

    private ActivityDistributorListBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDistributorListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Change notification color
        getWindow().setStatusBarColor(getResources().getColor(R.color.glitter_lake));

        // Set the title of the activity
        setTitle("List of Running Distributors");

        fetchDataFromDatabase();
    }

    private void fetchDataFromDatabase() {
        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        DatabaseReference BPDBRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("Distributor").child("BPDB - Bangladesh Power Development Board").child("Date").child(currentDate);
        DatabaseReference BREBRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("Distributor").child("BREB - Bangladesh Rural Electrification Board").child("Date").child(currentDate);
        DatabaseReference DESCORef = FirebaseDatabase.getInstance().getReference().child("SGM").child("Distributor").child("DESCO - Dhaka Electric Supply Company Limited").child("Date").child(currentDate);
        DatabaseReference DPDCRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("Distributor").child("DPDC - Dhaka Power Distribution Company Limited").child("Date").child(currentDate);
        DatabaseReference WZPDCLRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("Distributor").child("WZPDCL - West Zone Power Distribution Company").child("Date").child(currentDate);
        DatabaseReference NESCORef = FirebaseDatabase.getInstance().getReference().child("SGM").child("Distributor").child("NESCO - Northern Electricity Supply Company PLC").child("Date").child(currentDate);

        BPDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve total node
                    DataSnapshot demandSnapshot = dataSnapshot.child("demand");

                    String totalCurrentdemandValue = demandSnapshot.child("ddcurrentDemand").getValue(String.class);
                    String totaltargetdemandValue = demandSnapshot.child("ddtargetdemand").getValue(String.class);

                    // Update UI with fetched values
                    binding.BPDBTotalCurrentDemand.setText("Current Demand: "+String.valueOf(totalCurrentdemandValue)+" MW");
                    binding.BPDBTotalTargetDemand.setText("Target Demand: "+String.valueOf(totaltargetdemandValue)+" MW");
                } else {
                    // Handle case when data doesn't exist
                    // You can display a message or take appropriate action
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PowerPlantListActivity", "Failed to fetch power plant data from powerPlantRef: " + databaseError.getMessage());
            }
        });

        DESCORef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve total node
                    DataSnapshot demandSnapshot = dataSnapshot.child("demand");

                    String totalCurrentdemandValue = demandSnapshot.child("ddcurrentDemand").getValue(String.class);
                    String totaltargetdemandValue = demandSnapshot.child("ddtargetdemand").getValue(String.class);

                    // Update UI with fetched values
                    binding.DESCOTotalCurrentDemand.setText("Current Demand: "+String.valueOf(totalCurrentdemandValue)+" MW");
                    binding.DESCOTotalTargetDemand.setText("Target Demand: "+String.valueOf(totaltargetdemandValue)+" MW");
                } else {
                    // Handle case when data doesn't exist
                    // You can display a message or take appropriate action
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PowerPlantListActivity", "Failed to fetch power plant data from powerPlantRef: " + databaseError.getMessage());
            }
        });
    }
}