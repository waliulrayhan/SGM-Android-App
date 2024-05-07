package com.go.sgm_android.ui.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.go.sgm_android.Adapter.CommentAdapter;
import com.go.sgm_android.DistributorListActivity;
import com.go.sgm_android.PowerPlantListActivity;
import com.go.sgm_android.R;
import com.go.sgm_android.databinding.FragmentHomeBinding;
import com.go.sgm_android.model.Comment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Handler handler;
    private Runnable updateTimeRunnable;
    private CommentAdapter commentAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        fetchDataFromFirebase();

        //==========================================================================================
        // This is for Time and Date
        // Initialize TextViews for displaying current time and date
        final TextView currentTimeTextView = binding.currentTime;
        final TextView currentDateTextView = binding.currentDate;

        // Initialize Handler for updating time and date every second
        handler = new Handler();
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                // Get current time and date
                long currentTimeMillis = System.currentTimeMillis();
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a", Locale.getDefault());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                String currentTimeString = timeFormat.format(new Date(currentTimeMillis));
                String currentDateString = dateFormat.format(new Date(currentTimeMillis));

                // Update TextViews
                currentTimeTextView.setText("Time: "+currentTimeString);
                currentDateTextView.setText("Date: "+currentDateString);

                // Schedule the next update after 1 second
                handler.postDelayed(this, 1000);
            }
        };

        // Start updating time and date
        handler.post(updateTimeRunnable);

        binding.PP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PowerPlantListActivity.class);
                startActivity(intent);
            }
        });

        binding.DD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DistributorListActivity.class);
                startActivity(intent);
            }
        });

        binding.addCentralCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the dialog layout
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_layout, null);

                // Find views in the dialog layout
                EditText editTextInput = dialogView.findViewById(R.id.editTextInput);
                Button buttonSave = dialogView.findViewById(R.id.buttonSave);
                Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);

                // Create the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                // Set click listener for Save button
                buttonSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get input from EditText
                        String inputData = editTextInput.getText().toString().trim();

                        // Handle saving logic here
                        // For example, you can save data to Firebase or perform other actions

                        if (!inputData.isEmpty()){
                            Toast.makeText(getContext(), "Hello "+inputData, Toast.LENGTH_SHORT).show();
                            uploadCentralCommandToFirebase(inputData);
                        }

                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });

                // Set click listener for Cancel button
                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Dismiss the dialog without saving
                        dialog.dismiss();
                    }
                });

                // Show the dialog
                dialog.show();
            }
        });

        //==========================================================================================
        // RecyclerView for displaying comments
        RecyclerView recyclerView = binding.RVCentralCommand;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        commentAdapter = new CommentAdapter(new ArrayList<>());
        recyclerView.setAdapter(commentAdapter);

        // Fetch comments from Firebase and update RecyclerView
        fetchCommentData();

        return root;
    }

    private void uploadCentralCommandToFirebase(String inputData) {
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

        // Upload the comment to Firebase
        if (commentKey != null) {
            commentRef.child(commentKey).setValue(commentData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Comment uploaded successfully
                            Log.d("UploadCentralCommand", "Comment uploaded successfully");
                            // You can add any additional actions you want to perform on success
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to upload comment
                            Log.e("UploadCentralCommand", "Failed to upload comment: " + e.getMessage());
                            // Handle the error, if needed
                        }
                    });
        }
    }

    private void fetchCommentData() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference()
                .child("SGM").child("Date").child(currentDate).child("comments");

        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Comment> comments = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String commentText = snapshot.child("comment").getValue(String.class);
                    if (commentText != null) {
                        comments.add(new Comment(commentText));
                    }
                }
                commentAdapter.setComments(comments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FetchCommentData", "Failed to fetch comments: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchDataFromFirebase() {
        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Construct the Firebase reference path for the current date
        DatabaseReference ppReference = FirebaseDatabase.getInstance().getReference()
                .child("SGM").child("Date").child(currentDate);
        ppReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve total node
                    DataSnapshot totalsSnapshot = dataSnapshot.child("total");

                    float totalCurrentCapacityValue = totalsSnapshot.child("AllppcurrentCapacity").getValue(float.class);

                    // Update UI with fetched values
                    binding.ppTotalCurrentCapacity.setText("Total Current Capacity\n"+String.valueOf(totalCurrentCapacityValue)+" MW");
                } else {
                    // Handle case when data doesn't exist
                    // You can display a message or take appropriate action
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });

        // Construct the Firebase reference path for the current date
        DatabaseReference ddReference = FirebaseDatabase.getInstance().getReference()
                .child("SGM").child("Date").child(currentDate);
        ppReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve total node
                    DataSnapshot totalsSnapshot = dataSnapshot.child("total");

                    float totalCurrentCapacityValue = totalsSnapshot.child("AllddcurrentDemand").getValue(float.class);

                    // Update UI with fetched values
                    binding.ddTotalCurrentDemand.setText("Total Current Demand\n"+String.valueOf(totalCurrentCapacityValue)+" MW");
                } else {
                    // Handle case when data doesn't exist
                    // You can display a message or take appropriate action
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }
}
