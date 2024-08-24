package com.go.sgm_android.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.go.sgm_android.R;
import com.go.sgm_android.model.Grid;

import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridViewHolder> {

    private static List<Grid> grids;

    public GridAdapter(List<Grid> grids) {
        this.grids = grids;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        holder.bind(grids.get(position));
    }

    @Override
    public int getItemCount() {
        return grids.size();
    }

    public void setGrids(List<Grid> grids) {
        this.grids = grids;
        notifyDataSetChanged(); // Notify RecyclerView that the data has changed
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {
        TextView gridName;
        TextView gridNID;
        TextView gridPhone;
        TextView gridTotalCurrentSupply;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            gridName = itemView.findViewById(R.id.PP_name);
            gridNID = itemView.findViewById(R.id.grid_nid);
            gridPhone = itemView.findViewById(R.id.grid_phone);
            gridTotalCurrentSupply = itemView.findViewById(R.id.grid_total_current_supply);
        }

        void bind(Grid grid) {
            gridName.setText(grid.getName());
            gridNID.setText("NID: " + grid.getNID());
            gridPhone.setText("Phone: " + grid.getPhone());
            gridTotalCurrentSupply.setText("Total Current Supply: " + grid.getGridtotalCurrentSupply() + " MW");
        }
    }
}
