package com.akinfopark.savingsApp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.akinfopark.savingsApp.API.UserData;
import com.akinfopark.savingsApp.Agent.AgentMainActivity;
import com.akinfopark.savingsApp.MainActivity;
import com.akinfopark.savingsApp.R;
import com.akinfopark.savingsApp.Utils.MyPrefs;
import com.akinfopark.savingsApp.databinding.ActivitySplashBinding;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;
    Activity activity;
    private static int SPLASH_SCREEN_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        loadLocale();

        if (MyPrefs.getInstance(getApplicationContext()).getString("language").equalsIgnoreCase("ta")){
            setLang("ta");
        }else {
            setLang("en");
        }

        boolean login = MyPrefs.getInstance(getApplicationContext()).getBoolean("login");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (login) {
                    if (MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_TYPE).equalsIgnoreCase("Admin")) {
                        intent = new Intent(SplashActivity.this, MainActivity.class);
                    } else {
                        intent = new Intent(SplashActivity.this, AgentMainActivity.class);
                    }
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finishAffinity();
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }

    void setLang(String lng) {
        Locale locale = new Locale(lng);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(config, activity.getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = activity.getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lng);
        editor.apply();
    }

    void loadLocale() {
        SharedPreferences prefs = activity.getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLang(language);
    }
}