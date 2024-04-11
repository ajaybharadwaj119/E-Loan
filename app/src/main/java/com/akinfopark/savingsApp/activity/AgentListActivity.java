package com.akinfopark.savingsApp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.akinfopark.savingsApp.API.APICallbacks;
import com.akinfopark.savingsApp.API.APIStatus;
import com.akinfopark.savingsApp.API.APPConstants;
import com.akinfopark.savingsApp.API.UserData;
import com.akinfopark.savingsApp.MainActivity;
import com.akinfopark.savingsApp.R;
import com.akinfopark.savingsApp.Utils.CommonFunctions;
import com.akinfopark.savingsApp.Utils.DialogUtils;
import com.akinfopark.savingsApp.Utils.MyPrefs;
import com.akinfopark.savingsApp.Utils.NetworkController;
import com.akinfopark.savingsApp.Utils.OnItemViewClickListener;
import com.akinfopark.savingsApp.adapter.AdapterAgentList;
import com.akinfopark.savingsApp.databinding.ActivityAgentListBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentListActivity extends AppCompatActivity {

    ActivityAgentListBinding binding;
    Activity activity;
    AlertDialog dialogLoading;

    List<JSONObject> list = new ArrayList<>();
    AdapterAgentList adapterAgentList;

    APICallbacks apiCallbacks = new APICallbacks() {
        @Override
        public void taskProgress(String tag, int progress, Bundle bundle) {

        }

        @Override
        public void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle) {
            DialogUtils.dismissLoading(dialogLoading, null, null);

            try {
                if (apiStatus == APIStatus.SUCCESS) {
                    if (tag.equalsIgnoreCase("agent")) {
                        if (response.getBoolean("status")) {
                            JSONObject object = response.getJSONObject("result");

                            list.clear();
                            CommonFunctions.setJSONArray(object, "EmployeeList", list, adapterAgentList);
                            if (list.size() < 1) {
                                binding.empty.setVisibility(View.VISIBLE);
                            } else {
                                binding.empty.setVisibility(View.GONE);
                            }

                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAgentListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        dialogLoading = DialogUtils.createLoading(this);
        binding.TextViewTitile.setText(R.string.Agent_List);
        binding.tvAdd.setText(R.string.add_agent);

        binding.imageViewBack.setOnClickListener(v -> {
            finish();
        });


        callApi();

        adapterAgentList = new AdapterAgentList(activity, list, (v, i) -> {
            JSONObject object = list.get(i);

            if (v.getId() == R.id.layAgent) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(activity, AgentProfileActivity.class);
                bundle.putString("objVal", object.toString());
                intent.putExtras(bundle);
                startActivityForResult(intent, 11);

            }

        });

        binding.RecyclerViewAgList.setAdapter(adapterAgentList);


        binding.layAdd.setOnClickListener(v -> {
            Intent intent = new Intent(activity, AddAgentActivity.class);
            startActivityForResult(intent, 10);
        });

       /* binding.Swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callApi();
            }
        });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                callApi();
            }
        }

        if (requestCode == 11) {
            if (resultCode == RESULT_OK) {
                callApi();
            }
        }

    }

    void callApi() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "agents", map, "agent", new Bundle(), apiCallbacks);
    }
}