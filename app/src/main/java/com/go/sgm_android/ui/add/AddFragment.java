package com.go.sgm_android.ui.add;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.go.sgm_android.AddDistributorActivity;
import com.go.sgm_android.AddPowerPlantActivity;
import com.go.sgm_android.MainActivity;
import com.go.sgm_android.R;
import com.go.sgm_android.SetTargetActivity;
import com.go.sgm_android.databinding.FragmentAddBinding;
import com.go.sgm_android.databinding.FragmentSlideshowBinding;
import com.go.sgm_android.ui.slideshow.SlideshowViewModel;

public class AddFragment extends Fragment {

    private FragmentAddBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        try {
            binding.loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //                Toast.makeText(MainActivity.this, "Hello, Add Power Plant", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), AddPowerPlantActivity.class);
                    // Start SecondActivity
                    startActivity(intent);
                }
            });

            binding.loginButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Hello, Add Distributor", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), AddDistributorActivity.class);
                    // Start SecondActivity
                    startActivity(intent);
                }
            });

            binding.loginButton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                //                Toast.makeText(MainActivity.this, "Hello, Add Power Plant", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), SetTargetActivity.class);
                    // Start SecondActivity
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            // Example: Displaying a toast message to the user
            Toast.makeText(getContext(), "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}