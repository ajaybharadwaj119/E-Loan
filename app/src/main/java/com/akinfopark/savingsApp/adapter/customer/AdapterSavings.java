package com.akinfopark.savingsApp.adapter.customer;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akinfopark.savingsApp.API.UserData;
import com.akinfopark.savingsApp.Utils.CommonFunctions;
import com.akinfopark.savingsApp.Utils.DataFormats;
import com.akinfopark.savingsApp.Utils.MyPrefs;
import com.akinfopark.savingsApp.Utils.OnItemViewClickListener;
import com.akinfopark.savingsApp.adapter.AdapterAgentList;
import com.akinfopark.savingsApp.databinding.AdapterAgentListBinding;
import com.akinfopark.savingsApp.databinding.AdapterCustSavingsBinding;
import com.akinfopark.savingsApp.databinding.AdapterCustomerListBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdapterSavings extends RecyclerView.Adapter<AdapterSavings.ViewHolder> {

    Activity activity;
    List<JSONObject> list;
    OnItemViewClickListener onItemViewClickListener;


    public AdapterSavings(Activity activity, List<JSONObject> list, OnItemViewClickListener onItemViewClickListener) {
        this.activity = activity;
        this.list = list;
        this.onItemViewClickListener = onItemViewClickListener;
    }

    @NonNull
    @Override
    public AdapterSavings.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterCustSavingsBinding binding = AdapterCustSavingsBinding.inflate(CommonFunctions.getAdapterInflater(parent), parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSavings.ViewHolder holder, int position) {

        JSONObject object = list.get(position);


        try {

            String SavingRefID = object.getString("SavingRefID");

            String SavingStatus = object.getString("SavingStatus");

            holder.binding.tvStatus.setText(SavingRefID);
            holder.binding.status.setText(object.getString("SavingStatus"));
           /* if (SavingStatus.equalsIgnoreCase("Approved")){
                holder.binding.tvStatus.setTextColor(Color.parseColor("#33cc33"));
            }else {
                holder.binding.tvStatus.setTextColor(Color.parseColor("#3257A7"));
            }*/


            holder.binding.tvAmt.setText("₹ " + object.getString("SavingAmount"));


            String inputDate = object.getString("SavingCreatedOn");


            if (MyPrefs.getInstance(activity.getApplicationContext()).getString(UserData.KEY_USER_TYPE).equalsIgnoreCase("Agent")) {

                if (SavingStatus.equalsIgnoreCase("Approved")) {
                    holder.binding.imgEdt.setVisibility(View.GONE);
                } else {
                    holder.binding.imgEdt.setVisibility(View.VISIBLE);
                }

            } else {
                holder.binding.imgEdt.setVisibility(View.VISIBLE);
            }

            Log.i("DateAndTime", inputDate);


            holder.binding.tvDate.setText(DataFormats.convertDateFormat(inputDate, "dd/MM/yyyy", "yyyy-MM-dd HH:mm:ss"));

        } catch (JSONException e) {

        }


        holder.binding.imgEdt.setOnClickListener(v -> {
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
        AdapterCustSavingsBinding binding;

        public ViewHolder(@NonNull View itemView, AdapterCustSavingsBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }
}
