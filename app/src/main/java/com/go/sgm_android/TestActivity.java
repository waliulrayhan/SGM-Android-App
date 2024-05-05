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
    }
}