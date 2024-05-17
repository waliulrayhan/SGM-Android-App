package com.go.sgm_android;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.go.sgm_android.databinding.ActivityGraphBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Date;

public class GraphActivity extends AppCompatActivity {

    private ActivityGraphBinding binding;
    private List<String> xValues;
    private List<Entry> entries1;
    private List<Entry> entries2;
    private LineChart chart;
    private float yAxisMaximum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGraphBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Change notification color
        getWindow().setStatusBarColor(getResources().getColor(R.color.glitter_lake));

        // Set the title of the activity
        setTitle("Statistical Overview");

        // Initialize lists
        xValues = new ArrayList<>();
        entries1 = new ArrayList<>();
        entries2 = new ArrayList<>();
        chart = binding.chart;
        yAxisMaximum = 0f;

        // Setup chart
        setupChart();

        // Fetch data from Firebase
        fetchDataFromFirebase();
    }

    private void setupChart() {
        Description description = new Description();
        description.setText("All Data Record");
        description.setPosition(150f, 15f);
        chart.setDescription(description);
        chart.getAxisRight().setDrawLabels(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(4);
        xAxis.setGranularity(1f);
        xAxis.setAvoidFirstLastClipping(true); // Prevent clipping of first and last label

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);

        // Add space to x-axis
        xAxis.setSpaceMin(0f);
        xAxis.setSpaceMax(0.1f);
    }

    private void fetchDataFromFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("SGM/Date");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int index = 0;
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    String date = dateSnapshot.getKey();
                    Map<String, Object> totals = (Map<String, Object>) dateSnapshot.child("total").getValue();

                    if (totals != null) {
                        String formattedDate = formatDate(date);
                        xValues.add(formattedDate);

                        float allddCurrentDemand = Float.parseFloat(totals.get("AllddcurrentDemand").toString());
                        float allppCurrentCapacity = Float.parseFloat(totals.get("AllppcurrentCapacity").toString());

                        entries1.add(new Entry(index, allddCurrentDemand));
                        entries2.add(new Entry(index, allppCurrentCapacity));

                        yAxisMaximum = Math.max(yAxisMaximum, Math.max(allddCurrentDemand, allppCurrentCapacity));

                        index++;
                    }
                }

                // Add padding to yAxisMaximum
                yAxisMaximum += 20;

                updateChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    private String formatDate(String date) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Date parsedDate;
        try {
            parsedDate = originalFormat.parse(date);
            return targetFormat.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }

    private void updateChart() {
        LineDataSet dataSet1 = new LineDataSet(entries1, "Power Plants");
        dataSet1.setColor(Color.BLUE);

        LineDataSet dataSet2 = new LineDataSet(entries2, "Distributor");
        dataSet2.setColor(Color.RED);

        LineData lineData = new LineData(dataSet1, dataSet2);
        chart.setData(lineData);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xValues));

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaximum(yAxisMaximum);

        chart.invalidate();
    }
}
