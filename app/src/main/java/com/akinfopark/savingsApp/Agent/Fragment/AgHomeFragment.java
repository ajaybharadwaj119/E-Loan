package com.akinfopark.savingsApp.Agent.Fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.akinfopark.savingsApp.API.APICallbacks;
import com.akinfopark.savingsApp.API.APIStatus;
import com.akinfopark.savingsApp.API.APPConstants;
import com.akinfopark.savingsApp.API.UserData;
import com.akinfopark.savingsApp.Agent.Activity.TransAgentHistoryActivity;
import com.akinfopark.savingsApp.R;
import com.akinfopark.savingsApp.Utils.CommonFunctions;
import com.akinfopark.savingsApp.Utils.DialogUtils;
import com.akinfopark.savingsApp.Utils.MyPrefs;
import com.akinfopark.savingsApp.Utils.NetworkController;
import com.akinfopark.savingsApp.Utils.OnItemViewClickListener;
import com.akinfopark.savingsApp.activity.CustomerListActivity;
import com.akinfopark.savingsApp.activity.CustomerProfileActivity;
import com.akinfopark.savingsApp.activity.LoginActivity;
import com.akinfopark.savingsApp.activity.SavingListActivity;
import com.akinfopark.savingsApp.adapter.customer.AdapterSavings;
import com.akinfopark.savingsApp.databinding.DialogAddCustomerBinding;
import com.akinfopark.savingsApp.databinding.DialogSaveBinding;
import com.akinfopark.savingsApp.databinding.DialogSuccessBinding;
import com.akinfopark.savingsApp.databinding.FragmentAgHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AgHomeFragment extends Fragment {

    FragmentAgHomeBinding binding;
    Activity activity;
    AlertDialog dialogLoading, dialogSuccess, dialogAddCust, dialogSavEdt;
    DialogSaveBinding saveBinding;
    DialogSuccessBinding successBinding;
    String addName = "", addNum = "", addAddress = "", CusId = "", CusEntAmt = "", entId = "";
    DialogAddCustomerBinding addCustomerBinding;

    AdapterSavings adapterSavings;
    List<JSONObject> listSave = new ArrayList<>();

    APICallbacks apiCallbacks = new APICallbacks() {
        @Override
        public void taskProgress(String tag, int progress, Bundle bundle) {

        }

        @Override
        public void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle) {
            DialogUtils.dismissLoading(dialogLoading, null, binding.Swipe);
            try {
                if (apiStatus == APIStatus.SUCCESS) {
                    if (tag.equalsIgnoreCase("dashboard")) {

                        if (response.getBoolean("status")) {
                            Log.i("Todayyyresponse", response.toString());
                            JSONObject object = response.getJSONObject("result");
                            Log.i("Todayyy", object.toString());
                            String totColl = object.getString("TodayCollected");
                            String toBe = object.getString("ToBeSettled");

                            binding.tvLoanAmt.setText("₹ " + totColl);
                            binding.tvToBe.setText("₹ " + toBe);

                            binding.EdtCardNoS.requestFocus();

                        } else {
                            MyPrefs.getInstance(getContext()).putBoolean("login", false);
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.startActivity(intent);
                            activity.finishAffinity();
                        }
                    }

                    if (tag.equalsIgnoreCase("search")) {
                        if (response.getBoolean("status")) {
                            JSONObject object = response.getJSONObject("result");
                            JSONArray array = object.getJSONArray("Customers");

                            int length = array.length();

                            if (length == 1) {
                                JSONObject objectVal = array.getJSONObject(0);
                                Log.i("ObjValue", objectVal.toString());
                                Bundle bundle1 = new Bundle();
                                Intent intent = new Intent(activity, CustomerProfileActivity.class);
                                bundle1.putString("objVal", objectVal.toString());
                                intent.putExtras(bundle1);
                                startActivity(intent);

                            } else {
                                Bundle bundle1 = new Bundle();
                                Intent intent = new Intent(activity, CustomerListActivity.class);
                                bundle1.putString("Search", "1");
                                bundle1.putString("Value", object.toString());
                                intent.putExtras(bundle1);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (tag.equalsIgnoreCase("add")) {
                        if (response.getBoolean("status")) {
                            successBinding.tvMsg.setText(response.getString("message"));
                            dialogAddCust.dismiss();
                            dialogSuccess.show();
                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (tag.equalsIgnoreCase("profile")) {
                        try {
                            if (response.getBoolean("status")) {
                                JSONObject object = response.getJSONObject("result");
                                Log.i("ObjectArray", object.toString());

                                JSONObject objCust = object.getJSONObject("Customer");

                                listSave.clear();

                                Object customerObj = object.get("Customer");
                                if (customerObj instanceof JSONObject) {
                                    binding.edtTotAmt.setText("₹ " + object.getString("SavingTotal"));
                                    binding.edtCustNameS.setText(objCust.getString("CustomerName"));
                                    CusId = objCust.getString("CustomerID");

                                    CommonFunctions.setJSONArray(object, "Savings", listSave, adapterSavings);
                                } else {
                                    binding.edtTotAmt.setText("");
                                    binding.edtCustNameS.setText("");
                                }

                            } else {
                                MyPrefs.getInstance(getContext()).putBoolean("login", false);
                                Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(activity, LoginActivity.class);
                                activity.startActivity(intent);
                                activity.finishAffinity();

                            }

                        } catch (JSONException e) {
                            binding.edtTotAmt.setText("");
                            binding.edtCustNameS.setText("");
                            Toast.makeText(activity, "No data found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (tag.equalsIgnoreCase("addAmt")) {
                        if (response.getBoolean("status")) {
                            Toast.makeText(activity, "Savings is Added", Toast.LENGTH_SHORT).show();
                            // savingsApi();
                            callApi();
                            binding.edtTotAmt.setText("");
                            binding.edtCustNameS.setText("");
                            binding.EdtCardNoS.setText("");
                            binding.edtCash.setText("");

                            listSave.clear();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAgHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        dialogLoading = DialogUtils.createLoading(activity);

        saveBinding = DialogSaveBinding.inflate(getLayoutInflater());
        dialogSavEdt = DialogUtils.getCustomAlertDialog(activity, saveBinding.getRoot());

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        Date currentDate1 = new Date();
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat dateFormatM = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        // startdate = dateFormatM.format(currentDate1);
        String OutDate = dateFormat1.format(currentDate1);
        binding.tvCurrentDate.setText(OutDate);


        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String[] weekdays = new DateFormatSymbols().getWeekdays();
        String currentDayOfWeek = weekdays[dayOfWeek];

        binding.tvCurrentDay.setText(currentDayOfWeek);


        saveBinding.submitBtn.setOnClickListener(v -> {

        });

        addCustomerBinding = DialogAddCustomerBinding.inflate(getLayoutInflater());
        dialogAddCust = DialogUtils.getCustomAlertDialog(activity, addCustomerBinding.getRoot());

        successBinding = DialogSuccessBinding.inflate(getLayoutInflater());
        dialogSuccess = DialogUtils.getCustomAlertDialog(activity, successBinding.getRoot());


        String originalString = MyPrefs.getInstance(getContext()).getString(UserData.KEY_EMP_PREFIX);
        if (originalString.length() > 0) {

            String modifiedString = originalString.substring(0, originalString.length() - 1);
            binding.tvPrfx.setText(modifiedString+" Agent");
            // Now, modifiedString contains "Hell" (the last letter 'o' has been removed)
            // You can use modifiedString as needed.
        } else {
            // Handle the case where the string is empty (no letters to remove).
        }


        binding.tvAgentName.setText(MyPrefs.getInstance(getContext()).getString(UserData.KEY_EMP_NAME));

        callApi();

        adapterSavings = new AdapterSavings(activity, listSave, new OnItemViewClickListener() {
            @Override
            public void onClick(View v, int i) throws JSONException {
                JSONObject object = listSave.get(i);

                Log.i("AgentObjValue", object.toString());

                if (v.getId() == R.id.imgEdt) {
                    dialogSavEdt.show();
                }

            }
        });

        binding.RecyclerView.setAdapter(adapterSavings);


        // binding.AddSave.expand();


        binding.ImageViewUpSave.setVisibility(GONE);
        binding.ImageViewDownSave.setVisibility(VISIBLE);

        binding.ImageViewDownSave.setOnClickListener(v -> {
            binding.AddSave.expand();
            binding.ImageViewUpSave.setVisibility(View.VISIBLE);
            binding.ImageViewDownSave.setVisibility(View.GONE);
        });

        binding.ImageViewUpSave.setOnClickListener(v -> {
            binding.AddSave.collapse();
            binding.ImageViewUpSave.setVisibility(View.GONE);
            binding.ImageViewDownSave.setVisibility(View.VISIBLE);
            listSave.clear();
          //  adapterSavings.notifyDataSetChanged();
        });

        binding.layInclude.ImageViewDown.setOnClickListener(v -> {
            binding.layInclude.KycExpand.expand();

            binding.layInclude.ImageViewDown.setVisibility(GONE);
            binding.layInclude.ImageViewUp.setVisibility(VISIBLE);
        });

        binding.layInclude.ImageViewUp.setOnClickListener(v -> {
            binding.layInclude.KycExpand.collapse();

            binding.layInclude.ImageViewDown.setVisibility(VISIBLE);
            binding.layInclude.ImageViewUp.setVisibility(GONE);
        });

        binding.layInclude.tvSearch.setOnClickListener(v -> {
            String name = binding.layInclude.EdtCustNum.getText().toString();
            String num = binding.layInclude.EdtLoanNo.getText().toString();

           /* if (name.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Please enter name", Toast.LENGTH_SHORT).show();
            } else if (num.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Please enter loan number", Toast.LENGTH_SHORT).show();
            } else {*/
            searchCust(name, num);
            //}

        });

        binding.laySavings.setOnClickListener(v -> {
            Intent intent = new Intent(activity, SavingListActivity.class);
            startActivity(intent);
        });

        addCustomerBinding.tvAddCust.setOnClickListener(v -> {
            addName = addCustomerBinding.edtAddCust.getText().toString();
            addNum = addCustomerBinding.edtAddPhone.getText().toString();
            addAddress = addCustomerBinding.edtAddress.getText().toString();

            if (addName.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Please enter customer name", Toast.LENGTH_SHORT).show();
            } else {
                addCustApi();
            }
        });

        successBinding.tvDone.setOnClickListener(v -> {
            callApi();

            addCustomerBinding.edtAddCust.setText("");
            addCustomerBinding.edtAddPhone.setText("");
            addCustomerBinding.edtAddress.setText("");

            dialogSuccess.dismiss();
        });

        binding.tvAdd.setOnClickListener(v -> {
            dialogAddCust.show();
        });

        binding.Swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callApi();
                //searchCardNum();
            }
        });

       /* binding.EdtCardNoS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        binding.tvSave.setOnClickListener(v -> {

            CusId = binding.EdtCardNoS.getText().toString();
            CusEntAmt = binding.edtCash.getText().toString();

            if (CusId.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Please enter customer Id", Toast.LENGTH_SHORT).show();
            } else if (CusEntAmt.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Please enter amount", Toast.LENGTH_SHORT).show();
            } else {
                saveApi();
            }

        });

        binding.search.setOnClickListener(v -> {
            listSave.clear();
            adapterSavings.notifyDataSetChanged();
            entId = binding.EdtCardNoS.getText().toString();
            searchCardNum();
        });

        binding.layTransHistory.setOnClickListener(v -> {
            Intent intent = new Intent(activity, TransAgentHistoryActivity.class);
            startActivity(intent);
        });

    }

    void saveApi() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getContext()).getString(UserData.KEY_USER_ID));
        map.put("customerid", CusId);
        map.put("amount", CusEntAmt);
        map.put("Customerrefid", MyPrefs.getInstance(getContext()).getString(UserData.KEY_EMP_PREFIX) + CusId);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "addsaving", map, "addAmt", new Bundle(), apiCallbacks);
    }


    void addCustApi() {

        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            String currentDate = String.format("%02d/%02d/%04d", day, month, year);
            Log.i("CurrentDate", currentDate + "");

            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            Date date = inputFormat.parse(currentDate);

            // Format the date into the output strings
            SimpleDateFormat outputFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String Date = outputFormat1.format(date);

            Map<String, String> map = new HashMap<>();
            map.put("employeeid", MyPrefs.getInstance(getContext()).getString(UserData.KEY_USER_ID));
            map.put("customername", addName);
            map.put("joindate", Date);
            map.put("phone", addNum);
            map.put("address", addAddress);


            dialogLoading.show();
            NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "addcustomer", map, "add", new Bundle(), apiCallbacks);
        } catch (ParseException e) {

        }
    }

    void searchCardNum() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getContext()).getString(UserData.KEY_USER_ID));
        map.put("customerrefno", MyPrefs.getInstance(getContext()).getString(UserData.KEY_EMP_PREFIX) + entId);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "customerinfo", map, "profile", new Bundle(), apiCallbacks);
    }


   /* void testFun() {
        binding.edtTotAmt.setText("");
        binding.edtCustNameS.setText("");
        binding.EdtCardNoS.setText("");
        binding.edtCash.setText("");
        binding.EdtCardNoS.requestFocus();
    }*/


    void searchCust(String name, String num) {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getContext()).getString(UserData.KEY_USER_ID));
        map.put("customername", name);
        map.put("loanno", num);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "searchcustomers", map, "search", new Bundle(), apiCallbacks);
    }


    void callApi() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getContext()).getString(UserData.KEY_USER_ID));
        //dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "dashboard", map, "dashboard", new Bundle(), apiCallbacks);
    }

}