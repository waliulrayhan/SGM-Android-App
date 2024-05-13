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

public class HistoryPowerPlantAdapter extends RecyclerView.Adapter<HistoryPowerPlantAdapter.HistoryPowerPlantViewHolder> {
    private List<PowerPlant> powerPlants;

    public HistoryPowerPlantAdapter(List<PowerPlant> powerPlants) {
        this.powerPlants = powerPlants;
    }

    public void clear() {
        powerPlants.clear();
        notifyDataSetChanged(); // Notify adapter that the data has changed
    }
    @NonNull
    @Override
    public HistoryPowerPlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_power_plant, parent, false);
        return new HistoryPowerPlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryPowerPlantViewHolder holder, int position) {
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

    static class HistoryPowerPlantViewHolder extends RecyclerView.ViewHolder {
        TextView powerPlantName;
        TextView currentCapacity;
        TextView targetCapacity;
        TextView alert;

        public HistoryPowerPlantViewHolder(@NonNull View itemView) {
            super(itemView);
            powerPlantName = itemView.findViewById(R.id.PP_name);
            currentCapacity = itemView.findViewById(R.id.PP_Supply); // Assuming these are the ids in your item layout
            targetCapacity = itemView.findViewById(R.id.PP_Max_Output); // Assuming these are the ids in your item layout
            alert = itemView.findViewById(R.id.PP_Alert); // Assuming these are the ids in your item layout
        }

        void bind(PowerPlant powerPlant) {
            powerPlantName.setText("Power Plant\n"+powerPlant.getPPname());
            currentCapacity.setText("Supply: " + powerPlant.getPPcurrentCapacity() + " MW"); // Convert long to String
            targetCapacity.setText("Target: " + powerPlant.getPPtargetCapacity() + " MW"); // Convert long to String

            // Compare totalCapacity and targetCapacity
            float totalValue = powerPlant.getPPcurrentCapacity();
            float targetValue = powerPlant.getPPtargetCapacity();

            if (totalValue < targetValue) {
                alert.setText("Alert, failed");
            } else if (totalValue > targetValue) {
                alert.setText("Success Ok");
            } else {
                alert.setText("Taget and Total Capacity is Equal");
            }
        }
    }
}