package com.akinfopark.savingsApp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.akinfopark.savingsApp.API.APICallbacks;
import com.akinfopark.savingsApp.API.APIStatus;
import com.akinfopark.savingsApp.API.APPConstants;
import com.akinfopark.savingsApp.API.UserData;
import com.akinfopark.savingsApp.R;
import com.akinfopark.savingsApp.Utils.CommonFunctions;
import com.akinfopark.savingsApp.Utils.DialogUtils;
import com.akinfopark.savingsApp.Utils.MyPrefs;
import com.akinfopark.savingsApp.Utils.NetworkController;
import com.akinfopark.savingsApp.Utils.OnItemViewClickListener;
import com.akinfopark.savingsApp.adapter.AdapterLonasHome;
import com.akinfopark.savingsApp.adapter.customer.AdapterLoans;
import com.akinfopark.savingsApp.databinding.ActivityLoanListBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoanListActivity extends AppCompatActivity {

    ActivityLoanListBinding binding;
    Activity activity;
    AlertDialog dialogLoading;
    List<JSONObject> list = new ArrayList<>();
    AdapterLonasHome adapterLoans;
    int page = 1;
    int currentLength = 0;
    int perPage = 10;
    APICallbacks apiCallbacks = new APICallbacks() {
        @Override
        public void taskProgress(String tag, int progress, Bundle bundle) {

        }

        @Override
        public void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle) {
            DialogUtils.dismissLoading(dialogLoading, null, binding.Swipe);
            try {
                if (apiStatus == APIStatus.SUCCESS) {
                    if (tag.equalsIgnoreCase("list")) {
                        if (response.getBoolean("status")) {
                            JSONObject object = response.getJSONObject("result");

                           // list.clear();
                            CommonFunctions.setJSONArray(object, "Loans", list, adapterLoans);

                            if (list.size()<1){
                                binding.empty.setVisibility(View.VISIBLE);
                            }else {
                                binding.empty.setVisibility(View.GONE);
                            }
                            currentLength =response.getJSONObject("result").getInt("Count");
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
        binding = ActivityLoanListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;
        dialogLoading = DialogUtils.createLoading(this);

        binding.imageViewBack.setOnClickListener(v -> {
            finish();
        });


        binding.RecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (CommonFunctions.isLastItemDisplayingLinear(recyclerView) && currentLength >= perPage) {
                    page++;
                    callNewApi();
                }
            }
        });

        callApi();

        adapterLoans = new AdapterLonasHome(activity, list, new OnItemViewClickListener() {
            @Override
            public void onClick(View v, int i) throws JSONException {
                JSONObject object = list.get(i);

                Bundle bundle = new Bundle();
                Intent intent = new Intent(activity, CustomerProfileActivity.class);
                bundle.putString("objValSave", object.getJSONObject("customer").toString());
                bundle.putString("custId", object.getString("LoanCustomerID"));
                bundle.putString("select", "loans");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        binding.RecyclerView.setAdapter(adapterLoans);

        binding.Swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearPage();
            }
        });

        binding.tvAddLoans.setOnClickListener(v -> {
            Intent intent = new Intent(activity, AddLoanGlobelActivity.class);
            startActivityForResult(intent, 20);
        });

        binding.download.setOnClickListener(v->{

        });


    }

    void clearPage() {
        page = 1;
        list.clear();
        // dialogLoading.show();
        adapterLoans.notifyDataSetChanged();
        callApi();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20) {
            if (resultCode == RESULT_OK) {
                callApi();
            }
        }
    }


    void callNewApi() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("currentPage", "" + page);
      //  dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "loans", map, "list", new Bundle(), apiCallbacks);
    }

    void callApi() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("currentPage", "" + page);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "loans", map, "list", new Bundle(), apiCallbacks);
    }
}