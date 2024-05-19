package com.go.sgm_android;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.go.sgm_android.Adapter.PowerPlantAdapter;
import com.go.sgm_android.Adapter.PredictionAdapter;
import com.go.sgm_android.databinding.ActivityPredictionBinding;
import com.go.sgm_android.model.PowerPlant;
import com.go.sgm_android.model.Prediction;
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

public class PredictionActivity extends AppCompatActivity {

    private ActivityPredictionBinding binding;
    static String selectedDate = "";
    private PredictionAdapter powerPlantAdapter;
    private ArrayAdapter<String> ppnameAdapter;
    private AutoCompleteTextView nameAutoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPredictionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Change notification color
        getWindow().setStatusBarColor(getResources().getColor(R.color.glitter_lake));

        // Set the title of the activity
        setTitle("AI Prediction");

        binding.marquee.setSelected(true);

        binding.pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass the button reference to the DatePickerFragment
                DialogFragment newFragment = new DatePickerFragment(binding.pickDate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        // Initialize AutoCompleteTextViews
        nameAutoCompleteTextView = findViewById(R.id.autoCompleteTextViewName);

        // Initialize Adapters
        ppnameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);

        // Set the adapter for power plant names
        nameAutoCompleteTextView.setAdapter(ppnameAdapter);

        // Fetch power plant names from Firebase and update ppnameAdapter
        fetchPowerPlantNames();

        // Set a listener for the typeAutoCompleteTextView to fetch data accordingly
        nameAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = nameAutoCompleteTextView.getText().toString();

            // Check if any field is empty
            if (selectedDate.isEmpty() || selectedName.isEmpty() || selectedDate.equals("Select Date")) {
//                Toast.makeText(PredictionActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Initialize RecyclerView
                RecyclerView recyclerView1 = binding.PredictionRV;
                recyclerView1.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                // Initialize adapters
                powerPlantAdapter = new PredictionAdapter(new ArrayList<>());
                // Set up adapter
                recyclerView1.setAdapter(powerPlantAdapter);
                // Fetch power plant data from Firebase
                fetchPredictionData(selectedDate, selectedName);

//                Toast.makeText(PredictionActivity.this, "PP " + selectedName + " " + selectedDate, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPredictionData(String selectedDate, String selectedName) {
        // Implementation for fetching prediction data

        DatabaseReference powerPlantRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("Prediction");

        powerPlantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Prediction> powerPlants = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    if (snapshot.child("ppname").getValue(String.class).equals(selectedName)) {
//                        String name = snapshot.child("ppname").getValue(String.class);
                        float targetCapacity = snapshot.child("Date").child(selectedDate).child("capacity").child("pptargetCapacity").getValue(float.class);
                        float totalCapacity = snapshot.child("Date").child(selectedDate).child("total").child("pptotalCurrentCapacity").getValue(float.class);
                        // You can also fetch other fields similarly
                        Prediction powerPlant = new Prediction(selectedName, targetCapacity, totalCapacity);
                        powerPlants.add(powerPlant);
                    }else {
                        Toast.makeText(PredictionActivity.this, "Not Found.", Toast.LENGTH_SHORT).show();
                    }
                }
                // Now you have all PowerPlant objects from powerPlantRef, update your adapter or UI here
                // If needed, add logic here to merge or process data from both references
                powerPlantAdapter.setPowerPlants(powerPlants);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PowerPlantListActivity", "Failed to fetch power plant data from powerPlantRef: " + databaseError.getMessage());
            }
        });
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
                Log.e("PredictionActivity", "Failed to fetch power plant data from powerPlantRef: " + databaseError.getMessage());
            }
        });
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private Button pickDateButton;
        private Calendar minDate;
        private Calendar maxDate;

        public DatePickerFragment(Button pickDateButton) {
            this.pickDateButton = pickDateButton;
            minDate = Calendar.getInstance();
            minDate.add(Calendar.DAY_OF_MONTH, 1); // Set min date to next day
            maxDate = Calendar.getInstance();
            maxDate.add(Calendar.DAY_OF_MONTH, 7); // Set max date to 7 days from today
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
            DatePicker datePicker = dialog.getDatePicker();
            datePicker.setMinDate(minDate.getTimeInMillis()); // Set minimum date to next day
            datePicker.setMaxDate(maxDate.getTimeInMillis()); // Set maximum date to 7 days from today
            return dialog;
        }

        // Callback method when the user sets the date
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Display the selected date in the button
            String selectedDate = String.format("%02d-%02d-%d", day, month + 1, year);
            pickDateButton.setText(selectedDate);
            // Update the selectedDate variable in the PredictionActivity
            PredictionActivity.selectedDate = selectedDate;
        }
    }

}
