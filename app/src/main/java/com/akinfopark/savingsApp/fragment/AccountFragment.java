package com.akinfopark.savingsApp.fragment;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.akinfopark.savingsApp.activity.LoginActivity;
import com.akinfopark.savingsApp.databinding.FragmentAccountBinding;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AccountFragment extends Fragment {

    FragmentAccountBinding binding;
    Activity activity;
    AlertDialog dialogLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


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
                    String enddate = outputFormat1.format(date);

                    SimpleDateFormat outputFormat2 = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                    String outputDate2 = outputFormat2.format(date);

                    //Toast.makeText(activity, "" + enddate, Toast.LENGTH_SHORT).show();

                    closeAllApi(enddate);

                    // binding.tvEnDate.setText(outputDate2);
                    // Log.i("DateValueEn", enddate + "  " + outputDate2);

                } catch (ParseException e) {

                }
            }

        }
    };


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        dialogLoading = DialogUtils.createLoading(activity);

        loadLocale();

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

        binding.laylang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cngLang();
            }
        });

        binding.layLoanEnd.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.Confirmation);
            builder.setMessage(R.string.close_loan_conf);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Calendar calendarMax = Calendar.getInstance();
                    calendarMax.add(Calendar.MONTH, 1);
                    CommonFunctions.showDatePickerMinMaxDateNew(activity, "", "", "", "endDate", myDateSelectedListener);
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

    APICallbacks apiCallbacks = new APICallbacks() {
        @Override
        public void taskProgress(String tag, int progress, Bundle bundle) {

        }

        @Override
        public void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle) {
            DialogUtils.dismissLoading(dialogLoading, null, null);
        }
    };

    void cngLang() {
        final String[] listItems = {"English", "தமிழ்"};

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
        mBuilder.setTitle("Choose Language..");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    setLang("en");
                    MyPrefs.getInstance(getContext()).putString("language", "en");
                    activity.recreate();
                } else if (i == 1) {
                    setLang("ta");
                    MyPrefs.getInstance(getContext()).putString("language", "ta");
                    activity.recreate();
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

    void closeAllApi(String date) {
        Map<String, String> map = new HashMap<>();
        map.put("enddate", date);

        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "closeallloans", map, "close", new Bundle(), apiCallbacks);
    }

}