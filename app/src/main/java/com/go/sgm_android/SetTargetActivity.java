package com.go.sgm_android;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.go.sgm_android.databinding.ActivitySetTargetBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SetTargetActivity extends AppCompatActivity {

    ActivitySetTargetBinding binding;
    private AutoCompleteTextView typeAutoCompleteTextView, nameAutoCompleteTextView;
    private ArrayAdapter<String> typeAdapter, ppnameAdapter, ddnameAdapter;
    private String[] type = {"Power Plant", "Distributor"};
    static String selectedDate;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetTargetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Change notification color
        getWindow().setStatusBarColor(getResources().getColor(R.color.glitter_lake));

        // Set the title of the activity
        setTitle("Set Target");

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading data...");
        progressDialog.setCancelable(false);

        binding.pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass the button reference to the DatePickerFragment
                DialogFragment newFragment = new DatePickerFragment(binding.pickDate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        // Initialize AutoCompleteTextViews
        typeAutoCompleteTextView = findViewById(R.id.autoCompleteTextViewType);
        nameAutoCompleteTextView = findViewById(R.id.autoCompleteTextViewName);

        // Initialize Adapters
        typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, type);
        ppnameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        ddnameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);

        // Set adapters to typeAutoCompleteTextView
        typeAutoCompleteTextView.setAdapter(typeAdapter);

        // Set a listener for the typeAutoCompleteTextView to fetch data accordingly
        typeAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedType = (String) parent.getItemAtPosition(position);
            if (selectedType.equals("Power Plant")) {
                // Clear the text in the nameAutoCompleteTextView
                nameAutoCompleteTextView.setText("");
                // Set the adapter for power plant names
                nameAutoCompleteTextView.setAdapter(ppnameAdapter);
                // Fetch power plant names from Firebase and update ppnameAdapter
                fetchPowerPlantNames();
            } else if (selectedType.equals("Distributor")) {
                // Clear the text in the nameAutoCompleteTextView
                nameAutoCompleteTextView.setText("");
                // Set the adapter for distributor names
                nameAutoCompleteTextView.setAdapter(ddnameAdapter);
                // Show hardcoded distributor names
                String[] distributorNames = {"Bangladesh Power Development Board", "Bangladesh Rural Electrification Board", "Dhaka Electric Supply Company Limited", "Dhaka Power Distribution Company Limited", "Northern Electricity Supply Company PLC", "West Zone Power Distribution Company Limited"};
                ddnameAdapter.clear();
                ddnameAdapter.addAll(distributorNames);
                ddnameAdapter.notifyDataSetChanged();
            }
        });

        // Set a click listener for the save button
        binding.setTargetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected type and name
                String selectedType = typeAutoCompleteTextView.getText().toString();
                String selectedName = nameAutoCompleteTextView.getText().toString();
                String ss = binding.targetEditText.getText().toString().trim();
                float selectedTarget = Float.parseFloat(ss);

                if (!selectedDate.isEmpty() && !selectedType.isEmpty() && !selectedName.isEmpty() && !ss.isEmpty()){
                    // Show toast with selected type and name
                    String message = "Date: "+selectedDate+" Selected Type: " + selectedType + ", Selected Name: " + selectedName + ", Target: "+selectedTarget;
                    Toast.makeText(SetTargetActivity.this, message, Toast.LENGTH_SHORT).show();

                    setTargetDataIntoFirebase(selectedDate, selectedType, selectedName, selectedTarget);
                    progressDialog.show();
                }
                else {
                    Toast.makeText(SetTargetActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void setTargetDataIntoFirebase(String selectedDate, String selectedType, String selectedName, float selectedTarget) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("SGM");

        if (selectedType.equals("Power Plant")){
            // Upload PowerPlant data
            DatabaseReference powerPlantRef = databaseRef.child("PowerPlant");

            powerPlantRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("ppname").exists() && snapshot.child("ppname").getValue(String.class).equals(selectedName)) {
                            // This snapshot corresponds to the selected power plant
                            DatabaseReference powerPlantDateRef = snapshot.child("Date").child(selectedDate).getRef();
                            powerPlantDateRef.child("capacity").child("pptargetCapacity").setValue(selectedTarget)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SetTargetActivity.this, "Data uploaded successfully", Toast.LENGTH_SHORT).show();
                                                // Navigate to MainActivity
                                                startActivity(new Intent(SetTargetActivity.this, MainActivity.class));
                                                finish(); // Close the current activity
                                            } else {
                                                Toast.makeText(SetTargetActivity.this, "Failed to upload data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                    Log.e("UploadDataToFirebase", "Failed to upload power plant data: " + databaseError.getMessage());
                    Toast.makeText(SetTargetActivity.this, "Failed to upload data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (selectedType.equals("Distributor")){
            // Upload Distributor Data
            DatabaseReference distributorRef = databaseRef.child("Distributor").child(selectedName);
            distributorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DatabaseReference distributorDateRef = distributorRef.child("Date").child(selectedDate).getRef();
                    distributorDateRef.child("demand").child("ddtargetdemand").setValue(selectedTarget)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SetTargetActivity.this, "Data uploaded successfully", Toast.LENGTH_SHORT).show();
                                        // Navigate to MainActivity
                                        startActivity(new Intent(SetTargetActivity.this, MainActivity.class));
                                        finish(); // Close the current activity
                                    } else {
                                        Toast.makeText(SetTargetActivity.this, "Failed to upload data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                    Log.e("UploadDataToFirebase", "Failed to upload distributor data: " + databaseError.getMessage());
                    Toast.makeText(SetTargetActivity.this, "Failed to upload data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchPowerPlantNames() {
        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        DatabaseReference powerPlantRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("PowerPlant");

        powerPlantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> powerPlantNames = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    if (snapshot.child("ppname").exists()) {
                        String name = snapshot.child("ppname").getValue(String.class);
                        powerPlantNames.add(name);
                    }
                }
                // Update ppnameAdapter with the fetched power plant names
                ppnameAdapter.clear();
                ppnameAdapter.addAll(powerPlantNames);
                ppnameAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PowerPlantListActivity", "Failed to fetch power plant data from powerPlantRef: " + databaseError.getMessage());
            }
        });
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private Button pickDateButton;
        private Calendar minDate;
        private Calendar maxDate;

        public DatePickerFragment(Button pickDateButton) {
            this.pickDateButton = pickDateButton;
            minDate = Calendar.getInstance();
            maxDate = Calendar.getInstance();
            maxDate.add(Calendar.DAY_OF_MONTH, 6); // Set max date to current date + 7 days
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker.
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it.
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.getDatePicker().setMinDate(minDate.getTimeInMillis()); // Set minimum date
            dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis()); // Set maximum date
            return dialog;
        }

        // Callback method when the user sets the date
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Display the selected date in the button
            selectedDate = String.format("%02d-%02d-%d", day, month + 1, year);
            pickDateButton.setText(selectedDate);
        }
    }

    private static String formatKey(String key) {
        // Modify the key to display as desired
        // For example, replace underscores with spaces and capitalize each word
        String[] words = key.split("_");
        StringBuilder formattedKey = new StringBuilder();
        for (String word : words) {
            formattedKey.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }
        return formattedKey.toString().trim();
    }
}

