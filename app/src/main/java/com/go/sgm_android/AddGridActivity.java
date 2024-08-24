package com.go.sgm_android;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.go.sgm_android.databinding.ActivityAddCommentBinding;
import com.go.sgm_android.databinding.ActivityAddGridBinding;
import com.go.sgm_android.model.Distributor;
import com.go.sgm_android.model.Grid;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.Random;

public class AddGridActivity extends AppCompatActivity {

    private ActivityAddGridBinding binding;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddGridBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Change notification color
        getWindow().setStatusBarColor(getResources().getColor(R.color.glitter_lake));

        // Set the title of the activity
        setTitle("Add Grid Info.");

        // Check for internet connection immediately
        if (NetworkUtil.isConnected(this)) {
            // If connected, proceed to main activity after the splash screen duration
            // Initialize Firebase Database
            mDatabase = FirebaseDatabase.getInstance().getReference();

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Adding Grid Information...");
            progressDialog.setCancelable(false);

            // Handle button click
            binding.addDistributorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = binding.addGridNameEditText.getText().toString().trim();
                    String nid = binding.addNIDEditText.getText().toString().trim();
                    String phone = binding.addPhoneNumberEditText.getText().toString().trim();
                    String address = binding.addAdressEditText.getText().toString().trim();
                    String bank = binding.addBankDetailsEditText.getText().toString().trim();

                    if (!name.isEmpty() && !nid.isEmpty() && !phone.isEmpty() && !address.isEmpty() && !bank.isEmpty()) {
//                        Toast.makeText(AddGridActivity.this, "Hello " + name + nid + phone + address + bank, Toast.LENGTH_LONG).show();
                        progressDialog.show();
                        addGridDataToDatabase(name, nid, phone, address, bank);
//                    uploadDDDataToFirebase();
                    } else {
                        Toast.makeText(AddGridActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    }
                }

            });
        } else {
            // If not connected, show the no internet connection dialog
            showNoInternetDialog();
        }
    }

    private void addGridDataToDatabase(String name, String nid, String phone, String address, String bank) {
        // Reference to "SGM/PowerPlant"
        DatabaseReference distributorRef = mDatabase.child("SGM").child("Grid").push();

        // Create a map to store the distributor data
        Random random = new Random();
        float supply = random.nextFloat() * 50;

        // Create a map to store the distributor data
        Grid Data = new Grid(name, nid, phone, address,bank,supply);

        // Run a transaction to push the distributor data to the database
        distributorRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                // Set the distributor data
                mutableData.setValue(Data);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError == null) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(AddGridActivity.this, MainActivity.class);
                    // Start MainActivity
                    startActivity(intent);
                    Toast.makeText(AddGridActivity.this, "Grid Information added successfully", Toast.LENGTH_SHORT).show();
                    // Clear input fields after successful addition
                    binding.addGridNameEditText.setText("");
                    binding.addNIDEditText.setText("");
                    binding.addPhoneNumberEditText.setText("");
                    binding.addAdressEditText.setText("");
                    binding.addBankDetailsEditText.setText("");
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AddGridActivity.this, "Failed to add Data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showNoInternetDialog() {
        new AlertDialog.Builder(AddGridActivity.this)
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recreate(); // Restart the activity to check connection again
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish(); // Close the app
                    }
                })
                .setCancelable(false)
                .show();
    }
}