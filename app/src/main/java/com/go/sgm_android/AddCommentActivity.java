package com.go.sgm_android;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.go.sgm_android.databinding.ActivityAddCommentBinding;
import com.go.sgm_android.databinding.ActivityAddDistributorBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddCommentActivity extends AppCompatActivity {

    private ActivityAddCommentBinding binding;
    private AlertDialog loadingDialog; // Reference to the loading dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Change notification color
        getWindow().setStatusBarColor(getResources().getColor(R.color.glitter_lake));

        // Set the title of the activity
        setTitle("Add Distributor");


        // Find views in the dialog layout
        EditText editTextInput = findViewById(R.id.editTextInput);
        Button buttonSave = findViewById(R.id.buttonSave);


        // Set click listener for Save button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input from EditText
                String inputData = editTextInput.getText().toString().trim();

                // Handle saving logic here
                // For example, you can save data to Firebase or perform other actions

                if (inputData.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please, Fill the Comment Field.", Toast.LENGTH_SHORT).show();
                }

                if (!inputData.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Hello " + inputData, Toast.LENGTH_SHORT).show();
                    uploadCentralCommandToFirebase(inputData);
                    showLoadingDialog();
                }
            }
        });

    }

    private void uploadCentralCommandToFirebase(String inputData) {

        try {
            // Get the current date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String currentDate = dateFormat.format(new Date());

            // Reference to "SGM/Date/currentDate/comments"
            DatabaseReference commentRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("SGM")
                    .child("Date")
                    .child(currentDate)
                    .child("comments");

            // Push the comment to generate a unique key
            String commentKey = commentRef.push().getKey();

            // Create a map to hold the comment data
            Map<String, Object> commentData = new HashMap<>();
            commentData.put("comment", inputData);

            // Upload the comment to Firebase using a transaction
            if (commentKey != null) {
                commentRef.child(commentKey).runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        // Set the comment data
                        mutableData.setValue(commentData);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                        if (databaseError == null) {
                            // Comment uploaded successfully
                            Log.d("UploadCentralCommand", "Comment uploaded successfully");

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);

                            // You can add any additional actions you want to perform on success
                            hideLoadingDialog();
                        } else {
                            // Failed to upload comment
                            Log.e("UploadCentralCommand", "Failed to upload comment: " + databaseError.getMessage());
                            // Handle the error, if needed
                            hideLoadingDialog();
                        }
                    }
                });
            }
        } catch (Exception e) {
            // Example: Displaying a toast message to the user
            Toast.makeText(getApplicationContext(), "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Method to show the loading dialog
    private void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_loading, null));
        builder.setCancelable(false); // Prevent dismissing dialog by touching outside
        loadingDialog = builder.create();
        loadingDialog.show();
    }

    // Method to hide the loading dialog
    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}