package com.akinfopark.savingsApp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.akinfopark.savingsApp.API.APICallbacks;
import com.akinfopark.savingsApp.API.APIStatus;
import com.akinfopark.savingsApp.API.APPConstants;
import com.akinfopark.savingsApp.API.UserData;
import com.akinfopark.savingsApp.Utils.DialogUtils;
import com.akinfopark.savingsApp.Utils.MyPrefs;
import com.akinfopark.savingsApp.Utils.NetworkController;
import com.akinfopark.savingsApp.databinding.ActivitySavingsAddBinding;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddSavingsActivity extends AppCompatActivity {

    ActivitySavingsAddBinding binding;
    String name = "", cardNum = "", savAmt = "", interest = "", dueAmt = "", noAmt = "", matAmt = "";
    AlertDialog dialogLoading;
    Activity activity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavingsAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        dialogLoading = DialogUtils.createLoading(this);

        binding.imageViewBack.setOnClickListener(v -> {
            finish();
        });

        binding.submitBtn.setOnClickListener(v -> {
            name = binding.edtCusName.getText().toString();
            cardNum = binding.edtCarNum.getText().toString();
            savAmt = binding.edtSavAmt.getText().toString();
            interest = binding.edtInteret.getText().toString();
            dueAmt = binding.edtDueAmt.getText().toString();
            noAmt = binding.edtNoDue.getText().toString();
            matAmt = binding.edtMatAmt.getText().toString();

            callApi();

        });

    }

    void callApi() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("amount", savAmt);
        map.put("interest", interest);
        map.put("duescount", noAmt);
        map.put("dueamount", dueAmt);
        map.put("customerid", "1");

        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "addsaving", map, "add", new Bundle(), apiCallbacks);
    }

}