package com.akinfopark.savingsApp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.akinfopark.savingsApp.API.APICallbacks;
import com.akinfopark.savingsApp.API.APIStatus;
import com.akinfopark.savingsApp.API.APPConstants;
import com.akinfopark.savingsApp.API.UserData;
import com.akinfopark.savingsApp.R;
import com.akinfopark.savingsApp.Utils.CommonFunctions;
import com.akinfopark.savingsApp.Utils.DialogUtils;
import com.akinfopark.savingsApp.Utils.MyDateSelectedListener;
import com.akinfopark.savingsApp.Utils.MyPrefs;
import com.akinfopark.savingsApp.Utils.NetworkController;
import com.akinfopark.savingsApp.databinding.ActivityAddLoanBinding;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddLoanActivity extends AppCompatActivity {

    ActivityAddLoanBinding binding;
    Activity activity;
    AlertDialog dialogLoading;
    String loanno = "", amount = "", interest = "", duescount = "", dueamount = "", startdate = "", enddate = "", customerid = "", custName = "";
    int lnAmt = 0;
    int interestVal = 0;
    int NoOfDays = 0;
    int fnAmt = 0;

    APICallbacks apiCallbacks = new APICallbacks() {
        @Override
        public void taskProgress(String tag, int progress, Bundle bundle) {

        }

        @Override
        public void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle) {
            DialogUtils.dismissLoading(dialogLoading, null, null);
            try {
                if (apiStatus == APIStatus.SUCCESS) {
                    if (tag.equalsIgnoreCase("add")) {
                        if (response.getBoolean("status")) {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("done", "done");
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(activity, "Error Found"/* + response.getString("message")*/, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
    };

    MyDateSelectedListener myDateSelectedListener = new MyDateSelectedListener() {
        @Override
        public void onDateSet(String tag, Calendar calendar, Date data) {
            if (tag.equalsIgnoreCase("endDate")) {

                String inputVal = String.valueOf(data);

                // Parse the input date string into a Date object
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                    Date date = inputFormat.parse(inputVal);

                    // Format the date into the output strings
                    SimpleDateFormat outputFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    enddate = outputFormat1.format(date);

                    SimpleDateFormat outputFormat2 = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                    String outputDate2 = outputFormat2.format(date);

                    binding.tvEnDate.setText(outputDate2);
                    Log.i("DateValueEn", enddate + "  " + outputDate2);

                    //calculation();
                } catch (ParseException e) {

                }
            }

            if (tag.equalsIgnoreCase("stDate")) {

                String inputVal = String.valueOf(data);

                // Parse the input date string into a Date object
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                    Date date = inputFormat.parse(inputVal);

                    // Format the date into the output strings
                    SimpleDateFormat outputFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    startdate = outputFormat1.format(date);

                    SimpleDateFormat outputFormat2 = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                    String outputDate2 = outputFormat2.format(date);

                    binding.tvStDate.setText(outputDate2);
                    Log.i("DateValueSt", startdate + "  " + outputDate2);
                } catch (ParseException e) {

                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddLoanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        dialogLoading = DialogUtils.createLoading(this);

        Bundle bundle = CommonFunctions.getBundle(activity);

        if (!bundle.getString("CustId", "").equalsIgnoreCase("")) {
            customerid = bundle.getString("CustId");
            custName = bundle.getString("CustName");
            binding.edtCusName.setText(custName);
        }

        binding.imageViewBack.setOnClickListener(v -> {
            finish();
        });

        Date currentDate1 = new Date();
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        SimpleDateFormat dateFormatM = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        startdate = dateFormatM.format(currentDate1);
        String OutDate = dateFormat1.format(currentDate1);
        binding.tvStDate.setText(OutDate);


        binding.tvStDate.setOnClickListener(v -> {
            // Get the current date
            Date currentDate = new Date();
            // Define the desired date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault());
            // Format the current date
            String formattedDate = dateFormat.format(currentDate);
            Calendar calendarMax = Calendar.getInstance();
            calendarMax.add(Calendar.MONTH, 1);
            CommonFunctions.showDatePickerMinMaxDate(activity, "", "", "", "stDate", myDateSelectedListener);
        });

        binding.tvEnDate.setOnClickListener(v -> {
            Calendar calendarMax = Calendar.getInstance();
            calendarMax.add(Calendar.MONTH, 1);
            CommonFunctions.showDatePickerMinMaxDate(activity, "", "", "", "endDate", myDateSelectedListener);
        });


        binding.tvSubmit.setOnClickListener(v -> {

            amount = binding.edtLoanAmt.getText().toString();
            interest = binding.edtInterest.getText().toString();


            if (amount.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Enter loan amount", Toast.LENGTH_SHORT).show();
            } else if (interest.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Enter interest", Toast.LENGTH_SHORT).show();
            }/* else if (startdate.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Select start date", Toast.LENGTH_SHORT).show();
            } else if (enddate.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Select end date", Toast.LENGTH_SHORT).show();
            }*/ else {
                AddLoanApi();
            }

        });

    }


    void calculation() {
        amount = binding.edtLoanAmt.getText().toString();
        interest = binding.edtInterest.getText().toString();

        lnAmt = Integer.parseInt(amount);
        interestVal = Integer.parseInt(interest);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        try {
            // Parse the start date and end date strings into Date objects
            Date startDate = dateFormat.parse(startdate);
            Date endDate = dateFormat.parse(enddate);

            // Create Calendar objects and set the parsed dates
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startDate);

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endDate);

            // Calculate the number of days between the start and end dates
            long millisecondsPerDay = 24 * 60 * 60 * 1000;
            long diffMilliseconds = endDate.getTime() - startDate.getTime();
            int diffDays = (int) (diffMilliseconds / millisecondsPerDay);

            NoOfDays = diffDays;

        } catch (ParseException e) {
            e.printStackTrace();
        }


        fnAmt = lnAmt * (interestVal / 30) * NoOfDays;

        Log.i("FinalAmt", lnAmt + " " + interestVal + " " + NoOfDays + " " + fnAmt);
    }

    void AddLoanApi() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("amount", amount);
        map.put("interest", interest);
        map.put("startdate", startdate);
        map.put("enddate", enddate);
        map.put("customerid", customerid);

        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "addcustomerloan", map, "add", new Bundle(), apiCallbacks);
    }


}