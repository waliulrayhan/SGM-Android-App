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
    private AutoCompleteTextView autoCompleteTextViewDivision;
    private AutoCompleteTextView autoCompleteTextViewDistrict;
    private AutoCompleteTextView autoCompleteTextViewUpazilla;
    private AutoCompleteTextView autoCompleteTextViewOperator;
    private AutoCompleteTextView autoCompleteTextViewOwnership;
    private AutoCompleteTextView autoCompleteTextViewFuelType;
    private AutoCompleteTextView autoCompleteTextViewMethod;
    private ProgressDialog progressDialog;


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

        // Initialize AutoCompleteTextView
        autoCompleteTextViewDivision = findViewById(R.id.autoCompleteTextViewDivision);
        autoCompleteTextViewDistrict = findViewById(R.id.autoCompleteTextViewDistrict);
        autoCompleteTextViewUpazilla = findViewById(R.id.autoCompleteTextViewUpazilla);
        autoCompleteTextViewOperator = findViewById(R.id.autoCompleteTextViewOperator);
        autoCompleteTextViewOwnership = findViewById(R.id.autoCompleteTextViewOwnership);
        autoCompleteTextViewFuelType = findViewById(R.id.autoCompleteTextViewFuelType);
        autoCompleteTextViewMethod = findViewById(R.id.autoCompleteTextViewMethod);

        // Set up the adapter for AutoCompleteTextView
        ArrayAdapter<String> divisionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.divisions_array));
        autoCompleteTextViewDivision.setAdapter(divisionAdapter);

        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.districts_array));
        autoCompleteTextViewDistrict.setAdapter(districtAdapter);

        ArrayAdapter<String> upazillaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.upazillas_array));
        autoCompleteTextViewUpazilla.setAdapter(upazillaAdapter);

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
                String division = autoCompleteTextViewDivision.getText().toString().trim();
                String district = autoCompleteTextViewDistrict.getText().toString().trim();
                String upazilla = autoCompleteTextViewUpazilla.getText().toString().trim();
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
}
