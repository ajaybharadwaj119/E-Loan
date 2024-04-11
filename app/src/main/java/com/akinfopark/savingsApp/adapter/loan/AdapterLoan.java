package com.akinfopark.savingsApp.adapter.loan;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akinfopark.savingsApp.Utils.OnItemViewClickListener;
import com.akinfopark.savingsApp.adapter.customer.AdapterSavings;

import org.json.JSONObject;

import java.util.List;

public class AdapterLoan extends RecyclerView.Adapter<AdapterLoan.ViewHolder> {
    Activity activity;
    List<JSONObject> list;
    OnItemViewClickListener onItemViewClickListener;


    public AdapterLoan(Activity activity, List<JSONObject> list, OnItemViewClickListener onItemViewClickListener) {
        this.activity = activity;
        this.list = list;
        this.onItemViewClickListener = onItemViewClickListener;
    }

    @NonNull
    @Override
    public AdapterLoan.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLoan.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
