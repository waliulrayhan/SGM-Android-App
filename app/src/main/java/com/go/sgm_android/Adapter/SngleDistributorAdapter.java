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
public class SngleDistributorAdapter extends RecyclerView.Adapter<SngleDistributorAdapter.SngleDistributorViewHolder> {

    private List<Distributor> distributors;

    public SngleDistributorAdapter(List<Distributor> distributors) {
        this.distributors = distributors;
    }

    public void clear() {
        distributors.clear();
        notifyDataSetChanged(); // Notify adapter that the data has changed
    }

    @NonNull
    @Override
    public SngleDistributorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single_distributor, parent, false);
        return new SngleDistributorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SngleDistributorViewHolder holder, int position) {
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
    static class SngleDistributorViewHolder extends RecyclerView.ViewHolder {
        TextView Name;
        TextView Zone;
        TextView Circle;

        public SngleDistributorViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.dd_area_name);
            Zone = itemView.findViewById(R.id.dd_Zone);
            Circle = itemView.findViewById(R.id.dd_Circle);
        }

        void bind(Distributor distributor) {
            Name.setText(distributor.getDDname());
            Zone.setText(distributor.getDDzone()); // Convert long to String
            Circle.setText(distributor.getDDcircle()); // Convert long to String
        }
    }
}
