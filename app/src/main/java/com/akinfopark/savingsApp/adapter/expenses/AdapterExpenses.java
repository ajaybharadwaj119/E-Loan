package com.akinfopark.savingsApp.adapter.expenses;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akinfopark.savingsApp.Utils.CommonFunctions;
import com.akinfopark.savingsApp.Utils.DataFormats;
import com.akinfopark.savingsApp.Utils.OnItemViewClickListener;
import com.akinfopark.savingsApp.adapter.AdapterCustomerList;
import com.akinfopark.savingsApp.databinding.AdapterExpenseBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class AdapterExpenses extends RecyclerView.Adapter<AdapterExpenses.ViewHolder> {


    Activity activity;
    List<JSONObject> list;
    OnItemViewClickListener onItemViewClickListener;

    public AdapterExpenses(Activity activity, List<JSONObject> list, OnItemViewClickListener onItemViewClickListener) {
        this.activity = activity;
        this.list = list;
        this.onItemViewClickListener = onItemViewClickListener;
    }


    @NonNull
    @Override
    public AdapterExpenses.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterExpenseBinding binding = AdapterExpenseBinding.inflate(CommonFunctions.getAdapterInflater(parent), parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterExpenses.ViewHolder holder, int position) {
        JSONObject object = list.get(position);

        try {
            holder.binding.tvAgTitle.setText(object.getString("ExpenseName"));
            holder.binding.tvAmt.setText("₹ " + object.getString("ExpenseAmount"));


            /*DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(object.getString("ExpenseDateTime"), inputFormatter);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a", Locale.ENGLISH);
            String formattedDateTime = dateTime.format(outputFormatter);*/
            holder.binding.tvDate.setText(DataFormats.convertDateFormat(object.getString("ExpenseDateTime"), "dd/MM/yyyy", "yyyy-MM-dd HH:mm:ss"));


        } catch (JSONException e) {

        }

        holder.binding.ImgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onItemViewClickListener.onClick(v, holder.getAdapterPosition());
                } catch (JSONException e) {
                }
            }
        });

        holder.binding.imgEdt.setOnClickListener(new View.OnClickListener() {
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
        AdapterExpenseBinding binding;

        public ViewHolder(@NonNull View itemView, AdapterExpenseBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }
}
