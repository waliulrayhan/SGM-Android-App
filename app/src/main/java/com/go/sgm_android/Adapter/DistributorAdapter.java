package com.go.sgm_android.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.go.sgm_android.R;

import java.util.List;

public class DistributorAdapter extends RecyclerView.Adapter<DistributorAdapter.DistributorViewHolder> {

    private List<String> distributors;

    public DistributorAdapter(List<String> distributors) {
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

    static class DistributorViewHolder extends RecyclerView.ViewHolder {
        TextView distributorName;

        public DistributorViewHolder(@NonNull View itemView) {
            super(itemView);
            distributorName = itemView.findViewById(R.id.distributor_name);
        }

        void bind(String distributor) {
            distributorName.setText(distributor);
        }
    }
}
