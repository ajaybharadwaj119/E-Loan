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
import com.akinfopark.savingsApp.Agent.adapter.AdapterAgentHistory;
import com.akinfopark.savingsApp.R;
import com.akinfopark.savingsApp.Utils.CommonFunctions;
import com.akinfopark.savingsApp.Utils.DialogUtils;
import com.akinfopark.savingsApp.Utils.MyPrefs;
import com.akinfopark.savingsApp.Utils.NetworkController;
import com.akinfopark.savingsApp.Utils.OnItemViewClickListener;
import com.akinfopark.savingsApp.adapter.AdapterTransHistory;
import com.akinfopark.savingsApp.databinding.ActivityTransHistoryBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransHistoryActivity extends AppCompatActivity {

    ActivityTransHistoryBinding binding;
    Activity activity;
    AlertDialog dialogLoading;
    List<JSONObject> list = new ArrayList<>();
    AdapterTransHistory adapterTransHistory;


    APICallbacks apiCallbacks = new APICallbacks() {
        @Override
        public void taskProgress(String tag, int progress, Bundle bundle) {

        }

        @Override
        public void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle) {
            DialogUtils.dismissLoading(dialogLoading, null, binding.Swipe);

            try {
                if (apiStatus == APIStatus.SUCCESS) {

                    if (tag.equalsIgnoreCase("Adminlist")) {
                        if (response.getBoolean("status")) {
                            list.clear();
                            CommonFunctions.setJSONArray(response.getJSONObject("result"), "Collected", list, adapterTransHistory);
                        }
                    }


                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        dialogLoading = DialogUtils.createLoading(this);

        binding.imageViewBack.setOnClickListener(v -> {
            finish();
        });

        adapterTransHistory = new AdapterTransHistory(activity, list, (v, i) -> {

        });

        binding.RecyclerView.setAdapter(adapterTransHistory);

        listApi();
    }

    void listApi() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "transactionhistory", map, "Adminlist", new Bundle(), apiCallbacks);
    }

}