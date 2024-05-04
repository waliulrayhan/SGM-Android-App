package com.go.sgm_android;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.go.sgm_android.databinding.ActivityAddDistributorBinding;
import com.go.sgm_android.databinding.ActivityTestBinding;
import com.google.android.material.textfield.TextInputLayout;

public class TestActivity extends AppCompatActivity {
    private ActivityTestBinding binding;
    private TextInputLayout dropdownMenuDistrict;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Change notification color
        getWindow().setStatusBarColor(getResources().getColor(R.color.glitter_lake));

        // Set the title of the activity
        setTitle("Test Activity");

        // Change toolbar color
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.glitter_lake)));
            // Change toolbar text color
//            getSupportActionBar().setTitleTextColor(getResources().getColor(android.R.color.white));
//            getSupportActionBar().set
        }

        dropdownMenuDistrict = findViewById(R.id.dropdown_menu_district);

        // Set up the AutoCompleteTextView
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) dropdownMenuDistrict.getEditText();
        if (autoCompleteTextView != null) {
            String[] items = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items);
            autoCompleteTextView.setAdapter(adapter);
        }
    }
}