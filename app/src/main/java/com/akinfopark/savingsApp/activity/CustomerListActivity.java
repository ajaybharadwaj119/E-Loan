package com.akinfopark.savingsApp.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.akinfopark.savingsApp.adapter.AdapterCustomerList;
import com.akinfopark.savingsApp.databinding.ActivityAgentListBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerListActivity extends AppCompatActivity {
    ActivityAgentListBinding binding;
    Activity activity;
    AlertDialog dialogLoading;
    int page = 1;
    int currentLength = 0;
    int perPage = 10;
    List<JSONObject> list = new ArrayList<>();
    List<JSONObject> listNew = new ArrayList<>();
    AdapterCustomerList adapterCustomerList;
    APICallbacks apiCallbacks = new APICallbacks() {
        @Override
        public void taskProgress(String tag, int progress, Bundle bundle) {

        }

        @Override
        public void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle) {
            DialogUtils.dismissLoading(dialogLoading, null, null);
            try {
                if (apiStatus == APIStatus.SUCCESS) {

                    if (tag.equalsIgnoreCase("customers")) {
                        if (response.getBoolean("status")) {
                            JSONObject object = response.getJSONObject("result");

                            //  list.clear();
                            CommonFunctions.setJSONArray(object, "Customers", list, adapterCustomerList);

                            if (list.size() < 1) {
                                binding.empty.setVisibility(View.VISIBLE);
                            } else {
                                binding.empty.setVisibility(View.GONE);
                            }

                            currentLength = object.getInt("Count");
                            Log.i("LengthValue", currentLength + "  " + list.size());

                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (tag.equalsIgnoreCase("customersNew")) {
                        if (response.getBoolean("status")) {
                            JSONObject object = response.getJSONObject("result");
                            listNew.clear();
                            CommonFunctions.setJSONArray(object, "Customers", listNew, adapterCustomerList);
                            list.addAll(listNew);
                            adapterCustomerList.notifyDataSetChanged();
                            currentLength =  object.getInt("Count");

                          //  Log.i("LengthValue", currentLength + "  " + list.size());
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
        binding.TextViewTitile.setText(R.string.Customer_List);
        binding.tvAdd.setText(R.string.add_customer);
        binding.imageViewBack.setOnClickListener(v -> {
            finish();
        });

        Bundle bundle = CommonFunctions.getBundle(activity);

        adapterCustomerList = new AdapterCustomerList(activity, list, new OnItemViewClickListener() {
            @Override
            public void onClick(View v, int i) throws JSONException {
                JSONObject object = list.get(i);

                if (v.getId() == R.id.layout) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(activity, CustomerProfileActivity.class);
                    bundle.putString("objVal", object.toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                /*if (v.getId() == R.id.imgEdt) {
                    Toast.makeText(activity, "Edit", Toast.LENGTH_SHORT).show();
                }

                if (v.getId() == R.id.imgDelete) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("Confirmation");
                    builder.setMessage("Are you sure you want to delete this account?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteApi();
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }*/

            }
        });

        binding.RecyclerViewAgList.setAdapter(adapterCustomerList);

        binding.RecyclerViewAgList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (CommonFunctions.isLastItemDisplayingLinear(recyclerView) && currentLength >= perPage) {
                    page++;
                    callNewApi();
                }
            }
        });


        if (!bundle.getString("Search", "").equalsIgnoreCase("")) {
            try {
                JSONObject object = new JSONObject(bundle.getString("Value"));
                list.clear();
                CommonFunctions.setJSONArray(object, "Customers", list, adapterCustomerList);
            } catch (JSONException e) {

            }

        } else {

            clearPage();
        }


      /*  binding.Swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearPage();
            }
        });*/

        binding.layAdd.setOnClickListener(v -> {
            Intent intent = new Intent(activity, AddCustomerActivity.class);
            startActivityForResult(intent, 10);
        });




       /* binding.RecyclerViewAgList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.i("RecVal", dx + " -- " + dy + " -- " + recyclerView.getAdapter().getItemCount());

                if (CommonFunctions.isLastItemDisplayingLinear(recyclerView)  && currentLength >= perPage) {

                    page++;
                    callApi();
                }
            }
        });*/
    }


    void clearPage() {
        page = 1;
        list.clear();
        // dialogLoading.show();
        adapterCustomerList.notifyDataSetChanged();
        callApi();

    }

    void editApi() {
        Map<String, String> map = new HashMap<>();
        NetworkController.getInstance().callApiPost(CustomerListActivity.this, APPConstants.MAIN_URL + "", map, "edit", new Bundle(), apiCallbacks);
    }

    void deleteApi() {
        Map<String, String> map = new HashMap<>();

        dialogLoading.show();
        NetworkController.getInstance().callApiPost(CustomerListActivity.this, APPConstants.MAIN_URL + "", map, "delete", new Bundle(), apiCallbacks);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                clearPage();
            }
        }
    }

    void callNewApi() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("currentPage", "" + page);
        NetworkController.getInstance().callApiPost(CustomerListActivity.this, APPConstants.MAIN_URL + "customers",
                map, "customersNew", new Bundle(), apiCallbacks);
    }

    void callApi() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("currentPage", "" + page);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(CustomerListActivity.this, APPConstants.MAIN_URL + "customers", map, "customers", new Bundle(), apiCallbacks);
    }

}
