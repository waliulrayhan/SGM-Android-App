package com.go.sgm_android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.go.sgm_android.databinding.ActivityAddDistributorBinding;
import com.go.sgm_android.databinding.ActivityAddPowerPlantBinding;
import com.go.sgm_android.model.Distributor;
import com.go.sgm_android.model.PowerPlant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddDistributorActivity extends AppCompatActivity {

    private ActivityAddDistributorBinding binding;
    private DatabaseReference mDatabase;
    private AutoCompleteTextView distributorAutoCompleteTextView, zoneAutoCompleteTextView, circleAutoCompleteTextView;
    private ProgressDialog progressDialog;
    private ArrayAdapter<String> distributorAdapter, zoneAdapter, circleAdapter;


    // Define your distributor, zone, and circle arrays here
    private String[] distributor = {"Bangladesh Power Development Board", "Bangladesh Rural Electrification Board", "Dhaka Electric Supply Company Limited", "Dhaka Power Distribution Company Limited", "Northern Electricity Supply Company PLC", "West Zone Power Distribution Company Limited"};
    private String[][] zone = {
            {"Chattogram (South Zone)", "Mymensingh (Central Zone)", "Cumilla Zone", "Sylhet Zone"},
            {"No Circle"},
            {"North Zone", "Central Zone", "South Zone"},
            {"South Zone", "North Zone", "Central Zone"},
            {"No Circle"},
            {"Rajshahi Zone", "Rangpur Zone"}
    };
    private String[][][] circle = {
            {
                    {"Conservation and Management Circle, Chatto Metro (East)", "Conservation and Management Circle, Chatto Metro (West)", "Conservation and Management Circle, Chatto Metro (North)", "Conservation and Management Circle, Chatto Metro (South)", "Conservation and Management Circle, Rangamati"},
                    {"Conservation and Management Circle-1, Mymensingh", "Conservation and Management Circle-2, Mymensingh", "Conservation and Management Circle, Tangail", "Conservation and Management Circle, Jamalpur"},
                    {"Conservation and Management Circle, Cumilla", "Conservation and Management Circle, Noakhali", "Gopalpur"},
                    {"Conservation and Management Circle, Sylhet", "Conservation and Management Circle, Moulvibazar"}
            },
            {
                    {"Tongi", "Uttara", "Dakshinkhan"},
                    {"Baridhara", "Gulshan"},
                    {"Pallabi", "Agargaon", "Rupnagar"}
            },
            {
                    {"Tongi", "Uttara", "Dakshinkhan"},
                    {"Baridhara", "Gulshan"},
                    {"Pallabi", "Agargaon", "Rupnagar"}
            },
            {
                    {"Tongi", "Uttara", "Dakshinkhan"},
                    {"Baridhara", "Gulshan"},
                    {"Pallabi", "Agargaon", "Rupnagar"}
            },
            {
                    {"Tongi", "Uttara", "Dakshinkhan"},
                    {"Baridhara", "Gulshan"},
                    {"Pallabi", "Agargaon", "Rupnagar"}
            },
            {
                    {"Tongi", "Uttara", "Dakshinkhan"},
                    {"Baridhara", "Gulshan"},
                    {"Pallabi", "Agargaon", "Rupnagar"}
            }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDistributorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Change notification color
        getWindow().setStatusBarColor(getResources().getColor(R.color.glitter_lake));

        // Set the title of the activity
        setTitle("Add Distributor");

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding Power Plant...");
        progressDialog.setCancelable(false);

        // Initialize AutoCompleteTextViews
        distributorAutoCompleteTextView = findViewById(R.id.autoCompleteTextViewDistributor);
        zoneAutoCompleteTextView = findViewById(R.id.autoCompleteTextViewZone);
        circleAutoCompleteTextView = findViewById(R.id.autoCompleteTextViewCircle);

        // Initialize Adapters
        distributorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, distributor);
        distributorAutoCompleteTextView.setAdapter(distributorAdapter);

        zoneAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        zoneAutoCompleteTextView.setAdapter(zoneAdapter);

        circleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        circleAutoCompleteTextView.setAdapter(circleAdapter);

        // Set listeners for AutoCompleteTextViews
        distributorAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                updateZoneAutoCompleteTextView(position);
                // Clear the text of the district AutoCompleteTextView when division changes
                zoneAutoCompleteTextView.setText("");
                // Clear the text of the upazilla AutoCompleteTextView when division changes
                circleAutoCompleteTextView.setText("");
                // Clear the upazilla adapter when the division changes
                circleAdapter.clear();
                circleAdapter.notifyDataSetChanged();
            }
        });

        zoneAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int distributorPosition = distributorAdapter.getPosition(distributorAutoCompleteTextView.getText().toString());
                updateCircleAutoCompleteTextView(distributorPosition, position);
                // Clear the text of the upazilla AutoCompleteTextView when district changes
                circleAutoCompleteTextView.setText("");
            }
        });

        // Handle button click
        binding.addDistributorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String distributor = distributorAutoCompleteTextView.getText().toString().trim();
                String zone = zoneAutoCompleteTextView.getText().toString().trim();
                String circle = circleAutoCompleteTextView.getText().toString().trim();
                String name = binding.addDistributorEditText.getText().toString().trim();

                if (!distributor.isEmpty() && !zone.isEmpty() && !circle.isEmpty() && !name.isEmpty()) {
                    Toast.makeText(AddDistributorActivity.this, "Hello "+distributor+zone+circle+name, Toast.LENGTH_LONG).show();
                    progressDialog.show();
                    addDistributorToDatabase(distributor, zone, circle, name);
//                    uploadDDDataToFirebase();
                } else {
                    Toast.makeText(AddDistributorActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addDistributorToDatabase(String distributor, String zone, String circle, String name) {
        // Reference to "SGM/PowerPlant"
        DatabaseReference distributorRef = mDatabase.child("SGM").child("Distributor").child(distributor).push();

        // Create a map to store the distributor data
        Distributor distributorData = new Distributor(distributor, zone, circle, name,0,0);

        // Run a transaction to push the distributor data to the database
        distributorRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                // Set the distributor data
                mutableData.setValue(distributorData);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError == null) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(AddDistributorActivity.this, MainActivity.class);
                    // Start MainActivity
                    startActivity(intent);
                    Toast.makeText(AddDistributorActivity.this, "Distributor added successfully", Toast.LENGTH_SHORT).show();
                    // Clear input fields after successful addition
                    binding.addDistributorEditText.setText("");
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AddDistributorActivity.this, "Failed to add distributor: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    public static void uploadDDDataToFirebase() {
//        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("SGM");
//
//        // Get the current date
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
//        String currentDate = dateFormat.format(new Date());
//
//        // Upload Distributor Data
//        DatabaseReference distributorRef = databaseRef.child("Distributor");
//        distributorRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot distributorSnapshot : dataSnapshot.getChildren()) {
//                    DatabaseReference distributorDateRef = distributorSnapshot.child("Date").child(currentDate).getRef();
//                    distributorDateRef.child("demand").child("ddcurrentDemand").setValue(0);
//                    distributorDateRef.child("demand").child("ddtargetdemand").setValue(0);
//                    distributorDateRef.child("total").child("ddtotalCurrentdemand").setValue(0);
//                    distributorDateRef.child("alert").setValue("false");
//                    distributorDateRef.child("history").child("ddtotalCurrentDemand").setValue(0);
//                    distributorDateRef.child("history").child("last_update_time").setValue("11.59.59 PM");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e("UploadDataToFirebase", "Failed to upload distributor data: " + databaseError.getMessage());
//            }
//        });
//    }


    private void updateZoneAutoCompleteTextView(int position) {
        // Update Zones AutoCompleteTextView based on selected distributor
        String[] selectedZones = zone[position];
        zoneAdapter.clear();
        zoneAdapter.addAll(selectedZones);
        zoneAdapter.notifyDataSetChanged();
    }

    private void updateCircleAutoCompleteTextView(int distributorPosition, int zonePosition) {
        // Update circles AutoCompleteTextView based on selected zone and distributor
        String[] selectedCircles = circle[distributorPosition][zonePosition];
        circleAdapter.clear();
        circleAdapter.addAll(selectedCircles);
        circleAdapter.notifyDataSetChanged();
    }
}