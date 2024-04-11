package com.akinfopark.savingsApp.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akinfopark.savingsApp.Utils.CommonFunctions;
import com.akinfopark.savingsApp.Utils.OnItemViewClickListener;
import com.akinfopark.savingsApp.adapter.customer.AdapterSavings;
import com.akinfopark.savingsApp.databinding.AdapterHomeLoanBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterLonasHome extends RecyclerView.Adapter<AdapterLonasHome.ViewHolder> {

    Activity activity;
    List<JSONObject> list;
    OnItemViewClickListener onItemViewClickListener;


    public AdapterLonasHome(Activity activity, List<JSONObject> list, OnItemViewClickListener onItemViewClickListener) {
        this.activity = activity;
        this.list = list;
        this.onItemViewClickListener = onItemViewClickListener;
    }

    @NonNull
    @Override
    public AdapterLonasHome.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterHomeLoanBinding binding = AdapterHomeLoanBinding.inflate(CommonFunctions.getAdapterInflater(parent), parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLonasHome.ViewHolder holder, int position) {
        JSONObject object = list.get(position);

        try {

            holder.binding.tvAgName.setText( /*object.getString("SlNo") +*/ "    " + object.getJSONObject("customer").getString("CustomerName"));
            holder.binding.tvTotalSav.setText("₹ " + object.getString("LoanTotal"));
            holder.binding.tvCreatedOn.setText(convertDateFormat(object.getString("LoanStartDate") ));
            holder.binding.tvNum.setText(object.getJSONObject("customer").getString("CustomerRefID"));
            holder.binding.tvStatus.setText(object.getJSONObject("customer").getString("CustomerStatus"));

            if (!object.getJSONObject("customer").getString("CustomerStatus").equalsIgnoreCase("Open")) {
                holder.binding.tvStatus.setTextColor(Color.parseColor("#008000"));
            } else {
                holder.binding.tvStatus.setTextColor(Color.parseColor("#FF0000"));
            }


        } catch (JSONException e) {

        }

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
        AdapterHomeLoanBinding binding;

        public ViewHolder(@NonNull View itemView, AdapterHomeLoanBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }
}
