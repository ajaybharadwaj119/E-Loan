package com.akinfopark.savingsApp.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akinfopark.savingsApp.Utils.CommonFunctions;
import com.akinfopark.savingsApp.Utils.OnItemViewClickListener;
import com.akinfopark.savingsApp.adapter.customer.AdapterSavings;
import com.akinfopark.savingsApp.adapter.expenses.AdapterExpenses;
import com.akinfopark.savingsApp.databinding.AdapterCustSavingsBinding;
import com.akinfopark.savingsApp.databinding.AdapterTransHistoryBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterTransHistory extends RecyclerView.Adapter<AdapterTransHistory.ViewHolder> {

    Activity activity;
    List<JSONObject> list;
    OnItemViewClickListener onItemViewClickListener;

    public AdapterTransHistory(Activity activity, List<JSONObject> list, OnItemViewClickListener onItemViewClickListener) {
        this.activity = activity;
        this.list = list;
        this.onItemViewClickListener = onItemViewClickListener;
    }

    @NonNull
    @Override
    public AdapterTransHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterTransHistoryBinding binding = AdapterTransHistoryBinding.inflate(CommonFunctions.getAdapterInflater(parent), parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTransHistory.ViewHolder holder, int position) {
        JSONObject object = list.get(position);

        try {

            holder.binding.tvAmt.setText("₹ " + object.getString("Amount"));
            holder.binding.tvCardId.setText(object.getString("EmployeeName"));
          //  holder.binding.tvType.setText(object.getString("Type"));

            String originalDateStr = object.getString("CreatedOn");
            String desiredFormat = "dd-MM-yyyy";

            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat desiredDateFormat = new SimpleDateFormat(desiredFormat);


            Date originalDate = originalFormat.parse(originalDateStr);
            String formattedDate = desiredDateFormat.format(originalDate);
            System.out.println("Original Date: " + originalDateStr);
            System.out.println("Formatted Date: " + formattedDate);

            holder.binding.tvDate.setText(formattedDate);

           /* if (object.getString("Type").equalsIgnoreCase("Savings")) {
                holder.binding.ImgSavings.setVisibility(View.VISIBLE);
                holder.binding.ImgLoan.setVisibility(View.GONE);
            } else {
                holder.binding.ImgSavings.setVisibility(View.GONE);
                holder.binding.ImgLoan.setVisibility(View.VISIBLE);
            }*/

        } catch (JSONException | ParseException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        AdapterTransHistoryBinding binding;

        public ViewHolder(@NonNull View itemView, AdapterTransHistoryBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }
}
