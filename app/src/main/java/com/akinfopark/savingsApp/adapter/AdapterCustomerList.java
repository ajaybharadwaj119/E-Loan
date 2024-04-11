package com.akinfopark.savingsApp.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akinfopark.savingsApp.Utils.CommonFunctions;
import com.akinfopark.savingsApp.Utils.DataFormats;
import com.akinfopark.savingsApp.Utils.OnItemViewClickListener;
import com.akinfopark.savingsApp.databinding.AdapterAgentListBinding;
import com.akinfopark.savingsApp.databinding.AdapterCustomerListBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterCustomerList extends RecyclerView.Adapter<AdapterCustomerList.ViewHolder> {
    Activity activity;
    List<JSONObject> list;
    OnItemViewClickListener onItemViewClickListener;

    public AdapterCustomerList(Activity activity, List<JSONObject> list, OnItemViewClickListener onItemViewClickListener) {
        this.activity = activity;
        this.list = list;
        this.onItemViewClickListener = onItemViewClickListener;
    }

    @NonNull
    @Override
    public AdapterCustomerList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterCustomerListBinding binding = AdapterCustomerListBinding.inflate(CommonFunctions.getAdapterInflater(parent), parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCustomerList.ViewHolder holder, int position) {

        JSONObject object = list.get(position);

        try {

            holder.binding.tvNo.setText(object.getString("CustomerRefID"));

            holder.binding.tvAgName.setText(object.getString("CustomerName"));
            // holder.binding.tvMail.setText(object.getString("CustomerEmail"));
            holder.binding.tvNum.setText(object.getString("CustomerRefID"));


            String input = object.getString("CustomerCreatedOn");
          /*  SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a");
            Date date = inputFormat.parse(input);
            String output = outputFormat.format(date);*/

            holder.binding.tvCreatedOn.setText(DataFormats.convertDateFormat(input, "dd/MM/yyyy", "yyyy-MM-dd HH:mm:ss"));

        } catch (JSONException e) {

        }

        /*holder.binding.imgEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onItemViewClickListener.onClick(v, holder.getAdapterPosition());
                } catch (JSONException e) {
                }
            }
        });
        holder.binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onItemViewClickListener.onClick(v, holder.getAdapterPosition());
                } catch (JSONException e) {
                }
            }
        });*/

        holder.binding.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onItemViewClickListener.onClick(v, holder.getAdapterPosition());
                } catch (JSONException e) {

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AdapterCustomerListBinding binding;

        public ViewHolder(@NonNull View itemView, AdapterCustomerListBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }
}
