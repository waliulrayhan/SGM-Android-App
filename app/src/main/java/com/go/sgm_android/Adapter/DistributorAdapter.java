package com.go.sgm_android.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.go.sgm_android.R;
import com.go.sgm_android.model.Distributor;

import java.util.List;

public class DistributorAdapter extends RecyclerView.Adapter<DistributorAdapter.DistributorViewHolder> {

    private List<Distributor> distributors;

    public DistributorAdapter(List<Distributor> distributors) {
        this.distributors = distributors;
    }

    @NonNull
    @Override
    public DistributorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_distributor, parent, false);
        return new DistributorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DistributorViewHolder holder, int position) {
        holder.bind(distributors.get(position));
    }

    @Override
    public int getItemCount() {
        return distributors.size();
    }

    public void setDistributors(List<Distributor> distributors) {
        this.distributors = distributors;
        notifyDataSetChanged(); // Notify RecyclerView that the data has changed
    }
    static class DistributorViewHolder extends RecyclerView.ViewHolder {
        TextView distributorName;
        TextView currentDemand;
        TextView targetDemand;

        public DistributorViewHolder(@NonNull View itemView) {
            super(itemView);
            distributorName = itemView.findViewById(R.id.DD_name);
            currentDemand = itemView.findViewById(R.id.DD_total_current_demand);
            targetDemand = itemView.findViewById(R.id.DD_target_demand);
        }

        void bind(Distributor distributor) {
            distributorName.setText(distributor.getDDname());
            currentDemand.setText("Current Demand: "+distributor.getDDcurrentDemand()+" MW"); // Convert long to String
            targetDemand.setText("Target Demand: "+distributor.getDDtargetDemand()+" MW"); // Convert long to String
        }
    }
}
