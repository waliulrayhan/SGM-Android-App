package com.go.sgm_android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddDistributorActivity extends AppCompatActivity {

    private ActivityAddDistributorBinding binding;
    private DatabaseReference mDatabase;
    private AutoCompleteTextView distributorAutoCompleteTextView, zoneAutoCompleteTextView, circleAutoCompleteTextView;
    private ProgressDialog progressDialog;
    private ArrayAdapter<String> distributorAdapter, zoneAdapter, circleAdapter;


    // Define your distributor, zone, and circle arrays here
    private String[] distributor = {"BPDB - Bangladesh Power Development Board", "DESCO - Dhaka Electric Supply Company Limited"};
    private String[][] zone = {
            {"Chattogram (South Zone)", "Mymensingh (Central Zone)", "Cumilla Zone", "Sylhet Zone"},
            {"North Zone", "Central Zone", "South Zone"}
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
                } else {
                    Toast.makeText(AddDistributorActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addDistributorToDatabase(String distributor, String zone, String circle, String name) {
        // Reference to "SGM/PowerPlant"
        DatabaseReference distributorRef = mDatabase.child("SGM").child("Distributor").child(distributor).push();

        // Create a map to store the power plant data
        Distributor distributor1 = new Distributor(distributor, zone, circle, name);

        // Push the power plant data to the database
        distributorRef.setValue(distributor1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(AddDistributorActivity.this, MainActivity.class);
                        // Start SecondActivity
                        startActivity(intent);
                        Toast.makeText(AddDistributorActivity.this, "Distributor added successfully", Toast.LENGTH_SHORT).show();
                        // Clear input fields after successful addition
                        binding.addDistributorEditText.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddDistributorActivity.this, "Failed to add distributor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

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