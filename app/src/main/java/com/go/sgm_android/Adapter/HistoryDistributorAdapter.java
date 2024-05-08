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

public class HistoryDistributorAdapter extends RecyclerView.Adapter<HistoryDistributorAdapter.HistoryDistributorViewHolder> {

    private List<Distributor> distributors;

    public HistoryDistributorAdapter(List<Distributor> distributors) {
        this.distributors = distributors;
    }

    public void clear() {
        distributors.clear();
        notifyDataSetChanged(); // Notify adapter that the data has changed
    }

    @NonNull
    @Override
    public HistoryDistributorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_distributor, parent, false);
        return new HistoryDistributorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryDistributorViewHolder holder, int position) {
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
    static class HistoryDistributorViewHolder extends RecyclerView.ViewHolder {
        TextView distributorName;
        TextView currentDemand;
        TextView targetDemand;

        public HistoryDistributorViewHolder(@NonNull View itemView) {
            super(itemView);
            distributorName = itemView.findViewById(R.id.DD_name);
            currentDemand = itemView.findViewById(R.id.DD_total_current_demand);
            targetDemand = itemView.findViewById(R.id.DD_target_demand);
        }

        void bind(Distributor distributor) {
            distributorName.setText("Distributor\n"+distributor.getDDname());
            currentDemand.setText("Supply: " + distributor.getDDcurrentDemand() + " MW"); // Convert long to String
            targetDemand.setText("Target: " + distributor.getDDtargetDemand() + " MW"); // Convert long to String
        }
    }
}
