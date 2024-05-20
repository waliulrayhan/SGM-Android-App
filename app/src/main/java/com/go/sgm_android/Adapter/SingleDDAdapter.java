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


public class SingleDDAdapter extends RecyclerView.Adapter<SingleDDAdapter.SingleDDViewHolder> {

    private static List<Distributor> distributors;

    public SingleDDAdapter(List<Distributor> distributors) {
        this.distributors = distributors;
    }

    @NonNull
    @Override
    public SingleDDViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single_distributor, parent, false);
        return new SingleDDViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleDDViewHolder holder, int position) {
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

    static class SingleDDViewHolder extends RecyclerView.ViewHolder {
        TextView Name;
        TextView Zone;
        TextView Circle;

        public SingleDDViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.dd_area_name);
            Zone = itemView.findViewById(R.id.dd_Zone);
            Circle = itemView.findViewById(R.id.dd_Circle);

        }

        void bind(Distributor distributor) {
            Name.setText(distributor.getDDname());
            Zone.setText(distributor.getDDzone());
            Circle.setText(distributor.getDDcircle());
        }
    }
}

