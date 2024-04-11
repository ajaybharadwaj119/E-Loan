package com.akinfopark.savingsApp.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.akinfopark.savingsApp.adapter.customer.AdapterLoans;
import com.akinfopark.savingsApp.adapter.customer.AdapterSavings;
import com.akinfopark.savingsApp.databinding.ActivityCustomerProfileBinding;
import com.akinfopark.savingsApp.databinding.DialogAddSavingsBinding;
import com.akinfopark.savingsApp.databinding.DialogEditBinding;
import com.akinfopark.savingsApp.databinding.DialogSaveBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerProfileActivity extends AppCompatActivity {

    ActivityCustomerProfileBinding binding;
    Activity activity;
    DialogAddSavingsBinding dialogAddSavingsBinding;
    AlertDialog dialogLoading, dialogPopup, dialogEdit, dialogSavEdt;
    AdapterSavings adapterSavings;
    List<JSONObject> listSave = new ArrayList<>();
    AdapterLoans adapterLoans;
    List<JSONObject> listLoan = new ArrayList<>();
    boolean loan = false, saving = false, loanAdmin = false;
    String CustomerID = "", CustomerName = "", custid = "", CustomerRefID = "", CustomerJoinedOn = "", SavingCustomerID = "", type = "";

    DialogEditBinding editBinding;
    DialogSaveBinding saveBinding;

    APICallbacks apiCallbacks = new APICallbacks() {
        @Override
        public void taskProgress(String tag, int progress, Bundle bundle) {

        }

        @Override
        public void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle) {
            DialogUtils.dismissLoading(dialogLoading, null, null);
            try {
                if (apiStatus == APIStatus.SUCCESS) {

                    if (tag.equalsIgnoreCase("closeSav")) {
                        if (response.getBoolean("status")) {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (tag.equalsIgnoreCase("updateloan")) {
                        if (response.getBoolean("status")) {
                            dialogEdit.dismiss();
                            callApi();
                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (tag.equalsIgnoreCase("savUpd")) {
                        if (response.getBoolean("status")) {
                            dialogSavEdt.dismiss();
                            callApi();
                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (tag.equalsIgnoreCase("loans")) {
                        if (response.getBoolean("status")) {
                            JSONObject object = response.getJSONObject("result");

                            listLoan.clear();
                            CommonFunctions.setJSONArray(object, "Loans", listLoan, adapterLoans);
                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (tag.equalsIgnoreCase("savings")) {
                        if (response.getBoolean("status")) {
                            JSONObject object = response.getJSONObject("result");
                            JSONArray array = object.getJSONArray("Savings");
                            JSONObject objCustId = array.getJSONObject(0);
                            SavingCustomerID = objCustId.getString("SavingCustomerID");
                            Log.i("objCustIdobjCustId", objCustId.getString("SavingCustomerID"));


                            String value = objCustId.getString("SavingTotal");
                            StringBuilder separatedValue = new StringBuilder();

                            int length = value.length();

                            // Append the last three digits (thousands)
                            if (length > 3) {
                                separatedValue.append(value, length - 3, length);
                                separatedValue.insert(0, ",");
                                length -= 3;
                            }

                            // Append the digits in groups of two (tens and hundreds)
                            while (length > 2) {
                                separatedValue.insert(0, value, length - 2, length);
                                separatedValue.insert(0, ",");
                                length -= 2;
                            }

                            // Append the remaining digits (units)
                            separatedValue.insert(0, value, 0, length);
                            String separatedString = separatedValue.toString();
                            binding.tvSavAmt.setText("₹ " + separatedString);

                            JSONObject objectcustomer = objCustId.getJSONObject("customer");

                            binding.edtCusName.setText(objectcustomer.getString("CustomerName"));
                            binding.edtCardNum.setText(objectcustomer.getString("CustomerRefID"));
                            binding.tvJoinDate.setText(convertDateFormat(objectcustomer.getString("CustomerJoinedOn"), "dd/MM/yyyy"));

                            listSave.clear();
                            CommonFunctions.setJSONArray(objCustId, "payments", listSave, adapterSavings);
                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (tag.equalsIgnoreCase("addsaving")) {
                        if (response.getBoolean("status")) {
                            Toast.makeText(activity, "Savings is Added", Toast.LENGTH_SHORT).show();
                            dialogPopup.dismiss();
                            // savingsApi();
                            callApi();
                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (tag.equalsIgnoreCase("profile")) {
                        if (response.getBoolean("status")) {

                            JSONObject object = response.getJSONObject("result");
                            binding.tvSavAmt.setText("₹ " + amtConvert(object.getString("SavingTotal")));
                            binding.tvLoanAmt.setText("₹ " + amtConvert(object.getString("LoanTotal")));

                            binding.edtCusName.setText(object.getJSONObject("Customer").getString("CustomerName"));
                            // binding.edtCardNum.setText(object.getJSONObject("Customer").getString("CustomerPhone"));
                            binding.tvJoinDate.setText(convertDateFormat(object.getJSONObject("Customer").getString("CustomerJoinedOn"), "dd/MM/yyyy"));
                            binding.edtAddress.setText(object.getJSONObject("Customer").getString("CustomerAddress"));

                            JSONArray arrayLoan = object.getJSONArray("Loans");
                            listLoan.clear();

                            if (arrayLoan.length() == 0) {
                                loan = true;
                                binding.layLoan.setVisibility(View.GONE);
                            } else {
                                loan = false;
                                binding.layLoan.setVisibility(View.VISIBLE);
                                //binding.tvLoEmpty.setVisibility(View.GONE);
                                CommonFunctions.setJSONArray(object, "Loans", listLoan, adapterLoans);
                            }

                            JSONArray arraySave = object.getJSONArray("Savings");
                            listSave.clear();

                            if (arraySave.length() == 0) {
                                saving = true;
                         //       binding.laySave.setVisibility(View.GONE);
                            } else {
                                saving = false;
                         /*       binding.laySave.setVisibility(View.VISIBLE);
                                binding.recyclarsaving.setVisibility(View.VISIBLE);*/
                                CommonFunctions.setJSONArray(object, "Savings", listSave, adapterSavings);
                            }


                           /* if (saving) {
                                binding.tvSavEmpty.setVisibility(View.VISIBLE);
                                binding.CloseSavings.setVisibility(View.GONE);
                            } else {
                                binding.tvSavEmpty.setVisibility(View.GONE);
                                binding.CloseSavings.setVisibility(View.VISIBLE);
                            }
                            binding.tvLoEmpty.setVisibility(View.GONE);


                            if (type.equalsIgnoreCase("")) {
                                binding.dividerS.setVisibility(View.VISIBLE);
                                binding.dividerL.setVisibility(View.GONE);
                                binding.recyclarsaving.setVisibility(View.VISIBLE);
                                binding.recyclarloan.setVisibility(View.GONE);
                            } else if (type.equalsIgnoreCase("savings")) {
                                binding.dividerS.setVisibility(View.VISIBLE);
                                binding.dividerL.setVisibility(View.GONE);
                                binding.tvAddSavings.setVisibility(View.VISIBLE);
                                binding.tvAddLoans.setVisibility(View.GONE);
                                binding.recyclarsaving.setVisibility(View.VISIBLE);
                                binding.recyclarloan.setVisibility(View.GONE);
                                if (saving) {
                                    binding.tvSavEmpty.setVisibility(View.VISIBLE);
                                    binding.CloseSavings.setVisibility(View.GONE);
                                } else {
                                    binding.tvSavEmpty.setVisibility(View.GONE);
                                    binding.CloseSavings.setVisibility(View.VISIBLE);
                                }
                                binding.tvLoEmpty.setVisibility(View.GONE);
                            } else {
                                binding.dividerS.setVisibility(View.GONE);
                                binding.dividerL.setVisibility(View.VISIBLE);
                                binding.CloseSavings.setVisibility(View.INVISIBLE);
                                binding.tvAddSavings.setVisibility(View.GONE);

                                binding.tvSavEmpty.setVisibility(View.GONE);
                                binding.CloseSavings.setVisibility(View.VISIBLE);
                                if (loanAdmin) {
                                    binding.tvAddLoans.setVisibility(View.VISIBLE);
                                } else {
                                    binding.tvAddLoans.setVisibility(View.GONE);
                                }


                                binding.recyclarsaving.setVisibility(View.GONE);
                                binding.recyclarloan.setVisibility(View.VISIBLE);
                                // loanApi();

                                if (loan) {
                                    binding.tvLoEmpty.setVisibility(View.VISIBLE);
                                } else {
                                    binding.tvLoEmpty.setVisibility(View.GONE);
                                }
                            }*/

                           /* Log.i("SavLoan", saving + " " + loan);
                            if (!saving && loan) {
                                binding.layLoan.setVisibility(View.VISIBLE);
                                binding.dividerL.setVisibility(View.VISIBLE);
                                binding.recyclarloan.setVisibility(View.VISIBLE);
                                binding.tvLoEmpty.setVisibility(View.GONE);
                            }*/


                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (tag.equalsIgnoreCase("close")) {
                        if (response.getBoolean("status")) {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                            callApi();
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
        binding = ActivityCustomerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        dialogLoading = DialogUtils.createLoading(this);


        dialogAddSavingsBinding = DialogAddSavingsBinding.inflate(getLayoutInflater());
        dialogPopup = DialogUtils.getCustomAlertDialog(activity, dialogAddSavingsBinding.getRoot());

        editBinding = DialogEditBinding.inflate(getLayoutInflater());
        dialogEdit = DialogUtils.getCustomAlertDialog(activity, editBinding.getRoot());

        saveBinding = DialogSaveBinding.inflate(getLayoutInflater());
        dialogSavEdt = DialogUtils.getCustomAlertDialog(activity, saveBinding.getRoot());


        /*if (MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_TYPE).equalsIgnoreCase("Agent")) {
            binding.CloseSavings.setVisibility(View.INVISIBLE);
        } else {
            binding.CloseSavings.setVisibility(View.VISIBLE);
        }*/


        saveBinding.submitBtn.setOnClickListener(v -> {
            savingUpdateApi(saveBinding.edtAmt.getText().toString());
        });

        binding.imageViewBack.setOnClickListener(v -> {
            finish();
        });

        Bundle bundle = CommonFunctions.getBundle(activity);

        if (!bundle.getString("objVal", "").equalsIgnoreCase("")) {
            try {
                JSONObject object = new JSONObject(bundle.getString("objVal"));
                Log.i("ObjValueeee", object.toString());
                CustomerID = object.getString("CustomerID");
                CustomerName = object.getString("CustomerName");
                CustomerRefID = object.getString("CustomerRefID");
                CustomerJoinedOn = object.getString("CustomerJoinedOn");

                binding.edtCusName.setText(CustomerName);
                binding.edtCardNum.setText(CustomerRefID);
                binding.tvJoinDate.setText(convertDateFormat(CustomerJoinedOn, "dd/MM/yyyy"));
                callApi();
            } catch (JSONException e) {

            }
        }

        if (!bundle.getString("objValSave", "").equalsIgnoreCase("")) {
            try {
                JSONObject object = new JSONObject(bundle.getString("objValSave"));
                type = bundle.getString("select");
                Log.i("ObjValueeee", object.toString());
                CustomerID = bundle.getString("custId");
                CustomerName = object.getString("CustomerName");
                CustomerRefID = object.getString("CustomerRefID");
                CustomerJoinedOn = object.getString("CustomerJoinedOn");
                binding.edtCusName.setText(CustomerName);
                binding.edtCardNum.setText(CustomerRefID);
                binding.tvJoinDate.setText(convertDateFormat(CustomerJoinedOn, "dd/MM/yyyy"));

                callApi();
            } catch (JSONException e) {
            }
        }


        if (MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_TYPE).equalsIgnoreCase("Admin")) {
            loanAdmin = true;
        } else {
            loanAdmin = false;
        }

      /*  binding.laySave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.dividerS.setVisibility(View.VISIBLE);
                binding.dividerL.setVisibility(View.GONE);
                binding.tvAddSavings.setVisibility(View.VISIBLE);
                binding.tvAddLoans.setVisibility(View.GONE);
                binding.recyclarsaving.setVisibility(View.VISIBLE);
                binding.recyclarloan.setVisibility(View.GONE);
                if (saving) {
                    binding.tvSavEmpty.setVisibility(View.VISIBLE);
                    binding.CloseSavings.setVisibility(View.GONE);
                } else {
                    binding.tvSavEmpty.setVisibility(View.GONE);
                    binding.CloseSavings.setVisibility(View.VISIBLE);
                }
                binding.tvLoEmpty.setVisibility(View.GONE);

                //  savingsApi();
            }
        });*/

        binding.layLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   binding.dividerS.setVisibility(View.GONE);
                binding.dividerL.setVisibility(View.VISIBLE);
              //  binding.tvAddSavings.setVisibility(View.GONE);

                if (loanAdmin) {
                    binding.tvAddLoans.setVisibility(View.VISIBLE);
                } else {
                    binding.tvAddLoans.setVisibility(View.GONE);
                }

            //    binding.recyclarsaving.setVisibility(View.GONE);
                binding.recyclarloan.setVisibility(View.VISIBLE);
                // loanApi();

                if (loan) {
                    binding.tvLoEmpty.setVisibility(View.VISIBLE);
                } else {
                    binding.tvLoEmpty.setVisibility(View.GONE);
                }
               // binding.tvSavEmpty.setVisibility(View.GONE);

            }
        });

       /* if (type.equalsIgnoreCase("")) {
          //  binding.dividerS.setVisibility(View.VISIBLE);
            binding.dividerL.setVisibility(View.GONE);
          //  binding.recyclarsaving.setVisibility(View.VISIBLE);
            binding.recyclarloan.setVisibility(View.GONE);
        } else if (type.equalsIgnoreCase("savings")) {
            binding.dividerS.setVisibility(View.VISIBLE);
            binding.dividerL.setVisibility(View.GONE);
            binding.tvAddSavings.setVisibility(View.VISIBLE);
            binding.tvAddLoans.setVisibility(View.GONE);
            binding.recyclarsaving.setVisibility(View.VISIBLE);
            binding.recyclarloan.setVisibility(View.GONE);
            if (saving) {
                binding.tvSavEmpty.setVisibility(View.VISIBLE);
                binding.CloseSavings.setVisibility(View.GONE);
            } else {
                binding.tvSavEmpty.setVisibility(View.GONE);
                binding.CloseSavings.setVisibility(View.VISIBLE);
            }
            binding.tvLoEmpty.setVisibility(View.GONE);
        } else {
            binding.dividerS.setVisibility(View.GONE);
            binding.dividerL.setVisibility(View.VISIBLE);
            binding.CloseSavings.setVisibility(View.INVISIBLE);
            binding.tvAddSavings.setVisibility(View.GONE);

            binding.tvSavEmpty.setVisibility(View.GONE);
            binding.CloseSavings.setVisibility(View.VISIBLE);
            if (loanAdmin) {
                binding.tvAddLoans.setVisibility(View.VISIBLE);
            } else {
                binding.tvAddLoans.setVisibility(View.GONE);
            }


            binding.recyclarsaving.setVisibility(View.GONE);
            binding.recyclarloan.setVisibility(View.VISIBLE);
            // loanApi();

            if (loan) {
                binding.tvLoEmpty.setVisibility(View.VISIBLE);
            } else {
                binding.tvLoEmpty.setVisibility(View.GONE);
            }
        }*/


        // savingsApi();

      /*  binding.tvAddSavings.setOnClickListener(v -> {

            dialogAddSavingsBinding.edtCusName.setText(CustomerName);
            dialogAddSavingsBinding.edtCarNum.setText(CustomerRefID);
            dialogPopup.show();
            *//*Intent intent = new Intent(activity, AddSavingsActivity.class);
            startActivityForResult(intent, 10);*//*
        });*/

        dialogAddSavingsBinding.submitBtn.setOnClickListener(v -> {

            String txtAmt = dialogAddSavingsBinding.edtSavAmt.getText().toString();
            int fnAmt = 0;
            if (!txtAmt.equalsIgnoreCase("")) {
                fnAmt = Integer.parseInt(txtAmt);
            }


           /* if (fnAmt < 100) {
                Toast.makeText(activity, "Minimum amount is Rs.100", Toast.LENGTH_SHORT).show();
            } else*/
            if (fnAmt > 10000) {
                Toast.makeText(activity, "Maximum amount is Rs.10,000", Toast.LENGTH_SHORT).show();
            } else {
                AddSavingsApi(txtAmt);
                callApi();

                dialogAddSavingsBinding.edtSavAmt.setText("");

            }


        });


        binding.tvAddLoans.setOnClickListener(v -> {
            Bundle bundle1 = new Bundle();
            Intent intent = new Intent(activity, AddLoanActivity.class);
            bundle1.putString("CustId", CustomerID);
            bundle1.putString("CustName", CustomerName);
            intent.putExtras(bundle1);
            startActivityForResult(intent, 20);
        });


        adapterLoans = new AdapterLoans(activity, listLoan, (v, i) -> {
            JSONObject object = listLoan.get(i);
            Log.i("ObjValuee", object.toString());
            if (v.getId() == R.id.tvClose) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.Confirmation);
                builder.setMessage("Are you sure you want to close the loan?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            closeApi(object.getString("LoanRefID"));
                        } catch (JSONException e) {

                        }
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
            }

            if (v.getId() == R.id.imgEdt) {
                try {
                    editBinding.edtAmt.setText("");
                    custid = object.getString("LoanID");
                    editBinding.edtAmt.setText("" + object.getString("LoanAmount"));
                } catch (JSONException e) {

                }
                dialogEdit.show();
            }


        });

        binding.recyclarloan.setAdapter(adapterLoans);


        adapterSavings = new AdapterSavings(activity, listSave, (v, i) -> {
            JSONObject object = listSave.get(i);
            Log.i("SavingsValuee", object.toString());
            if (v.getId() == R.id.imgEdt) {
                custid = object.getString("SavingID");
                saveBinding.edtAmt.setText(object.getString("SavingAmount"));

                dialogSavEdt.show();
            }


        });
      //  binding.recyclarsaving.setAdapter(adapterSavings);

        editBinding.submitBtn.setOnClickListener(v -> {
            String edtAmt = editBinding.edtAmt.getText().toString();
            loanUpdateApi(edtAmt);
        });


        binding.CloseSavings.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.Confirmation);
            builder.setMessage(R.string.ar_close_savings);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    closeSavings();
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


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                //    savingsApi();
            }
        }

        if (requestCode == 20) {
            if (resultCode == RESULT_OK) {
                //  loanApi();
                callApi();
            }
        }

    }

    public static String amtConvert(String value) {
        StringBuilder separatedValue = new StringBuilder();

        int length = value.length();

        // Append the last three digits (thousands)
        if (length > 3) {
            separatedValue.append(value, length - 3, length);
            separatedValue.insert(0, ",");
            length -= 3;
        }

        // Append the digits in groups of two (tens and hundreds)
        while (length > 2) {
            separatedValue.insert(0, value, length - 2, length);
            separatedValue.insert(0, ",");
            length -= 2;
        }

        // Append the remaining digits (units)
        separatedValue.insert(0, value, 0, length);
        String separatedString = separatedValue.toString();
        return /*separatedString*/value;

    }

    void callApi() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("customerid", CustomerID);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "customerprofile", map, "profile", new Bundle(), apiCallbacks);
    }


    void AddSavingsApi(String amt) {

        String customerid = "";

        int hyphenIndex = CustomerRefID.indexOf('-'); // Find the index of the hyphen character
        if (hyphenIndex != -1) {
            customerid = CustomerRefID.substring(hyphenIndex + 1); ; // Extract the substring before the hyphen
            System.out.println(customerid); // This will print "BS02"
        } else {
            // Handle the case where there is no hyphen in the string
            customerid = CustomerRefID;
        }

        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("amount", amt);
        map.put("customerid", customerid);
        map.put("Customerrefid", CustomerRefID);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "addsaving", map, "addsaving", new Bundle(), apiCallbacks);
    }

    void closeSavings() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("customerid", CustomerID);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "closesaving", map, "closeSav", new Bundle(), apiCallbacks);
    }

    void closeApi(String id) {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("loanid", id);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "closeloan", map, "close", new Bundle(), apiCallbacks);
    }

    void loanUpdateApi(String amt) {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("amount", amt);
        map.put("loanid", custid);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "updateloan", map, "updateloan", new Bundle(), apiCallbacks);
    }

    void savingUpdateApi(String amt) {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("amount", amt);
        map.put("savingid", custid);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "updatesaving", map, "savUpd", new Bundle(), apiCallbacks);
    }


    public static String convertDateFormat(String inputDate, String outputFormat) {
        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = inputDateFormat.parse(inputDate);

            SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

}