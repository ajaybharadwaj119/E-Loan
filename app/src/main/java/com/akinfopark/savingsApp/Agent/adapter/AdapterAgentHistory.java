package com.akinfopark.savingsApp.Agent.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akinfopark.savingsApp.Utils.CommonFunctions;
import com.akinfopark.savingsApp.Utils.OnItemViewClickListener;
import com.akinfopark.savingsApp.adapter.AdapterTransHistory;
import com.akinfopark.savingsApp.databinding.AdapterAgentHistoryBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterAgentHistory extends RecyclerView.Adapter<AdapterAgentHistory.ViewHolder> {

    Activity activity;
    List<JSONObject> list;
    OnItemViewClickListener onItemViewClickListener;

    public AdapterAgentHistory(Activity activity, List<JSONObject> list, OnItemViewClickListener onItemViewClickListener) {
        this.activity = activity;
        this.list = list;
        this.onItemViewClickListener = onItemViewClickListener;
    }

    @NonNull
    @Override
    public AdapterAgentHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterAgentHistoryBinding binding = AdapterAgentHistoryBinding.inflate(CommonFunctions.getAdapterInflater(parent), parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAgentHistory.ViewHolder holder, int position) {
        JSONObject object = list.get(position);

        try {

            holder.binding.tvAmt.setText("₹ " + object.getString("Amount"));


            String originalDateStr = object.getString("CreatedOn");
            String desiredFormat = "dd-MM-yyyy";

            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat desiredDateFormat = new SimpleDateFormat(desiredFormat);


            Date originalDate = originalFormat.parse(originalDateStr);
            String formattedDate = desiredDateFormat.format(originalDate);
            System.out.println("Original Date: " + originalDateStr);
            System.out.println("Formatted Date: " + formattedDate);

            holder.binding.tvDate.setText(formattedDate);

        } catch (JSONException | ParseException e) {
            throw new RuntimeException(e);
        }

    }


    void newDate() {
        String originalDateStr = "2023-07-30";
        String desiredFormat = "dd-MM-yyyy";

        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat desiredDateFormat = new SimpleDateFormat(desiredFormat);

        try {
            Date originalDate = originalFormat.parse(originalDateStr);
            String formattedDate = desiredDateFormat.format(originalDate);
            System.out.println("Original Date: " + originalDateStr);
            System.out.println("Formatted Date: " + formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static String convertDateFormat(String inputDate, String outputFormat) {
        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("YYYY-MM-DD");
            Date date = inputDateFormat.parse(inputDate);

            SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AdapterAgentHistoryBinding binding;

        public ViewHolder(@NonNull View itemView, AdapterAgentHistoryBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }
}
