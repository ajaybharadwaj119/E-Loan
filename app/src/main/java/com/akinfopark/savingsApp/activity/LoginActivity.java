package com.akinfopark.savingsApp.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.akinfopark.savingsApp.API.APICallbacks;
import com.akinfopark.savingsApp.API.APIStatus;
import com.akinfopark.savingsApp.API.APPConstants;
import com.akinfopark.savingsApp.API.UserData;
import com.akinfopark.savingsApp.Agent.AgentMainActivity;
import com.akinfopark.savingsApp.MainActivity;
import com.akinfopark.savingsApp.R;
import com.akinfopark.savingsApp.Utils.DialogUtils;
import com.akinfopark.savingsApp.Utils.MyPrefs;
import com.akinfopark.savingsApp.Utils.NetworkController;
import com.akinfopark.savingsApp.databinding.ActivityLoginBinding;
import com.akinfopark.savingsApp.databinding.DialogPopupBinding;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    Activity activity;
    String userName = "", Password = "";
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
                    if (tag.equalsIgnoreCase("login")) {
                        if (response.getBoolean("status")) {
                            JSONObject object = response.getJSONObject("result");
                            MyPrefs.getInstance(getApplicationContext()).putBoolean("login", true);
                            MyPrefs.getInstance(getApplicationContext()).putString(UserData.KEY_USER_ID, object.getString("EmployeeID"));
                            MyPrefs.getInstance(getApplicationContext()).putString(UserData.KEY_USER_TYPE, object.getString("EmployeeType"));
                            MyPrefs.getInstance(getApplicationContext()).putString(UserData.KEY_USER_STATUS, object.getString("EmployeeStatus"));
                            MyPrefs.getInstance(getApplicationContext()).putString(UserData.KEY_EMP_NAME, object.getString("EmployeeName"));
                            MyPrefs.getInstance(getApplicationContext()).putString(UserData.KEY_EMP_PREFIX, response.getString("AgencyPrefix") + "-");

                            if (object.getString("EmployeeType").equalsIgnoreCase("Admin")) {
                                Intent intent = new Intent(activity, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(activity, AgentMainActivity.class);
                                startActivity(intent);
                                finish();

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
        loadLocale();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;

        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));*/

        dialogLoading = DialogUtils.createLoading(this);

        binding.tvLogin.setOnClickListener(v -> {
            userName = binding.tvuserName.getText().toString();
            Password = binding.etLoginPassword.getText().toString();

            if (userName.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Please Enter User Name", Toast.LENGTH_SHORT).show();
            } else if (Password.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Please Enter Password", Toast.LENGTH_SHORT).show();
            } else {
                callApi();
            }

        });

        binding.imgLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cngLang();
            }
        });

    }

    void cngLang() {
        final String[] listItems = {"English", "தமிழ்"};

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
        mBuilder.setTitle("Choose Language..");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    setLang("en");
                    recreate();
                } else if (i == 1) {
                    setLang("ta");
                    recreate();
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    void setLang(String lng) {
        Locale locale = new Locale(lng);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lng);
        editor.apply();
    }

    void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLang(language);
    }


    void callApi() {
        Map<String, String> map = new HashMap<>();
        map.put("username", userName);
        map.put("password", Password);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            map.put("token", task.getResult());
            MyPrefs.getInstance(getApplicationContext()).putString(UserData.KEY_FB_TOKEN, task.getResult());
            dialogLoading.show();
            NetworkController.getInstance().callApiPost(LoginActivity.this, APPConstants.MAIN_URL + "login", map, "login", new Bundle(), apiCallbacks);
        });
    }

}