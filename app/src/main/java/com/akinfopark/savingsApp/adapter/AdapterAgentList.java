package com.akinfopark.savingsApp.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akinfopark.savingsApp.Utils.CommonFunctions;
import com.akinfopark.savingsApp.Utils.DataFormats;
import com.akinfopark.savingsApp.Utils.OnItemViewClickListener;
import com.akinfopark.savingsApp.databinding.AdapterAgentListBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterAgentList extends RecyclerView.Adapter<AdapterAgentList.ViewHolder> {

    Activity activity;
    List<JSONObject> list;
    OnItemViewClickListener onItemViewClickListener;


    public AdapterAgentList(Activity activity, List<JSONObject> list, OnItemViewClickListener onItemViewClickListener) {
        this.activity = activity;
        this.list = list;
        this.onItemViewClickListener = onItemViewClickListener;
    }

    @NonNull
    @Override
    public AdapterAgentList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterAgentListBinding binding = AdapterAgentListBinding.inflate(CommonFunctions.getAdapterInflater(parent), parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAgentList.ViewHolder holder, int position) {
        JSONObject object = list.get(position);

        try {
            holder.binding.tvAgName.setText(object.getString("EmployeeSlNo") + ") " + object.getString("EmployeeName"));
            holder.binding.tvNum.setText(object.getString("EmployeePhone"));

            holder.binding.tvStatus.setText(object.getString("EmployeeStatus"));

            if (object.getString("EmployeeStatus").equalsIgnoreCase("Approved")) {
                holder.binding.tvStatus.setTextColor(Color.parseColor("#28B463"));
            } else {
                holder.binding.tvStatus.setTextColor(Color.parseColor("#E74C3C"));
            }


            String input = object.getString("EmployeeCreatedOn");
           /* SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a");
            Date date = inputFormat.parse(input);
            String output = outputFormat.format(date);*/

            holder.binding.tvCreatedOn.setText(DataFormats.convertDateFormat(input, "dd/MM/yyyy", "yyyy-MM-dd HH:mm:ss"));

        } catch (JSONException e) {

        }

        holder.binding.layAgent.setOnClickListener(v -> {
            try {
                onItemViewClickListener.onClick(v, holder.getAdapterPosition());
            } catch (JSONException e) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AdapterAgentListBinding binding;

        public ViewHolder(@NonNull View itemView, AdapterAgentListBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }
}
