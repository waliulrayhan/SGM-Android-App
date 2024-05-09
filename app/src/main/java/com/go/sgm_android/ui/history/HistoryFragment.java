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
import androidx.core.content.ContextCompat;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
            binding.card.setVisibility(View.GONE);
            binding.card2.setVisibility(View.GONE);
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

                if (selectedType.isEmpty() && selectedDate.isEmpty()) {
                    // Both name and selectedDate are empty
                    Toast.makeText(getContext(), "Please select a type and a date", Toast.LENGTH_SHORT).show();
                } else if (selectedType.isEmpty()) {
                    // Name is empty
                    Toast.makeText(getContext(), "Please select a type", Toast.LENGTH_SHORT).show();
                } else if (selectedDate.isEmpty()) {
                    // Selected date is empty
                    Toast.makeText(getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
                } else {
                    // Both name and selectedDate are not empty, proceed with search
                    // Here you can implement the logic for searching based on name and selectedDate
                    // For now, let's just show a toast message indicating search is triggered

                    if (selectedType.equals("All Data")){
                        powerPlantAdapter.clear();
                        distributorAdapter.clear();
                        fetchDataFromFirebase3(selectedType, selectedDate);
                        fetchTotalDataFromFirebase3(selectedDate);
                    } else if (selectedType.equals("Power Plant")) {
                        distributorAdapter.clear();
                        fetchDataFromFirebase1(selectedType, selectedDate);
                        fetchTotalDataFromFirebase1(selectedDate);
                    } else if (selectedType.equals("Distributor")){
                        powerPlantAdapter.clear();
                        fetchDataFromFirebase2(selectedType, selectedDate);
                        fetchTotalDataFromFirebase2(selectedDate);
                    }
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

        } catch (Exception e) {
            // Example: Displaying a toast message to the user
            Toast.makeText(getContext(), "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return root;
    }

    private void fetchDataFromFirebase1(String name, String selectedDate) {

        try {
            // Construct the Firebase reference path for the current date
            DatabaseReference powerPlantRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("PowerPlant");
            powerPlantRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<PowerPlant> powerPlants = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        if (snapshot.child("ppname").exists() && snapshot.child("Date").child(selectedDate).exists()) {
                            binding.dataTextView.setVisibility(View.GONE);
//                            binding.card.setVisibility(View.VISIBLE);

                            String name = snapshot.child("ppname").getValue(String.class);
                            float currentCapacity = snapshot.child("Date").child(selectedDate).child("capacity").child("pptargetCapacity").getValue(float.class);
                            float targetCapacity = snapshot.child("Date").child(selectedDate).child("total").child("pptotalCurrentCapacity").getValue(float.class);
                            // You can also fetch other fields similarly
                            PowerPlant powerPlant = new PowerPlant(name, targetCapacity, currentCapacity);
                            powerPlants.add(powerPlant);
                        }else {
                            binding.dataTextView.setText("No Data Found for the Selected Date");
                            binding.dataTextView.setVisibility(View.VISIBLE);

//                            binding.card.setVisibility(View.GONE);
//                            binding.card2.setVisibility(View.GONE);
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
        }catch (Exception e){
            // Example: Displaying a toast message to the user
            Toast.makeText(getContext(), "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
                        binding.dataTextView.setVisibility(View.GONE);
                        binding.card2.setVisibility(View.VISIBLE);

                        float currentDemand = snapshot.child("Date").child(selectedDate).child("total").child("ddtotalCurrentdemand").getValue(float.class);
                        float targetDemand = snapshot.child("Date").child(selectedDate).child("demand").child("ddtargetdemand").getValue(float.class);
                        // You can also fetch other fields similarly
                        Distributor distributor = new Distributor(distributorKey, currentDemand, targetDemand);
                        distributors.add(distributor);
                    }else {
                        binding.dataTextView.setText("No Data Found for the Selected Date");
                        binding.dataTextView.setVisibility(View.VISIBLE);

                        binding.card.setVisibility(View.GONE);
                        binding.card2.setVisibility(View.GONE);
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
                        binding.dataTextView.setVisibility(View.GONE);
                        binding.card.setVisibility(View.VISIBLE);
                        binding.card2.setVisibility(View.VISIBLE);

                        String name = snapshot.child("ppname").getValue(String.class);
                        float currentCapacity = snapshot.child("Date").child(selectedDate).child("capacity").child("pptargetCapacity").getValue(float.class);
                        float targetCapacity = snapshot.child("Date").child(selectedDate).child("total").child("pptotalCurrentCapacity").getValue(float.class);
                        // You can also fetch other fields similarly
                        PowerPlant powerPlant = new PowerPlant(name, targetCapacity, currentCapacity);
                        powerPlants.add(powerPlant);
                    }else {
                        binding.dataTextView.setText("No Data Found for the Selected Date");
                        binding.dataTextView.setVisibility(View.VISIBLE);

                        binding.card.setVisibility(View.GONE);
                        binding.card2.setVisibility(View.GONE);
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
                        binding.dataTextView.setVisibility(View.GONE);

                        float currentDemand = snapshot.child("Date").child(selectedDate).child("total").child("ddtotalCurrentdemand").getValue(float.class);
                        float targetDemand = snapshot.child("Date").child(selectedDate).child("demand").child("ddtargetdemand").getValue(float.class);
                        // You can also fetch other fields similarly
                        Distributor distributor = new Distributor(distributorKey, currentDemand, targetDemand);
                        distributors.add(distributor);
                    }else {
                        binding.dataTextView.setText("No Data Found for the Selected Date");
                        binding.dataTextView.setVisibility(View.VISIBLE);
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

    private void fetchTotalDataFromFirebase1(String selectedDate) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("SGM").child("Date").child(selectedDate);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve total nodes
                    DataSnapshot totalsSnapshot = dataSnapshot.child("total");

                    // Fetch total target capacity value
                    float totaltargetCapacityValue = totalsSnapshot.child("AllpptargetCapacity").getValue(float.class);
                    // Fetch total current capacity value
                    float totalCurrentCapacityValue = totalsSnapshot.child("AllppcurrentCapacity").getValue(float.class);
                    // Fetch total target Demand value
//                        float totaltargetDemandValue = totalsSnapshot.child("AllddtargetDemand").getValue(float.class);
                    // Fetch total current Demand value
//                        float totalCurrentDemandValue = totalsSnapshot.child("AllddcurrentDemand").getValue(float.class);

                    binding.dataTextView.setVisibility(View.GONE);
                    binding.card.setVisibility(View.VISIBLE);
                    binding.card2.setVisibility(View.GONE);
//                     Update UI with fetched values
                    binding.AllpptargetCapacity.setText("Total Target Capacity of Power Plants: "+String.valueOf(totaltargetCapacityValue)+" MW");
//                     Update UI with fetched values
                    binding.AllppcurrentCapacity.setText("Total Capacity Supply: "+String.valueOf(totalCurrentCapacityValue)+" MW");
//                     Update UI with fetched values
//                    binding.ddTotalCurrentDemand.setText("Total Target Demand of Distributors: "+String.valueOf(totalCurrentDemandValue)+" MW");
//                     Update UI with fetched values
//                    binding.ddTotalCurrentDemand.setText("Total Demand Supply: "+String.valueOf(totalCurrentDemandValue)+" MW");
                } else {
                    // Handle case when data doesn't exist
                    // You can display a message or take appropriate action
                    binding.dataTextView.setText("No Data Found for the Selected Date");
                    binding.dataTextView.setVisibility(View.VISIBLE);

                    binding.card.setVisibility(View.GONE);
                    binding.card2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

    private void fetchTotalDataFromFirebase2(String selectedDate) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("SGM").child("Date").child(selectedDate);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve total nodes
                    DataSnapshot totalsSnapshot = dataSnapshot.child("total");

                    // Fetch total target capacity value
//                    float totaltargetCapacityValue = totalsSnapshot.child("AllpptargetCapacity").getValue(float.class);
                    // Fetch total current capacity value
//                    float totalCurrentCapacityValue = totalsSnapshot.child("AllppcurrentCapacity").getValue(float.class);
                    // Fetch total target Demand value
                        float totaltargetDemandValue = totalsSnapshot.child("AllddtargetDemand").getValue(float.class);
                    // Fetch total current Demand value
                        float totalCurrentDemandValue = totalsSnapshot.child("AllddcurrentDemand").getValue(float.class);

                    binding.dataTextView.setVisibility(View.GONE);
                    binding.card2.setVisibility(View.VISIBLE);
                    binding.card.setVisibility(View.GONE);
//                     Update UI with fetched values
//                    binding.AllpptargetCapacity.setText("Total Target Capacity of Power Plants: "+String.valueOf(totaltargetCapacityValue)+" MW");
//                     Update UI with fetched values
//                    binding.AllppcurrentCapacity.setText("Total Capacity Supply: "+String.valueOf(totalCurrentCapacityValue)+" MW");
//                     Update UI with fetched values
                    binding.AllddtargetDemand.setText("Total Target Demand of Distributors: "+String.valueOf(totaltargetDemandValue)+" MW");
//                     Update UI with fetched values
                    binding.AllddcurrentDemand.setText("Total Demand Supply: "+String.valueOf(totalCurrentDemandValue)+" MW");
                } else {
                    // Handle case when data doesn't exist
                    // You can display a message or take appropriate action
                    binding.dataTextView.setText("No Data Found for the Selected Date");
                    binding.dataTextView.setVisibility(View.VISIBLE);

                    binding.card.setVisibility(View.GONE);
                    binding.card2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

    private void fetchTotalDataFromFirebase3(String selectedDate) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("SGM").child("Date").child(selectedDate);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve total nodes
                    DataSnapshot totalsSnapshot = dataSnapshot.child("total");

                    // Fetch total target capacity value
                    float totaltargetCapacityValue = totalsSnapshot.child("AllpptargetCapacity").getValue(float.class);
                    // Fetch total current capacity value
                    float totalCurrentCapacityValue = totalsSnapshot.child("AllppcurrentCapacity").getValue(float.class);
                    // Fetch total target Demand value
                    float totaltargetDemandValue = totalsSnapshot.child("AllddtargetDemand").getValue(float.class);
                    // Fetch total current Demand value
                    float totalCurrentDemandValue = totalsSnapshot.child("AllddcurrentDemand").getValue(float.class);

                    binding.dataTextView.setVisibility(View.GONE);
                    binding.card2.setVisibility(View.VISIBLE);
                    binding.card.setVisibility(View.VISIBLE);
//                     Update UI with fetched values
                    binding.AllpptargetCapacity.setText("Total Target Capacity of Power Plants: "+String.valueOf(totaltargetCapacityValue)+" MW");
//                     Update UI with fetched values
                    binding.AllppcurrentCapacity.setText("Total Capacity Supply: "+String.valueOf(totalCurrentCapacityValue)+" MW");
//                     Update UI with fetched values
                    binding.AllddtargetDemand.setText("Total Target Demand of Distributors: "+String.valueOf(totaltargetDemandValue)+" MW");
//                     Update UI with fetched values
                    binding.AllddcurrentDemand.setText("Total Demand Supply: "+String.valueOf(totalCurrentDemandValue)+" MW");
                } else {
                    // Handle case when data doesn't exist
                    // You can display a message or take appropriate action
                    binding.dataTextView.setText("No Data Found for the Selected Date");
                    binding.dataTextView.setVisibility(View.VISIBLE);

                    binding.card.setVisibility(View.GONE);
                    binding.card2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
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
        private Calendar maxDateCalendar;

        public DatePickerFragment(Button pickDateButton) {
            this.pickDateButton = pickDateButton;

            // Set maximum date to yesterday
            maxDateCalendar = Calendar.getInstance();
            maxDateCalendar.add(Calendar.DAY_OF_MONTH, -1);
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
            DatePickerDialog dialog = new DatePickerDialog(requireContext(), this, year, month, day);

            // Set maximum date to yesterday
            dialog.getDatePicker().setMaxDate(maxDateCalendar.getTimeInMillis());

            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Update the text of the button with the selected date
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
