package com.go.sgm_android.ui.history;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.go.sgm_android.R;
import com.go.sgm_android.databinding.FragmentHistoryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private DatabaseReference databaseReference;
    static TextView historyTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Firebase database reference
//        databaseReference = FirebaseDatabase.getInstance().getReference().child("SGM");

        // Date Picker
        Button pickDateButton = root.findViewById(R.id.pickDate);

        historyTextView = root.findViewById(R.id.historyTextView);
        pickDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass the button reference to the DatePickerFragment
                DialogFragment newFragment = new DatePickerFragment(pickDateButton);
                newFragment.show(getChildFragmentManager(), "datePicker");
            }
        });

        return root;
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
            String selectedDate = String.format("%02d-%02d-%d", day, month + 1, year);
            pickDateButton.setText(selectedDate);

            // Query the Firebase database for the selected date
            DatabaseReference dateReference = FirebaseDatabase.getInstance().getReference()
                    .child("SGM").child(selectedDate).child("history");
            dateReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                // Inside DatePickerFragment class
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        StringBuilder historyText = new StringBuilder();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String key = snapshot.getKey();
                            Object value = snapshot.getValue();

                            if (key != null && value != null) {
                                // Modify the key to display as desired
                                String formattedKey = formatKey(key);
                                historyText.append(formattedKey).append(": ").append(value.toString()).append("\n");
                            }
                        }
                        historyTextView.setText(historyText.toString());
                    } else {
                        historyTextView.setText("No data available for the selected date");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle potential errors
                }
            });
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
