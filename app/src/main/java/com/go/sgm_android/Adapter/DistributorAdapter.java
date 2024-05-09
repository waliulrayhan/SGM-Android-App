package com.go.sgm_android.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.go.sgm_android.DistributorDetailsActivity;
import com.go.sgm_android.R;
import com.go.sgm_android.model.Distributor;

import java.util.List;

public class DistributorAdapter extends RecyclerView.Adapter<DistributorAdapter.DistributorViewHolder> {

    private static List<Distributor> distributors;

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

            // Set click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the clicked distributor
                    Distributor selectedDistributor = distributors.get(getAdapterPosition());
                    // Start Details Activity and pass the selected distributor data
                    Intent intent = new Intent(itemView.getContext(), DistributorDetailsActivity.class);
                    intent.putExtra("DISTRIBUTOR_NAME", selectedDistributor.getDDname());
                    intent.putExtra("CURRENT_DEMAND", selectedDistributor.getDDcurrentDemand());
                    intent.putExtra("TARGET_DEMAND", selectedDistributor.getDDtargetDemand());
                    itemView.getContext().startActivity(intent);
                }
            });
        }

        void bind(Distributor distributor) {
            distributorName.setText(distributor.getDDname());
            currentDemand.setText("Current Demand: " + distributor.getDDcurrentDemand() + " MW");
            targetDemand.setText("Target Demand: " + distributor.getDDtargetDemand() + " MW");
        }
    }
}
