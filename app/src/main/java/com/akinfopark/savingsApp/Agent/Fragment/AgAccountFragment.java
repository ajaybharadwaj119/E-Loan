package com.akinfopark.savingsApp.Agent.Fragment;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akinfopark.savingsApp.API.UserData;
import com.akinfopark.savingsApp.R;
import com.akinfopark.savingsApp.Utils.MyPrefs;
import com.akinfopark.savingsApp.activity.LoginActivity;
import com.akinfopark.savingsApp.databinding.FragmentAgAccountBinding;

public class AgAccountFragment extends Fragment {


    FragmentAgAccountBinding binding;
    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAgAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();

        binding.tvName.setText(MyPrefs.getInstance(getContext()).getString(UserData.KEY_EMP_NAME));


        binding.layLogout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.Confirmation);
            builder.setMessage(R.string.logout_conf);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MyPrefs.getInstance(getContext()).putBoolean("login", false);
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                    activity.finishAffinity();
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
}