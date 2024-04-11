package com.akinfopark.savingsApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.akinfopark.savingsApp.databinding.ActivityMainBinding;
import com.akinfopark.savingsApp.fragment.AccountFragment;
import com.akinfopark.savingsApp.fragment.HomeFragment;
import com.akinfopark.savingsApp.fragment.NotifFragment;
import com.akinfopark.savingsApp.fragment.ReportFragment;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    Activity activity;
    ActivityMainBinding binding;
    HomeFragment homeFragment = new HomeFragment();
  //  ReportFragment reportFragment = new ReportFragment();
    NotifFragment notifFragment = new NotifFragment();
    AccountFragment accountFragment = new AccountFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
        binding.Navbt.getMenu().getItem(0).setChecked(true);


        binding.Navbt.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                        return true;

                   /* case R.id.report:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, reportFragment).commit();
                        return true;*/

                    /*case R.id.notif:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, notifFragment).commit();
                        return true;*/
                    case R.id.account:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, accountFragment).commit();
                        return true;
                }

                return false;
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }
}