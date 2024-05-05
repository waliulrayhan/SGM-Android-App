package com.go.sgm_android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.go.sgm_android.databinding.ActivityAddPowerPlantBinding;
import com.go.sgm_android.model.PowerPlant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddPowerPlantActivity extends AppCompatActivity {

    private ActivityAddPowerPlantBinding binding;
    private DatabaseReference mDatabase;
    private AutoCompleteTextView divisionAutoCompleteTextView, districtAutoCompleteTextView, upazillaAutoCompleteTextView;
    private AutoCompleteTextView autoCompleteTextViewOperator;
    private AutoCompleteTextView autoCompleteTextViewOwnership;
    private AutoCompleteTextView autoCompleteTextViewFuelType;
    private AutoCompleteTextView autoCompleteTextViewMethod;
    private ProgressDialog progressDialog;
    // Define variables to keep track of the previously selected division and district
    private String previousDivision = "";
    private String previousDistrict = "";

    private ArrayAdapter<String> divisionAdapter, districtAdapter, upazillaAdapter;

    // Define your divisions, districts, and upazillas arrays here
    private String[] divisions = {"Dhaka", "Chittagong", "Rajshahi"};
    private String[][] districts = {
            {"Dhaka", "Gazipur", "Tangail"},
            {"Chittagong", "Cox's Bazar", "Chandpur"},
            {"Rajshahi", "Bogra", "Naogaon"}
    };
    private String[][][] upazillas = {
            {
                    {"Dhaka", "Savar", "Gulshan"},
                    {"Gazipur", "Tongi", "Kaliakair"},
                    {"Tangail", "Mirzapur", "Gopalpur"}
            },
            {
                    {"Chittagong", "Chittagong Sadar", "Anwara"},
                    {"Cox's Bazar", "Cox's Bazar Sadar", "Teknaf"},
                    {"Chandpur", "Chandpur Sadar", "Haimchar"}
            },
            {
                    {"Rajshahi", "Rajshahi Sadar", "Paba"},
                    {"Bogra", "Bogra Sadar", "Shibganj"},
                    {"Naogaon", "Naogaon Sadar", "Sapahar"}
            }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPowerPlantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Change notification color
        getWindow().setStatusBarColor(getResources().getColor(R.color.glitter_lake));

        // Set the title of the activity
        setTitle("Add Power Plant");

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding Power Plant...");
        progressDialog.setCancelable(false);

        // Initialize AutoCompleteTextViews
        divisionAutoCompleteTextView = findViewById(R.id.autoCompleteTextViewDivision);
        districtAutoCompleteTextView = findViewById(R.id.autoCompleteTextViewDistrict);
        upazillaAutoCompleteTextView = findViewById(R.id.autoCompleteTextViewUpazilla);
        autoCompleteTextViewOperator = findViewById(R.id.autoCompleteTextViewOperator);
        autoCompleteTextViewOwnership = findViewById(R.id.autoCompleteTextViewOwnership);
        autoCompleteTextViewFuelType = findViewById(R.id.autoCompleteTextViewFuelType);
        autoCompleteTextViewMethod = findViewById(R.id.autoCompleteTextViewMethod);

        // Initialize Adapters
        divisionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, divisions);
        divisionAutoCompleteTextView.setAdapter(divisionAdapter);

        districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        districtAutoCompleteTextView.setAdapter(districtAdapter);

        upazillaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        upazillaAutoCompleteTextView.setAdapter(upazillaAdapter);

        // Set listeners for AutoCompleteTextViews
        divisionAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                updateDistrictAutoCompleteTextView(position);
                // Clear the text of the district AutoCompleteTextView when division changes
                districtAutoCompleteTextView.setText("");
                // Clear the text of the upazilla AutoCompleteTextView when division changes
                upazillaAutoCompleteTextView.setText("");
                // Clear the upazilla adapter when the division changes
                upazillaAdapter.clear();
                upazillaAdapter.notifyDataSetChanged();
            }
        });

        districtAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int divisionPosition = divisionAdapter.getPosition(divisionAutoCompleteTextView.getText().toString());
                updateUpazillaAutoCompleteTextView(divisionPosition, position);
                // Clear the text of the upazilla AutoCompleteTextView when district changes
                upazillaAutoCompleteTextView.setText("");
            }
        });

        ArrayAdapter<String> operatorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.operators_array));
        autoCompleteTextViewOperator.setAdapter(operatorAdapter);

        ArrayAdapter<String> ownershipAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.ownership_array));
        autoCompleteTextViewOwnership.setAdapter(ownershipAdapter);

        ArrayAdapter<String> fuelTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.fuel_types_array));
        autoCompleteTextViewFuelType.setAdapter(fuelTypeAdapter);

        ArrayAdapter<String> methodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.methods_array));
        autoCompleteTextViewMethod.setAdapter(methodAdapter);

        // Handle button click
        binding.addPowerPlantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String division = divisionAutoCompleteTextView.getText().toString().trim();
                String district = districtAutoCompleteTextView.getText().toString().trim();
                String upazilla = upazillaAutoCompleteTextView.getText().toString().trim();
                String operator = autoCompleteTextViewOperator.getText().toString().trim();
                String ownership = autoCompleteTextViewOwnership.getText().toString().trim();
                String fuelType = autoCompleteTextViewFuelType.getText().toString().trim();
                String method = autoCompleteTextViewMethod.getText().toString().trim();
                String output = binding.outputEditText.getText().toString().trim();
                String name = binding.addPowerPlantEditText.getText().toString().trim();

                if (!division.isEmpty() && !district.isEmpty() && !upazilla.isEmpty() && !operator.isEmpty() && !ownership.isEmpty() && !fuelType.isEmpty() && !method.isEmpty() && !output.isEmpty() && !name.isEmpty()) {
//                    Toast.makeText(AddPowerPlantActivity.this, "Hello "+division+district+upazilla+operator+ownership+fuelType+method+output+name, Toast.LENGTH_SHORT).show();
                    progressDialog.show();
                    addPowerPlantToDatabase(division, district, upazilla, operator, ownership, fuelType, method, output, name);
                } else {
                    Toast.makeText(AddPowerPlantActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addPowerPlantToDatabase(String division, String district, String upazilla, String operator,
                                         String ownership, String fuelType, String method, String output, String name) {

        // Reference to "SGM/PowerPlant"
        DatabaseReference powerPlantRef = mDatabase.child("SGM").child("PowerPlant").push();

        // Create a map to store the power plant data
        PowerPlant powerPlant = new PowerPlant(division, district, upazilla, operator, ownership, fuelType, method, output, name);

        // Push the power plant data to the database
        powerPlantRef.setValue(powerPlant)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(AddPowerPlantActivity.this, MainActivity.class);
                        // Start SecondActivity
                        startActivity(intent);
                        Toast.makeText(AddPowerPlantActivity.this, "Power plant added successfully", Toast.LENGTH_SHORT).show();
                        // Clear input fields after successful addition
                        binding.addPowerPlantEditText.setText("");
                        binding.outputEditText.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddPowerPlantActivity.this, "Failed to add power plant: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateDistrictAutoCompleteTextView(int divisionPosition) {
        // Update districts AutoCompleteTextView based on selected division
        String[] selectedDistricts = districts[divisionPosition];
        districtAdapter.clear();
        districtAdapter.addAll(selectedDistricts);
        districtAdapter.notifyDataSetChanged();
    }

    private void updateUpazillaAutoCompleteTextView(int divisionPosition, int districtPosition) {
        // Update upazillas AutoCompleteTextView based on selected district and division
        String[] selectedUpazillas = upazillas[divisionPosition][districtPosition];
        upazillaAdapter.clear();
        upazillaAdapter.addAll(selectedUpazillas);
        upazillaAdapter.notifyDataSetChanged();
    }
}
