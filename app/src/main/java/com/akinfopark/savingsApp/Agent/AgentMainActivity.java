package com.akinfopark.savingsApp.Agent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.akinfopark.savingsApp.Agent.Fragment.AgAccountFragment;
import com.akinfopark.savingsApp.Agent.Fragment.AgHomeFragment;
import com.akinfopark.savingsApp.Agent.Fragment.AgNotifFragment;
import com.akinfopark.savingsApp.R;
import com.akinfopark.savingsApp.databinding.ActivityAgentMainBinding;
import com.google.android.material.navigation.NavigationBarView;

public class AgentMainActivity extends AppCompatActivity {

    ActivityAgentMainBinding binding;
    Activity activity;

    AgHomeFragment homeFragment = new AgHomeFragment();
    AgNotifFragment notifFragment = new AgNotifFragment();
    AgAccountFragment accountFragment = new AgAccountFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAgentMainBinding.inflate(getLayoutInflater());
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

                    case R.id.account:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, accountFragment).commit();
                        return true;
                }

                return false;
            }
        });

    }
}