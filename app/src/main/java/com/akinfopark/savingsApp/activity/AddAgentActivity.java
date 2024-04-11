package com.akinfopark.savingsApp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.akinfopark.savingsApp.API.APICallbacks;
import com.akinfopark.savingsApp.API.APIStatus;
import com.akinfopark.savingsApp.API.APPConstants;
import com.akinfopark.savingsApp.API.UserData;
import com.akinfopark.savingsApp.R;
import com.akinfopark.savingsApp.Utils.DialogUtils;
import com.akinfopark.savingsApp.Utils.MyPrefs;
import com.akinfopark.savingsApp.Utils.NetworkController;
import com.akinfopark.savingsApp.databinding.ActivityAddAgentBinding;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddAgentActivity extends AppCompatActivity {

    ActivityAddAgentBinding binding;
    Activity activity;
    String name = "", password = "", phone = "";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAgentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        dialogLoading = DialogUtils.createLoading(this);

        binding.imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.save.setOnClickListener(v -> {
            name = binding.edtFstName.getText().toString();
            password = binding.etLoginPassword.getText().toString();
            phone = binding.edtPhone.getText().toString();

            if (name.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Enter name", Toast.LENGTH_SHORT).show();
            } else if (password.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Enter password", Toast.LENGTH_SHORT).show();
            } else if (phone.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Enter phone number", Toast.LENGTH_SHORT).show();
            } else {
                callApi();
            }

        });


    }

    void callApi() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("agentname", name);
        map.put("agentphone", phone);
        map.put("password", password);

        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "addagent", map, "add", new Bundle(), apiCallbacks);
    }

}