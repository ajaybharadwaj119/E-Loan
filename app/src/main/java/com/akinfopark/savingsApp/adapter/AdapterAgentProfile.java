package com.akinfopark.savingsApp.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akinfopark.savingsApp.Utils.CommonFunctions;
import com.akinfopark.savingsApp.Utils.OnItemViewClickListener;
import com.akinfopark.savingsApp.databinding.AdapterAgentProfileBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterAgentProfile extends RecyclerView.Adapter<AdapterAgentProfile.ViewHolder> {

    Activity activity;
    List<JSONObject> list;
    OnItemViewClickListener onItemViewClickListener;

    public AdapterAgentProfile(Activity activity, List<JSONObject> list, OnItemViewClickListener onItemViewClickListener) {
        this.activity = activity;
        this.list = list;
        this.onItemViewClickListener = onItemViewClickListener;
    }

    @NonNull
    @Override
    public AdapterAgentProfile.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterAgentProfileBinding binding = AdapterAgentProfileBinding.inflate(CommonFunctions.getAdapterInflater(parent), parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAgentProfile.ViewHolder holder, int position) {
        JSONObject object = list.get(position);

        try {

            int a = position+1;

            holder.binding.tvNo.setText(object.getString("CustomerRefID"));
            holder.binding.tvCustName.setText(a + ") " + object.getString("CustomerName"));
            holder.binding.tvAmt.setText("₹ " + object.getString("SavingAmount"));
            holder.binding.tvDate.setText(convertDateFormat(object.getString("SavingDate")));

            holder.binding.tvStatus.setText(object.getString("SavingStatus"));
            holder.binding.tvStatus.setTextColor(Color.parseColor("#28B463"));


        } catch (JSONException e) {

        }

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
        AdapterAgentProfileBinding binding;

        public ViewHolder(@NonNull View itemView, AdapterAgentProfileBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }
}
