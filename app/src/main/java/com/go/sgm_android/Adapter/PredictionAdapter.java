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
import com.go.sgm_android.model.Prediction;
import com.go.sgm_android.model.Prediction;

import java.util.List;

public class PredictionAdapter extends RecyclerView.Adapter<PredictionAdapter.PredictionViewHolder> {

    private static List<Prediction> powerPlants;

    public PredictionAdapter(List<Prediction> powerPlants) {
        this.powerPlants = powerPlants;
    }

    @NonNull
    @Override
    public PredictionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prediction, parent, false);
        return new PredictionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionViewHolder holder, int position) {
        holder.bind(powerPlants.get(position));
    }

    @Override
    public int getItemCount() {
        return powerPlants.size();
    }

    public void setPowerPlants(List<Prediction> powerPlants) {
        this.powerPlants = powerPlants;
        notifyDataSetChanged(); // Notify RecyclerView that the data has changed
    }

    static class PredictionViewHolder extends RecyclerView.ViewHolder {
        TextView powerPlantName;
        TextView currentCapacity;
        TextView targetCapacity;
        TextView alert;

        public PredictionViewHolder(@NonNull View itemView) {
            super(itemView);
            powerPlantName = itemView.findViewById(R.id.PP_name);
            currentCapacity = itemView.findViewById(R.id.PP_current_capacity);
            targetCapacity = itemView.findViewById(R.id.PP_Max_Output);
            alert = itemView.findViewById(R.id.pp_alert);
        }

        void bind(Prediction powerPlant) {
            powerPlantName.setText(powerPlant.getPPname());
            currentCapacity.setText("Supply : " + powerPlant.getPPtotalCurrentCapacity() + " +/- 10 MW");
            targetCapacity.setText("Target Capacity: " + powerPlant.getPPtargetCapacity() + " +/- 10 MW");

            // Compare totalCapacity and targetCapacity
            float totalCapacityValue = powerPlant.getPPtotalCurrentCapacity();
            float targettargetValue = powerPlant.getPPtargetCapacity();

            if ((targettargetValue-totalCapacityValue)>=20) {
                alert.setText("Alert: This power plant may have failed to meet the target.");
            } else if ((targettargetValue-totalCapacityValue)<=20) {
                alert.setText("Alert: No alert for that day.");
            } else if (targettargetValue==totalCapacityValue){
                alert.setText("Alert: No alert for that day.");
            }
        }

    }
}
