package com.akinfopark.savingsApp.adapter.customer;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akinfopark.savingsApp.Utils.CommonFunctions;
import com.akinfopark.savingsApp.Utils.OnItemViewClickListener;
import com.akinfopark.savingsApp.databinding.AdaperCustLoanBinding;
import com.akinfopark.savingsApp.databinding.AdapterCustSavingsBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdapterLoans extends RecyclerView.Adapter<AdapterLoans.ViewHolder> {

    Activity activity;
    List<JSONObject> list;
    OnItemViewClickListener onItemViewClickListener;


    public AdapterLoans(Activity activity, List<JSONObject> list, OnItemViewClickListener onItemViewClickListener) {
        this.activity = activity;
        this.list = list;
        this.onItemViewClickListener = onItemViewClickListener;
    }

    @NonNull
    @Override
    public AdapterLoans.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdaperCustLoanBinding binding = AdaperCustLoanBinding.inflate(CommonFunctions.getAdapterInflater(parent), parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLoans.ViewHolder holder, int position) {
        JSONObject object = list.get(position);

        try {
            holder.binding.tvRef.setText(object.getString("SlNo"));
            holder.binding.tvIntAmt.setText("₹ " + object.getString("LoanInterestAmount"));
            holder.binding.tvLnAmt.setText("₹ " + object.getString("LoanAmount"));

            //holder.binding.tvCrDate.setText("Created On : " + convertDateFormat(object.getString("LoanStartDate")));
            holder.binding.tvStDate.setText("From: " + convertDateFormat(object.getString("LoanStartDate")));
            holder.binding.tvEnDate.setText("To: " + convertDateFormat(object.getString("LoanEnddate")));
            holder.binding.tvStatus.setText(object.getString("LoanStatus"));
            holder.binding.tvCurInt.setText("₹ " + object.getString("LoanCurrentInterest"));

            if (object.getString("LoanStatus").equalsIgnoreCase("Approved")) {

                holder.binding.layCurInt.setVisibility(View.VISIBLE);
                holder.binding.laynew.setVisibility(View.GONE);
                holder.binding.tvEnDate.setVisibility(View.GONE);
                holder.binding.tvClose.setVisibility(View.VISIBLE);
                holder.binding.tvStatus.setTextColor(Color.parseColor("#FF0000"));
            } else {
                holder.binding.laynew.setVisibility(View.VISIBLE);
                holder.binding.layCurInt.setVisibility(View.INVISIBLE);
                holder.binding.tvEnDate.setVisibility(View.VISIBLE);
                holder.binding.tvClose.setVisibility(View.GONE);
                holder.binding.tvStatus.setTextColor(Color.parseColor("#008000"));
            }


        } catch (JSONException e) {

        }

        holder.binding.ImageViewDown.setOnClickListener(v -> {
            holder.binding.KycExpand.expand();
            holder.binding.ImageViewDown.setVisibility(View.GONE);
            holder.binding.ImageViewUp.setVisibility(View.VISIBLE);

        });

        holder.binding.imgEdt.setOnClickListener(v->{
            try {
                onItemViewClickListener.onClick(v, holder.getAdapterPosition());
            } catch (JSONException e) {

            }
        });

        holder.binding.tvClose.setOnClickListener(v -> {
            try {
                onItemViewClickListener.onClick(v, holder.getAdapterPosition());
            } catch (JSONException e) {

            }
        });


        holder.binding.ImageViewUp.setOnClickListener(v -> {
            holder.binding.KycExpand.collapse();
            holder.binding.ImageViewDown.setVisibility(View.VISIBLE);
            holder.binding.ImageViewUp.setVisibility(View.GONE);
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
        AdaperCustLoanBinding binding;

        public ViewHolder(@NonNull View itemView, AdaperCustLoanBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }
}
