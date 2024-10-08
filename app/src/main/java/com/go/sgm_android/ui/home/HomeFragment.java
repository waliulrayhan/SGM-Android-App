package com.go.sgm_android.ui.home;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.go.sgm_android.Adapter.CommentAdapter;
import com.go.sgm_android.AddCommentActivity;
import com.go.sgm_android.DistributorListActivity;
import com.go.sgm_android.GridDetailsActivity;
import com.go.sgm_android.NetworkUtil;
import com.go.sgm_android.PowerPlantListActivity;
import com.go.sgm_android.R;
import com.go.sgm_android.SplashActivity;
import com.go.sgm_android.databinding.FragmentHomeBinding;
import com.go.sgm_android.model.Comment;
import com.go.sgm_android.model.PowerPlant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
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
    private AlertDialog loadingDialog; // Reference to the loading dialog

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (NetworkUtil.isConnected(getContext())) {
            showLoadingDialog();

            try {
                RecyclerView recyclerView = binding.RVCentralCommand;
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                commentAdapter = new CommentAdapter(new ArrayList<>());
                recyclerView.setAdapter(commentAdapter);

                ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    private Drawable deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.delete_forever_24dp_fill0);
                    private ColorDrawable background = new ColorDrawable(Color.RED);

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        String commentKey = commentAdapter.getCommentKey(position);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        String currentDate = dateFormat.format(new Date());
                        DatabaseReference commentRef = FirebaseDatabase.getInstance()
                                .getReference()
                                .child("SGM")
                                .child("Date")
                                .child(currentDate)
                                .child("comments")
                                .child(commentKey);

                        commentRef.removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                commentAdapter.removeItem(position);
                                Toast.makeText(getContext(), "Comment deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to delete comment", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                        View itemView = viewHolder.itemView;
                        int itemHeight = itemView.getHeight();

                        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                        background.draw(c);

                        int deleteIconMargin = (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
                        int deleteIconTop = itemView.getTop() + (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
                        int deleteIconBottom = deleteIconTop + deleteIcon.getIntrinsicHeight();
                        int deleteIconLeft = itemView.getRight() - deleteIconMargin - deleteIcon.getIntrinsicWidth();
                        int deleteIconRight = itemView.getRight() - deleteIconMargin;

                        deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
                        deleteIcon.draw(c);
                    }
                };
                new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

                final TextView currentTimeTextView = binding.currentTime;
                final TextView currentDateTextView = binding.currentDate;

                handler = new Handler();
                updateTimeRunnable = new Runnable() {
                    @Override
                    public void run() {
                        long currentTimeMillis = System.currentTimeMillis();
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a", Locale.getDefault());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                        String currentTimeString = timeFormat.format(new Date(currentTimeMillis));
                        String currentDateString = dateFormat.format(new Date(currentTimeMillis));

                        currentTimeTextView.setText("Time: " + currentTimeString);
                        currentDateTextView.setText("Date: " + currentDateString);

                        handler.postDelayed(this, 1000);
                    }
                };

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

                binding.GridDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), GridDetailsActivity.class);
                        startActivity(intent);
                    }
                });

                binding.addCentralCommand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getContext(), AddCommentActivity.class);
                        startActivity(intent);

//                        // Inflate the dialog layout
//                        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_layout, null);
//
//                        // Find views in the dialog layout
//                        EditText editTextInput = dialogView.findViewById(R.id.editTextInput);
//                        Button buttonSave = dialogView.findViewById(R.id.buttonSave);
//                        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
//
//                        // Create the dialog
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                        builder.setView(dialogView);
//                        AlertDialog dialog = builder.create();
//
//                        // Set click listener for Save button
//                        buttonSave.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                // Get input from EditText
//                                String inputData = editTextInput.getText().toString().trim();
//
//                                // Handle saving logic here
//                                // For example, you can save data to Firebase or perform other actions
//
//                                if (inputData.isEmpty()){
//                                    Toast.makeText(getContext(), "Please, Fill the Comment Field.", Toast.LENGTH_SHORT).show();
//                                }
//
//                                if (!inputData.isEmpty()) {
//                                    Toast.makeText(getContext(), "Hello " + inputData, Toast.LENGTH_SHORT).show();
//                                    uploadCentralCommandToFirebase(inputData);
//                                    showLoadingDialog();
//                                }
//
//                                // Dismiss the dialog
//                                dialog.dismiss();
//                            }
//                        });
//
//                        // Set click listener for Cancel button
//                        buttonCancel.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                // Dismiss the dialog without saving
//                                dialog.dismiss();
//                            }
//                        });
//
//                        // Show the dialog
//                        dialog.show();
                    }
                });

            } catch (Exception e) {
                Toast.makeText(getContext(), "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            showNoInternetDialog();
        }

        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fetchCommentData();  // Fetch comments first
        fetchDataFromFirebase();  // Then fetch other data
    }

    // Method to show the loading dialog
    private void showLoadingDialog() {
        if (isAdded() && !isDetached() && !isRemoving() && loadingDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setView(LayoutInflater.from(requireContext()).inflate(R.layout.dialog_loading, null));
            builder.setCancelable(false); // Prevent dismissing dialog by touching outside
            loadingDialog = builder.create();
            loadingDialog.show();
        }
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }


