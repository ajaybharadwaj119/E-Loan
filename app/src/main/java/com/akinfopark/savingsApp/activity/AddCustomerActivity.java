package com.akinfopark.savingsApp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.akinfopark.savingsApp.API.APICallbacks;
import com.akinfopark.savingsApp.API.APIStatus;
import com.akinfopark.savingsApp.API.APPConstants;
import com.akinfopark.savingsApp.API.UserData;
import com.akinfopark.savingsApp.Utils.CommonFunctions;
import com.akinfopark.savingsApp.Utils.DialogUtils;
import com.akinfopark.savingsApp.Utils.MyDateSelectedListener;
import com.akinfopark.savingsApp.Utils.MyPrefs;
import com.akinfopark.savingsApp.Utils.NetworkController;
import com.akinfopark.savingsApp.databinding.ActivityAddCustBinding;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class AddCustomerActivity extends AppCompatActivity {

    ActivityAddCustBinding binding;
    Activity activity;
    String save = "", loan = "", firstname = "", lastname = "", phoneNumber = "", cardNumber = "", joinDate = "", address = "";
    AlertDialog dialogLoading;
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
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
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
                    joinDate = outputFormat1.format(date);

                    SimpleDateFormat outputFormat2 = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                    String outputDate2 = outputFormat2.format(date);

                    binding.edtDate.setText(outputDate2);
                    Log.i("DateValue", joinDate + "  " + outputDate2);
                } catch (ParseException e) {

                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCustBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        dialogLoading = DialogUtils.createLoading(this);

        binding.imageViewBack.setOnClickListener(v -> {
            finish();
        });

       /* binding.cheSave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    save = "savings";
                } else {
                    save = "";
                }
            }
        });

        binding.cheLoan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loan = "loan";
                } else {
                    loan = "";
                }
            }
        });*/
        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            String currentDate = String.format("%02d/%02d/%04d", day, month, year);
            Log.i("CurrentDate", currentDate + "");

            binding.edtDate.setText(currentDate);

            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            Date date = inputFormat.parse(currentDate);

            // Format the date into the output strings
            SimpleDateFormat outputFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            joinDate = outputFormat1.format(date);

            Log.i("JoinDateeee", joinDate);

        } catch (ParseException e) {

        }

        binding.save.setOnClickListener(v -> {

            firstname = binding.edtFstName.getText().toString();
            lastname = binding.edtLstName.getText().toString();
            phoneNumber = binding.edtPhone.getText().toString();
            cardNumber = binding.edtCardNum.getText().toString();
            joinDate = binding.edtDate.getText().toString();
            address = binding.edtAddress.getText().toString();


            if (firstname.equalsIgnoreCase("")) {
                Toast.makeText(this, "Please enter first name", Toast.LENGTH_SHORT).show();
            } /*else if (phoneNumber.length() < 10) {
                Toast.makeText(activity, "Please enter valid phone number", Toast.LENGTH_SHORT).show();
            } else if (cardNumber.equalsIgnoreCase("")) {
                Toast.makeText(this, "Please enter card number", Toast.LENGTH_SHORT).show();
            } else if (joinDate.equalsIgnoreCase("")) {
                Toast.makeText(this, "Please select join date", Toast.LENGTH_SHORT).show();
            }*/ else {

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                String currentDate = String.format("%02d/%02d/%04d", day, month, year);
                Log.i("CurrentDate", currentDate + "");

                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                Date date = null;
                try {
                    date = inputFormat.parse(currentDate);
                } catch (ParseException e) {

                }

                // Format the date into the output strings
                SimpleDateFormat outputFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String Date = outputFormat1.format(date);

                callApi(Date);
            }

        });

    }

    void callApi(String Date) {

        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("customername", firstname + " " + lastname);
        // map.put("cardno", cardNumber);
        map.put("joindate", Date);
        map.put("phone", phoneNumber);
        map.put("address", address);

        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "addcustomer", map, "add", new Bundle(), apiCallbacks);
    }

}