package com.go.sgm_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.go.sgm_android.databinding.ActivityAddPowerPlantBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddPowerPlantActivity extends AppCompatActivity {

    private ActivityAddPowerPlantBinding binding;
    private DatabaseReference mDatabase;
    private AlertDialog progressDialog;

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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Uploading Your Post");
        builder.setMessage("Please wait...");
        builder.setCancelable(false);
        progressDialog = builder.create();

        binding.addPowerPlantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.addPowerPlantEditText.getText().toString().trim();
                if (!name.isEmpty()) {
                    // Show progress dialog
                    progressDialog.show();
                    // Add power plant to Firebase Realtime Database
                    addPowerPlantToDatabase(name);
                } else {
                    Toast.makeText(AddPowerPlantActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addPowerPlantToDatabase(String name) {
        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Reference to "SGM/currentDate/power_plants/name"
        DatabaseReference powerPlantRef = mDatabase.child("SGM").child(currentDate).child("power_plants").child(name);

        // Perform transaction to set current capacity and target capacity to 0
        powerPlantRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                // Ensure data is not null
                if (mutableData.getValue() == null) {
                    mutableData.child("current_capacity").setValue(0);
                    mutableData.child("target_capacity").setValue(0);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError == null) {

                    // Display success message
                    Toast.makeText(AddPowerPlantActivity.this, "Power plant " + name + " added successfully", Toast.LENGTH_SHORT).show();
                    // Start MainActivity
                    Intent intent = new Intent(AddPowerPlantActivity.this, MainActivity.class);
                    // Dismiss progress dialog
                    progressDialog.dismiss();
                    startActivity(intent);
                    // Finish current activity to prevent going back to it when pressing back button from MainActivity
                    finish();
                } else {
                    // Dismiss progress dialog
                    progressDialog.dismiss();
                    // Handle transaction error
                    Toast.makeText(AddPowerPlantActivity.this, "Failed to add power plant: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}