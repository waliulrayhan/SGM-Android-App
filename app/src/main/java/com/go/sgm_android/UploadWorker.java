package com.go.sgm_android;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UploadWorker extends Worker {

    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Execute the data upload operation here
        try {
            uploadDataToFirebase(getApplicationContext());
            return Result.success();
        } catch (Exception e) {
            return Result.failure();
        }
    }

    public static void uploadDataToFirebase(Context context) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("SGM");

        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        String currentDate = dateFormat.format(new Date());

        // Upload PowerPlant data
        DatabaseReference powerPlantRef = databaseRef.child("PowerPlant");

        powerPlantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String powerPlantKey = snapshot.getKey();
                    DatabaseReference powerPlantDateRef = snapshot.child("Date").child(currentDate).getRef();
                    powerPlantDateRef.child("capacity").child("ppcurrentCapacity").setValue(0);
                    powerPlantDateRef.child("capacity").child("pptargetCapacity").setValue(0);
                    powerPlantDateRef.child("total").child("pptotalCurrentCapacity").setValue(0);
                    powerPlantDateRef.child("alert").setValue("false");
                    powerPlantDateRef.child("history").child("pptotalCurrentCapacity").setValue(0);
                    powerPlantDateRef.child("history").child("last_update_time").setValue("11.59.59 PM");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UploadDataToFirebase", "Failed to upload power plant data: " + databaseError.getMessage());
            }
        });



        // Upload Distributor Data
        DatabaseReference distributorRef = databaseRef.child("Distributor");
        distributorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot distributorSnapshot : dataSnapshot.getChildren()) {
                    DatabaseReference distributorDateRef = distributorSnapshot.child("Date").child(currentDate).getRef();
                    distributorDateRef.child("demand").child("ddcurrentDemand").setValue(0);
                    distributorDateRef.child("demand").child("ddtargetdemand").setValue(0);
                    distributorDateRef.child("total").child("ddtotalCurrentdemand").setValue(0);
                    distributorDateRef.child("alert").setValue("false");
                    distributorDateRef.child("history").child("ddtotalCurrentDemand").setValue(0);
                    distributorDateRef.child("history").child("last_update_time").setValue("11.59.59 PM");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UploadDataToFirebase", "Failed to upload distributor data: " + databaseError.getMessage());
            }
        });

        // Upload Date data
        DatabaseReference dateRef = databaseRef.child("Date").child(currentDate).child("total");
        dateRef.child("AllppcurrentCapacity").setValue(0);
        dateRef.child("AllpptargetCapacity").setValue(0);
        dateRef.child("AllddcurrentDemand").setValue(0);
        dateRef.child("AllddtargetDemand").setValue(0);
    }
}