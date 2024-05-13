package com.go.sgm_android.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.go.sgm_android.PowerPlantDetailsActivity;
import com.go.sgm_android.R;
import com.go.sgm_android.model.PowerPlant;

import java.util.List;

public class PowerPlantAdapter extends RecyclerView.Adapter<PowerPlantAdapter.PowerPlantViewHolder> {

    private static List<PowerPlant> powerPlants;

    public PowerPlantAdapter(List<PowerPlant> powerPlants) {
        this.powerPlants = powerPlants;
    }

    @NonNull
    @Override
    public PowerPlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_power_plant, parent, false);
        return new PowerPlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PowerPlantViewHolder holder, int position) {
        holder.bind(powerPlants.get(position));
    }

    @Override
    public int getItemCount() {
        return powerPlants.size();
    }

    public void setPowerPlants(List<PowerPlant> powerPlants) {
        this.powerPlants = powerPlants;
        notifyDataSetChanged(); // Notify RecyclerView that the data has changed
    }

    static class PowerPlantViewHolder extends RecyclerView.ViewHolder {
        TextView powerPlantName;
        TextView currentCapacity;
        TextView targetCapacity;
        TextView totalCapacity;
        TextView alert;

        public PowerPlantViewHolder(@NonNull View itemView) {
            super(itemView);
            powerPlantName = itemView.findViewById(R.id.PP_name);
            currentCapacity = itemView.findViewById(R.id.PP_current_capacity);
            targetCapacity = itemView.findViewById(R.id.PP_Max_Output);
            totalCapacity = itemView.findViewById(R.id.PP_total_current_capacity);
            alert = itemView.findViewById(R.id.pp_alert);

            // Set click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the clicked power plant
                    PowerPlant selectedPowerPlant = powerPlants.get(getAdapterPosition());
                    // Start Details Activity and pass the selected power plant data
                    Intent intent = new Intent(itemView.getContext(), PowerPlantDetailsActivity.class);
                    intent.putExtra("POWER_PLANT_NAME", selectedPowerPlant.getPPname());
                    intent.putExtra("CURRENT_CAPACITY", selectedPowerPlant.getPPcurrentCapacity());
                    intent.putExtra("TARGET_CAPACITY", selectedPowerPlant.getPPtargetCapacity());
                    itemView.getContext().startActivity(intent);
                }
            });
        }

        void bind(PowerPlant powerPlant) {
            powerPlantName.setText(powerPlant.getPPname());
            currentCapacity.setText("Current Capacity: " + powerPlant.getPPcurrentCapacity() + " MW");
            targetCapacity.setText("Target Capacity: " + powerPlant.getPPtargetCapacity() + " MW");
            totalCapacity.setText("Total Current Capacity: " + powerPlant.getPPtotalCurrentCapacity() + " MW");

            // Compare totalCapacity and targetCapacity
            float totalCapacityValue = powerPlant.getPPtotalCurrentCapacity();
            float targetCapacityValue = powerPlant.getPPtargetCapacity();

            if (totalCapacityValue < targetCapacityValue) {
                alert.setText("Alert, failed");
            } else if (totalCapacityValue > targetCapacityValue) {
                alert.setText("Success Ok");
            } else {
                alert.setText("Taget and Total Capacity is Equal");
            }
        }

    }
}
