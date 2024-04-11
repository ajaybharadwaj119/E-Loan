package com.akinfopark.savingsApp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.akinfopark.savingsApp.API.APICallbacks;
import com.akinfopark.savingsApp.API.APIStatus;
import com.akinfopark.savingsApp.API.APPConstants;
import com.akinfopark.savingsApp.API.UserData;
import com.akinfopark.savingsApp.R;
import com.akinfopark.savingsApp.Utils.AlertYesNoListener;
import com.akinfopark.savingsApp.Utils.CommonFunctions;
import com.akinfopark.savingsApp.Utils.DialogUtils;
import com.akinfopark.savingsApp.Utils.MyPrefs;
import com.akinfopark.savingsApp.Utils.NetworkController;
import com.akinfopark.savingsApp.Utils.OnItemViewClickListener;
import com.akinfopark.savingsApp.adapter.AdapterAgentProfile;
import com.akinfopark.savingsApp.databinding.ActivityAgentProfileBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentProfileActivity extends AppCompatActivity {

    ActivityAgentProfileBinding binding;
    Activity activity;
    AlertDialog dialogLoading;
    String agentId = "", savingids = "";
    List<JSONObject> list = new ArrayList<>();
    AdapterAgentProfile adapterAgentProfile;

    boolean status = false;

    APICallbacks apiCallbacks = new APICallbacks() {
        @Override
        public void taskProgress(String tag, int progress, Bundle bundle) {

        }

        @Override
        public void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle) {
            DialogUtils.dismissLoading(dialogLoading, null, null);

            try {
                if (apiStatus == APIStatus.SUCCESS) {

                    if (tag.equalsIgnoreCase("delete")){
                        if (response.getBoolean("status")) {

                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("done", "done");
                            setResult(RESULT_OK, intent);
                            finish();

                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (tag.equalsIgnoreCase("profile")) {
                        if (response.getBoolean("status")) {
                            JSONObject object = response.getJSONObject("result");

                            binding.tvInter.setText("₹ " + object.getString("ToBeSettled"));
                            binding.tvCount.setText( object.getString("CustomerCount"));

                            if (object.getString("ToBeSettled").equalsIgnoreCase("0")) {
                                binding.btnCollected.setVisibility(View.GONE);
                            } else {
                                binding.btnCollected.setVisibility(View.VISIBLE);
                                list.clear();
                                CommonFunctions.setJSONArray(object, "ToBeSettledList", list, adapterAgentProfile);
                                JSONArray toBeSettledList = object.getJSONArray("ToBeSettledList");

                                StringBuilder outputBuilder = new StringBuilder();
                                for (int i = 0; i < toBeSettledList.length(); i++) {
                                    try {
                                        JSONObject item = toBeSettledList.getJSONObject(i);
                                        String savingRefID = item.getString("SavingRefID");
                                        outputBuilder.append(savingRefID);

                                        if (i < toBeSettledList.length() - 1) {
                                            outputBuilder.append(",");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                savingids = outputBuilder.toString();
                                Log.i("OutputValueee", savingids);
                            }

                        }
                    }

                    if (tag.equalsIgnoreCase("status")) {
                        if (response.getBoolean("status")) {

                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("done", "done");
                            setResult(RESULT_OK, intent);
                            finish();

                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (tag.equalsIgnoreCase("collect")) {
                        if (response.getBoolean("status")) {

                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("done", "done");
                            setResult(RESULT_OK, intent);
                            finish();

                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            } catch (JSONException e) {

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAgentProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        dialogLoading = DialogUtils.createLoading(this);

        Bundle bundle = CommonFunctions.getBundle(activity);

        if (!bundle.getString("objVal", "").equalsIgnoreCase("")) {

            try {
                JSONObject object = new JSONObject(bundle.getString("objVal"));

                agentId = object.getString("EmployeeID");

                Log.i("ObjectValue", object.toString());

                binding.tvAgName.setText(object.getString("EmployeeName"));
                binding.tvAgNum.setText("+91 " + object.getString("EmployeePhone"));
                binding.tvStatus.setText(object.getString("EmployeeStatus"));
                binding.tvPassword.setText( " : " +object.getString("EmployeePassword"));

                if (object.getString("EmployeeStatus").equalsIgnoreCase("Approved")) {
                    binding.btnDis.setVisibility(View.VISIBLE);
                    binding.btnEn.setVisibility(View.GONE);
                    binding.tvStatus.setTextColor(Color.parseColor("#28B463"));
                    status = true;
                } else {
                    binding.btnDis.setVisibility(View.GONE);
                    binding.btnEn.setVisibility(View.VISIBLE);
                    binding.tvStatus.setTextColor(Color.parseColor("#E74C3C"));
                    status = false;
                }

                callApi();

            } catch (JSONException e) {

            }
        }


        binding.btnCollected.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.Confirmation);
            builder.setMessage(R.string.ar_collect_amt);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CollectApi();
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

        });

        binding.imgDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.Confirmation);
            builder.setMessage(R.string.ar_delete_account);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    delete();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });


        binding.btnDis.setOnClickListener(v -> {
            DisableDialog();
        });

        binding.btnEn.setOnClickListener(v -> {
            EnableDialog();
        });


        binding.imageViewBack.setOnClickListener(v -> {
            finish();
        });

        adapterAgentProfile = new AdapterAgentProfile(activity, list, new OnItemViewClickListener() {
            @Override
            public void onClick(View v, int i) throws JSONException {

            }
        });

        binding.RecyclerView.setAdapter(adapterAgentProfile);

    }

    private void EnableDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enable Agent");
        builder.setMessage("Are you sure you want to enable this agent?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the "Yes" button click
                statusApi("Approved");
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the "No" button click
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void DisableDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Disable Agent");
        builder.setMessage("Are you sure you want to disable this agent?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the "Yes" button click
                statusApi("Pending");
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the "No" button click
                dialog.dismiss();
            }
        });
        builder.show();
    }

    void delete() {
        Map<String, String> map = new HashMap<>();
        map.put("agentid", agentId);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "deleteagent", map, "delete", new Bundle(), apiCallbacks);
    }

    void statusApi(String status) {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("agentid", agentId);
        map.put("agentstatus", status);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "setagentstatus", map, "status", new Bundle(), apiCallbacks);
    }

    void CollectApi() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("agentid", agentId);
        map.put("savingids", savingids);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "settlesaving", map, "collect", new Bundle(), apiCallbacks);
    }


    void callApi() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("agentid", agentId);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "agentprofile", map, "profile", new Bundle(), apiCallbacks);
    }

}