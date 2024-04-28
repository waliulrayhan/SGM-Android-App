package com.go.sgm_android.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.go.sgm_android.R;
import com.go.sgm_android.model.PowerPlant;

import java.util.List;

public class PowerPlantAdapter extends RecyclerView.Adapter<PowerPlantAdapter.PowerPlantViewHolder> {

    private List<PowerPlant> powerPlants;

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

        public PowerPlantViewHolder(@NonNull View itemView) {
            super(itemView);
            powerPlantName = itemView.findViewById(R.id.power_plant_name);
            currentCapacity = itemView.findViewById(R.id.current_capacity); // Assuming these are the ids in your item layout
            targetCapacity = itemView.findViewById(R.id.target_capacity); // Assuming these are the ids in your item layout
        }

        void bind(PowerPlant powerPlant) {
            powerPlantName.setText(powerPlant.getName());
            currentCapacity.setText(String.valueOf(powerPlant.getCurrentCapacity())); // Convert long to String
            targetCapacity.setText(String.valueOf(powerPlant.getTargetCapacity())); // Convert long to String
        }
    }
}