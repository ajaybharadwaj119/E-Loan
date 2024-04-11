package com.akinfopark.savingsApp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.akinfopark.savingsApp.API.APICallbacks;
import com.akinfopark.savingsApp.API.APIStatus;
import com.akinfopark.savingsApp.API.APPConstants;
import com.akinfopark.savingsApp.API.UserData;
import com.akinfopark.savingsApp.R;
import com.akinfopark.savingsApp.Utils.DialogUtils;
import com.akinfopark.savingsApp.Utils.MyPrefs;
import com.akinfopark.savingsApp.Utils.NetworkController;
import com.akinfopark.savingsApp.databinding.ActivityReportBinding;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    ActivityReportBinding binding;
    Activity activity;
    AlertDialog dialogLoading;

    APICallbacks apiCallbacks = new APICallbacks() {
        @Override
        public void taskProgress(String tag, int progress, Bundle bundle) {

        }

        @Override
        public void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;
        dialogLoading = DialogUtils.createLoading(this);

        binding.imageViewBack.setOnClickListener(v->{
            finish();
        });

        binding.laySave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.dividerS.setVisibility(View.VISIBLE);
                binding.dividerL.setVisibility(View.GONE);
                /*binding.tvAddSavings.setVisibility(View.VISIBLE);
                binding.tvAddLoans.setVisibility(View.GONE);*/
                binding.recyclarsaving.setVisibility(View.VISIBLE);
                binding.recyclarloan.setVisibility(View.GONE);
                savingsApi();
            }
        });

        binding.layLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.dividerS.setVisibility(View.GONE);
                binding.dividerL.setVisibility(View.VISIBLE);
             /*   binding.tvAddSavings.setVisibility(View.GONE);
                binding.tvAddLoans.setVisibility(View.VISIBLE);*/
                binding.recyclarsaving.setVisibility(View.GONE);
                binding.recyclarloan.setVisibility(View.VISIBLE);
                loanApi();
            }
        });


        binding.dividerS.setVisibility(View.VISIBLE);
        binding.dividerL.setVisibility(View.GONE);
        binding.recyclarsaving.setVisibility(View.VISIBLE);
        binding.recyclarloan.setVisibility(View.GONE);
        savingsApi();
    }


    void savingsApi() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "savings", map, "savings", new Bundle(), apiCallbacks);
    }

    void loanApi() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "loans", map, "loans", new Bundle(), apiCallbacks);
    }

}