//    private void uploadCentralCommandToFirebase(String inputData) {
//
//        try {
//            // Get the current date
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
//            String currentDate = dateFormat.format(new Date());
//
//            // Reference to "SGM/Date/currentDate/comments"
//            DatabaseReference commentRef = FirebaseDatabase.getInstance()
//                    .getReference()
//                    .child("SGM")
//                    .child("Date")
//                    .child(currentDate)
//                    .child("comments");
//
//            // Push the comment to generate a unique key
//            String commentKey = commentRef.push().getKey();
//
//            // Create a map to hold the comment data
//            Map<String, Object> commentData = new HashMap<>();
//            commentData.put("comment", inputData);
//
//            // Upload the comment to Firebase using a transaction
//            if (commentKey != null) {
//                commentRef.child(commentKey).runTransaction(new Transaction.Handler() {
//                    @NonNull
//                    @Override
//                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
//                        // Set the comment data
//                        mutableData.setValue(commentData);
//                        return Transaction.success(mutableData);
//                    }
//
//                    @Override
//                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
//                        if (databaseError == null) {
//                            // Comment uploaded successfully
//                            Log.d("UploadCentralCommand", "Comment uploaded successfully");
//                            // You can add any additional actions you want to perform on success
//                            hideLoadingDialog();
//                        } else {
//                            // Failed to upload comment
//                            Log.e("UploadCentralCommand", "Failed to upload comment: " + databaseError.getMessage());
//                            // Handle the error, if needed
//                            hideLoadingDialog();
//                        }
//                    }
//                });
//            }
//        } catch (Exception e) {
//            // Example: Displaying a toast message to the user
//            Toast.makeText(getContext(), "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }

    private void fetchCommentData() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String currentDate = dateFormat.format(new Date());

            DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference()
                    .child("SGM").child("Date").child(currentDate).child("comments");

            showLoadingDialog(); // Show loading dialog before starting the data fetch

            commentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Comment> comments = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String commentKey = snapshot.getKey();
                        String commentText = snapshot.child("comment").getValue(String.class);
                        if (commentText != null && commentKey != null) {
                            comments.add(0, new Comment(commentKey, commentText));
                        }
                    }
                    if (isAdded() && !isDetached()) {
                        getActivity().runOnUiThread(() -> {
                            commentAdapter.setComments(comments);
                            hideLoadingDialog(); // Hide loading dialog after data is set
                        });
                    } else {
                        hideLoadingDialog(); // Ensure the dialog is hidden even if the fragment is not in a valid state
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("FetchCommentData", "Failed to fetch comments: " + databaseError.getMessage());
                    hideLoadingDialog(); // Hide loading dialog in case of failure
                }
            });
        } catch (Exception e) {
            Log.e("FetchCommentData", "Exception: " + e.getMessage());
            if (isAdded() && !isDetached()) {
                Toast.makeText(getContext(), "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            hideLoadingDialog(); // Ensure the dialog is hidden in case of exception
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchDataFromFirebase() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("SGM").child("Date").child(currentDate);

        showLoadingDialog(); // Show loading dialog before starting the data fetch

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot totalsSnapshot = dataSnapshot.child("total");

                    Float totalCurrentCapacityValue = totalsSnapshot.child("AllppcurrentCapacity").getValue(Float.class);
                    Float totalCurrentDemandValue = totalsSnapshot.child("AllddcurrentDemand").getValue(Float.class);

                    if (totalCurrentCapacityValue != null && totalCurrentDemandValue != null) {
                        float frequency = (totalCurrentCapacityValue / totalCurrentDemandValue) * 50;

                        if (isAdded() && !isDetached()) {
                            getActivity().runOnUiThread(() -> {
                                if (totalCurrentCapacityValue < totalCurrentDemandValue) {
                                    binding.ppTotalCurrentCapacity.setText("Total Current Capacity\n" + totalCurrentCapacityValue + " MW");
                                    binding.ddTotalCurrentDemand.setText("Total Current Demand\n" + totalCurrentDemandValue + " MW");
                                    binding.frequency.setText("Current Frequency\n" + frequency + " Hz");
                                } else if (totalCurrentCapacityValue > totalCurrentDemandValue) {
                                    binding.ppTotalCurrentCapacity.setText("Total Current Capacity\n" + totalCurrentDemandValue + " MW");
                                    binding.ddTotalCurrentDemand.setText("Total Current Demand\n" + totalCurrentDemandValue + " MW");
                                    binding.frequency.setText("Current Frequency\n50 Hz");
                                } else {
                                    binding.ppTotalCurrentCapacity.setText("Total Current Capacity\n" + totalCurrentCapacityValue + " MW");
                                    binding.ddTotalCurrentDemand.setText("Total Current Demand\n" + totalCurrentDemandValue + " MW");
                                    binding.frequency.setText("Current Frequency\n50 Hz");
                                }

                                hideLoadingDialog(); // Hide loading dialog after data is set
                            });
                        } else {
                            hideLoadingDialog(); // Ensure the dialog is hidden even if the fragment is not in a valid state
                        }
                    } else {
                        Log.e("FetchDataFromFirebase", "Total current capacity or demand value is null");
                        hideLoadingDialog(); // Hide loading dialog in case of error
                    }
                } else {
                    Log.e("FetchDataFromFirebase", "DataSnapshot does not exist for current date");
                    hideLoadingDialog(); // Hide loading dialog in case of error
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FetchDataFromFirebase", "Failed to fetch data: " + databaseError.getMessage());
                hideLoadingDialog(); // Hide loading dialog in case of failure
            }
        });

        DatabaseReference powerPlantRef = FirebaseDatabase.getInstance().getReference().child("SGM").child("PowerPlant");

        powerPlantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder alertMessageBuilder = new StringBuilder();
                boolean isFirstAlert = true;
                boolean hasAlerts = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String ppName = snapshot.child("ppname").getValue(String.class);
                    DataSnapshot dateSnapshot = snapshot.child("Date").child(currentDate);
                    if (ppName != null && dateSnapshot.exists() && dateSnapshot.child("alert").exists()) {
                        Boolean alertValue = dateSnapshot.child("alert").getValue(Boolean.class);
                        if (alertValue != null && alertValue) {
                            hasAlerts = true;
                            if (!isFirstAlert) {
                                alertMessageBuilder.append(", ");
                            } else {
                                isFirstAlert = false;
                            }
                            alertMessageBuilder.append("Alert: ").append(ppName).append(" may have failed to meet the target");
                        }
                    }
                }

                String alertMessage = alertMessageBuilder.toString();
                final boolean finalHasAlerts = hasAlerts;
                if (isAdded() && !isDetached()) {
                    getActivity().runOnUiThread(() -> {
                        if (finalHasAlerts) {
                            binding.marquee.setSelected(true);
                            binding.marquee.setText(alertMessage);
                            binding.marquee.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                            binding.marquee.setCompoundDrawablesWithIntrinsicBounds(R.drawable.dangerous_24dp_fill0, 0, 0, 0);
//                            binding.marquee.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
                            binding.marquee.setVisibility(View.VISIBLE);
                        } else {
                            binding.marquee.setSelected(true);
                            binding.marquee.setText("Success: All power plant meet the target.               Success: All power plant meet the target.");
                            binding.marquee.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                            binding.marquee.setCompoundDrawablesWithIntrinsicBounds(R.drawable.new_releases_24dp_fill0, 0, 0, 0);
//                            binding.marquee.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
                            binding.marquee.setVisibility(View.VISIBLE);
                        }

                        hideLoadingDialog(); // Hide loading dialog after data is set
                    });
                } else {
                    hideLoadingDialog(); // Ensure the dialog is hidden even if the fragment is not in a valid state
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PowerPlantListActivity", "Failed to fetch power plant data from powerPlantRef: " + databaseError.getMessage());
                hideLoadingDialog(); // Hide loading dialog in case of failure
            }
        });
    }

    private void showNoInternetDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requireActivity().recreate(); // Restart the activity to check connection again
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requireActivity().finish(); // Close the app
                    }
                })
                .setCancelable(false)
                .show();
    }

}
