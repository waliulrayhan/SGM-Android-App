package com.go.sgm_android.ui.history;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.go.sgm_android.Adapter.HistoryDistributorAdapter;
import com.go.sgm_android.Adapter.HistoryPowerPlantAdapter;
import com.go.sgm_android.Adapter.PowerPlantAdapter;
import com.go.sgm_android.R;
import com.go.sgm_android.databinding.FragmentHistoryBinding;
import com.go.sgm_android.model.Distributor;
import com.go.sgm_android.model.PowerPlant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private TextView text;
    private String name="";
    static String selectedDate="";
    private String[] type = {"All Data", "Power Plant", "Distributor"};
    private AutoCompleteTextView typeAutoCompleteTextView;
    private ArrayAdapter<String> typeAdapter;
    private HistoryPowerPlantAdapter powerPlantAdapter;
    private HistoryDistributorAdapter distributorAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        try {
            // Date Picker
            Button pickDateButton = root.findViewById(R.id.pickDate);
            text = root.findViewById(R.id.textView7);

            binding.dataTextView.setVisibility(View.GONE);
            binding.historyDataTextView.setVisibility(View.GONE);
            pickDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Pass the button reference to the DatePickerFragment
                    DialogFragment newFragment = new DatePickerFragment(pickDateButton);
                    newFragment.show(getChildFragmentManager(), "datePicker");
                }
            });

            // Initialize AutoCompleteTextViews
            typeAutoCompleteTextView = root.findViewById(R.id.autoCompleteTextViewType);

            // Initialize Adapters
            typeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, type);

            // Set adapters to typeAutoCompleteTextView
            typeAutoCompleteTextView.setAdapter(typeAdapter);

            // Set a listener for the typeAutoCompleteTextView to fetch data accordingly
            typeAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                String selectedType = (String) parent.getItemAtPosition(position);
                if (selectedType.equals("Power Plant")) {
                    name = "Power Plant";
                } else if (selectedType.equals("Distributor")) {
                    name = "Distributor";
                } else {
                    name = "All Data";
                }
            });

            //==========================================================================================
            // This is for Power Plant Recycler View
            // Initialize RecyclerView
            RecyclerView recyclerView1 = binding.powerPlantList;
            recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));

            // Initialize adapters
            powerPlantAdapter = new HistoryPowerPlantAdapter(new ArrayList<>());
            // Set up adapter
            recyclerView1.setAdapter(powerPlantAdapter);

            //==========================================================================================
            // This is for Distributor Recycler View
            // Initialize RecyclerView
            RecyclerView recyclerView2 = binding.distributorList;
            recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));

            // Initialize adapters
            distributorAdapter = new HistoryDistributorAdapter(new ArrayList<>());
            // Set up adapter
            recyclerView2.setAdapter(distributorAdapter);

            binding.searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    String selectedDate = pickDateButton.getText().toString().trim();

                    if (name.isEmpty() && selectedDate.isEmpty()) {
                        // Both name and selectedDate are empty
                        Toast.makeText(getContext(), "Please select a type and a date", Toast.LENGTH_SHORT).show();
                    } else if (name.isEmpty()) {
                        // Name is empty
                        Toast.makeText(getContext(), "Please select a type", Toast.LENGTH_SHORT).show();
                    } else if (selectedDate.isEmpty()) {
                        // Selected date is empty
                        Toast.makeText(getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
                    } else {
                        // Both name and selectedDate are not empty, proceed with search
                        // Here you can implement the logic for searching based on name and selectedDate
                        // For now, let's just show a toast message indicating search is triggered

                        if (name.equals("All Data")){
                            powerPlantAdapter.clear();
                            distributorAdapter.clear();
                            fetchDataFromFirebase3(name, selectedDate);
                        } else if (name.equals("Power Plant")) {
                            distributorAdapter.clear();
                            fetchDataFromFirebase1(name, selectedDate);
                        } else if (name.equals("Distributor")){
                            powerPlantAdapter.clear();
                            fetchDataFromFirebase2(name, selectedDate);
                        }
                    }
                }
            });

        } catch (Exception e) {
            // Example: Displaying a toast message to the user
            Toast.makeText(getContext(), "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return root;
    }

    private void fetchDataFromFirebase1(String name, String selectedDate) {

        DatabaseReference powerPlantRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("PowerPlant");
        powerPlantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<PowerPlant> powerPlants = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    if (snapshot.child("ppname").exists() && snapshot.child("Date").child(selectedDate).exists()) {
                        String name = snapshot.child("ppname").getValue(String.class);
                        float currentCapacity = snapshot.child("Date").child(selectedDate).child("capacity").child("pptargetCapacity").getValue(float.class);
                        float targetCapacity = snapshot.child("Date").child(selectedDate).child("total").child("pptotalCurrentCapacity").getValue(float.class);
                        // You can also fetch other fields similarly
                        PowerPlant powerPlant = new PowerPlant(name, targetCapacity, currentCapacity);
                        powerPlants.add(powerPlant);
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

    private void fetchDataFromFirebase2(String name, String selectedDate) {
        DatabaseReference distributorRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("Distributor");
        distributorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Distributor> distributors = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Distributor keys mapping
                    Map<String, String> distributorKeysMap = new HashMap<>();
                    distributorKeysMap.put("BPDB", "Bangladesh Power Development Board");
//                    distributorKeysMap.put("BREB", "Bangladesh Rural Electrification Board");
                    distributorKeysMap.put("DESCO", "Dhaka Electric Supply Company Limited");
//                    distributorKeysMap.put("DPDC", "Dhaka Power Distribution Company Limited");
//                    distributorKeysMap.put("WZPDCL", "West Zone Power Distribution Company");
//                    distributorKeysMap.put("NESCO", "Northern Electricity Supply Company PLC");

                    // Extract distributor key and name
                    String distributorKey = snapshot.getKey();
                    String distributorName = distributorKeysMap.get(distributorKey);

                    if (snapshot.child("Date").child(selectedDate).exists()) {
                        float currentDemand = snapshot.child("Date").child(selectedDate).child("total").child("ddtotalCurrentdemand").getValue(float.class);
                        float targetDemand = snapshot.child("Date").child(selectedDate).child("demand").child("ddtargetdemand").getValue(float.class);
                        // You can also fetch other fields similarly
                        Distributor distributor = new Distributor(distributorKey, currentDemand, targetDemand);
                        distributors.add(distributor);
                    }
                }
                // Now you have all PowerPlant objects from powerPlantRef, update your adapter or UI here
                // If needed, add logic here to merge or process data from both references
                distributorAdapter.setDistributors(distributors);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", "Failed to fetch distributor data from distributorRef: " + databaseError.getMessage());
            }
        });
    }

    private void fetchDataFromFirebase3(String name, String selectedDate) {
        DatabaseReference powerPlantRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("PowerPlant");
        powerPlantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<PowerPlant> powerPlants = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    if (snapshot.child("ppname").exists() && snapshot.child("Date").child(selectedDate).exists()) {
                        String name = snapshot.child("ppname").getValue(String.class);
                        float currentCapacity = snapshot.child("Date").child(selectedDate).child("capacity").child("pptargetCapacity").getValue(float.class);
                        float targetCapacity = snapshot.child("Date").child(selectedDate).child("total").child("pptotalCurrentCapacity").getValue(float.class);
                        // You can also fetch other fields similarly
                        PowerPlant powerPlant = new PowerPlant(name, targetCapacity, currentCapacity);
                        powerPlants.add(powerPlant);
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

        DatabaseReference distributorRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("Distributor");
        distributorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Distributor> distributors = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Distributor keys mapping
                    Map<String, String> distributorKeysMap = new HashMap<>();
                    distributorKeysMap.put("BPDB", "Bangladesh Power Development Board");
//                    distributorKeysMap.put("BREB", "Bangladesh Rural Electrification Board");
                    distributorKeysMap.put("DESCO", "Dhaka Electric Supply Company Limited");
//                    distributorKeysMap.put("DPDC", "Dhaka Power Distribution Company Limited");
//                    distributorKeysMap.put("WZPDCL", "West Zone Power Distribution Company");
//                    distributorKeysMap.put("NESCO", "Northern Electricity Supply Company PLC");

                    // Extract distributor key and name
                    String distributorKey = snapshot.getKey();
                    String distributorName = distributorKeysMap.get(distributorKey);

                    if (snapshot.child("Date").child(selectedDate).exists()) {
                        float currentDemand = snapshot.child("Date").child(selectedDate).child("total").child("ddtotalCurrentdemand").getValue(float.class);
                        float targetDemand = snapshot.child("Date").child(selectedDate).child("demand").child("ddtargetdemand").getValue(float.class);
                        // You can also fetch other fields similarly
                        Distributor distributor = new Distributor(distributorKey, currentDemand, targetDemand);
                        distributors.add(distributor);
                    }
                }
                // Now you have all PowerPlant objects from powerPlantRef, update your adapter or UI here
                // If needed, add logic here to merge or process data from both references
                distributorAdapter.setDistributors(distributors);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", "Failed to fetch distributor data from distributorRef: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        private Button pickDateButton;
        public DatePickerFragment(Button pickDateButton) {
            this.pickDateButton = pickDateButton;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker.
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it.
            return new DatePickerDialog(requireContext(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Update the text of the button with the selected date
            selectedDate = String.format("%02d-%02d-%d", day, month + 1, year);
//            TextView text = root.findViewById(R.id.textView7);
//            text.setVisibility(View.GONE);
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
