package com.akinfopark.savingsApp.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akinfopark.savingsApp.Utils.CommonFunctions;
import com.akinfopark.savingsApp.Utils.OnItemViewClickListener;
import com.akinfopark.savingsApp.adapter.customer.AdapterSavings;
import com.akinfopark.savingsApp.databinding.AdapterHomeSavingsBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterSavingHome extends RecyclerView.Adapter<AdapterSavingHome.ViewHolder> {

    Activity activity;
    List<JSONObject> list;
    OnItemViewClickListener onItemViewClickListener;


    public AdapterSavingHome(Activity activity, List<JSONObject> list, OnItemViewClickListener onItemViewClickListener) {
        this.activity = activity;
        this.list = list;
        this.onItemViewClickListener = onItemViewClickListener;
    }

    @NonNull
    @Override
    public AdapterSavingHome.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterHomeSavingsBinding binding = AdapterHomeSavingsBinding.inflate(CommonFunctions.getAdapterInflater(parent), parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSavingHome.ViewHolder holder, int position) {
        JSONObject object = list.get(position);

        try {

            Log.i("ObjObdfsdf", object.toString());
            holder.binding.tvAgName.setText( object.getJSONObject("customer").getString("CustomerName"));
            holder.binding.tvTotalSav.setText("₹ " + object.getString("SavingTotal"));
            // holder.binding.tvCreatedOn.setText(convertDateFormat(object.getJSONObject("customer").getString("CustomerJoinedOn")));
            holder.binding.tvNum.setText(object.getJSONObject("customer").getString("CustomerRefID"));
            holder.binding.tvStatus.setText(object.getJSONObject("customer").getString("CustomerStatus"));

            if (object.getJSONObject("customer").getString("CustomerStatus").equalsIgnoreCase("")) {
                holder.binding.tvStatus.setTextColor(Color.parseColor("#FF0000"));
            } else {
                holder.binding.tvStatus.setTextColor(Color.parseColor("#008000"));
            }

        } catch (JSONException e) {

            //Log.i("RuntimeException", e.toString());
        }

        holder.binding.tvCreatedOn.setOnClickListener(v -> {
            try {
                onItemViewClickListener.onClick(v, holder.getAdapterPosition());
            } catch (JSONException e) {

            }
        });

        holder.binding.laySave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onItemViewClickListener.onClick(v, holder.getAdapterPosition());
                } catch (JSONException e) {

                }
            }
        });

    }


    private static String convertDateFormat(String inputDate) {
        String outputDate = "";
        try {
            // Define input and output date formats
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

            // Parse the input date string into a Date object
            Date date = inputFormat.parse(inputDate);

            // Format the Date object into the desired output format
            outputDate = outputFormat.format(date);
        } catch (ParseException e) {

        }
        return outputDate;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AdapterHomeSavingsBinding binding;

        public ViewHolder(@NonNull View itemView, AdapterHomeSavingsBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }
}
