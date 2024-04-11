package com.akinfopark.savingsApp.fragment;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.akinfopark.savingsApp.Utils.AppSignatureHelper.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
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
import com.akinfopark.savingsApp.Utils.AlertYesNoListener;
import com.akinfopark.savingsApp.Utils.DialogUtils;
import com.akinfopark.savingsApp.Utils.FileDownloader;
import com.akinfopark.savingsApp.Utils.MyPrefs;
import com.akinfopark.savingsApp.Utils.NetworkController;
import com.akinfopark.savingsApp.activity.AgentListActivity;
import com.akinfopark.savingsApp.activity.CustomerListActivity;
import com.akinfopark.savingsApp.activity.CustomerProfileActivity;
import com.akinfopark.savingsApp.activity.ExpenseListActivity;
import com.akinfopark.savingsApp.activity.LoanListActivity;
import com.akinfopark.savingsApp.activity.LoginActivity;
import com.akinfopark.savingsApp.activity.SavingListActivity;
import com.akinfopark.savingsApp.activity.TransHistoryActivity;
import com.akinfopark.savingsApp.databinding.DialogPopupBinding;
import com.akinfopark.savingsApp.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    Activity activity;
    AlertDialog dialogLoading;
    String url = "";
    List<JSONObject> list = new ArrayList<>();
    public static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 10;

    APICallbacks apiCallbacks = new APICallbacks() {
        @Override
        public void taskProgress(String tag, int progress, Bundle bundle) {

        }

        @Override
        public void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle) {
            DialogUtils.dismissLoading(dialogLoading, null, binding.Swipe);
            try {
                if (apiStatus == APIStatus.SUCCESS) {

                    if (tag.equalsIgnoreCase("pdf")) {
                       // url = APPConstants.PDF + response.getString("output");
                        Log.i("PdfValue", url);
                        savePdf();

                    }

                    if (tag.equalsIgnoreCase("dashboard")) {
                        if (response.getBoolean("status")) {
                            JSONObject object = response.getJSONObject("result");

                            binding.tvTotalColl.setText("₹ " + object.getString("TotalCollected"));
                            binding.tvLoanAmt.setText("₹ " + object.getString("LoanAmount"));
                            binding.tvInter.setText("₹ " + object.getString("AmountOnAccount"));
                            binding.tvExpense.setText("₹ " + object.getString("Expenses"));
                            binding.tvCustomer.setText(object.getString("Customers"));
                            binding.tvOnAgents.setText("₹ " + object.getString("Cashinhand"));
                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    }

                    if (tag.equalsIgnoreCase("search")) {
                        if (response.getBoolean("status")) {
                            JSONObject object = response.getJSONObject("result");
                            JSONArray array = object.getJSONArray("Customers");

                            int length = array.length();

                            if (length == 1) {
                                JSONObject objectVal = array.getJSONObject(0);
                                Log.i("ObjValue", objectVal.toString());
                                Bundle bundle1 = new Bundle();
                                Intent intent = new Intent(activity, CustomerProfileActivity.class);
                                bundle1.putString("objVal", objectVal.toString());
                                intent.putExtras(bundle1);
                                startActivity(intent);

                            } else {
                                Bundle bundle1 = new Bundle();
                                Intent intent = new Intent(activity, CustomerListActivity.class);
                                bundle1.putString("Search", "1");
                                bundle1.putString("Value", object.toString());
                                intent.putExtras(bundle1);
                                startActivity(intent);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        dialogLoading = DialogUtils.createLoading(activity);



        callApi();

        Date currentDate1 = new Date();
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat dateFormatM = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        // startdate = dateFormatM.format(currentDate1);
        String OutDate = dateFormat1.format(currentDate1);
        binding.tvCurrentDate.setText(OutDate);


        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String[] weekdays = new DateFormatSymbols().getWeekdays();
        String currentDayOfWeek = weekdays[dayOfWeek];

        binding.tvCurrentDay.setText(currentDayOfWeek);


        String originalString = MyPrefs.getInstance(getContext()).getString(UserData.KEY_EMP_PREFIX);
        if (originalString.length() > 0) {

            String modifiedString = originalString.substring(0, originalString.length() - 1);
            binding.tvPrefix.setText(modifiedString + " Admin");
            // Now, modifiedString contains "Hell" (the last letter 'o' has been removed)
            // You can use modifiedString as needed.
        } else {
            // Handle the case where the string is empty (no letters to remove).
        }


        binding.layCustom.setOnClickListener(v -> {
            Intent intent = new Intent(activity, CustomerListActivity.class);
            startActivity(intent);
        });



        binding.layLoan.setOnClickListener(v -> {
            Intent intent = new Intent(activity, LoanListActivity.class);
            startActivity(intent);
        });
        binding.layExpense.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ExpenseListActivity.class);
            startActivity(intent);
        });

        binding.laySavings.setOnClickListener(v -> {
            Intent intent = new Intent(activity, SavingListActivity.class);
            startActivity(intent);
        });

        binding.layPayHist.setOnClickListener(v -> {
            Toast.makeText(activity, "Coming soon", Toast.LENGTH_SHORT).show();
        });

        binding.layReport.setOnClickListener(v -> {
            Toast.makeText(activity, "Coming soon", Toast.LENGTH_SHORT).show();
        });

        binding.layInclude.ImageViewDown.setOnClickListener(v -> {
            binding.layInclude.KycExpand.expand();

            binding.layInclude.ImageViewDown.setVisibility(GONE);
            binding.layInclude.ImageViewUp.setVisibility(VISIBLE);
        });

        binding.layInclude.ImageViewUp.setOnClickListener(v -> {
            binding.layInclude.KycExpand.collapse();

            binding.layInclude.ImageViewDown.setVisibility(VISIBLE);
            binding.layInclude.ImageViewUp.setVisibility(GONE);
        });

        binding.layInclude.tvSearch.setOnClickListener(v -> {
            String name = binding.layInclude.EdtCustNum.getText().toString();
            String num = binding.layInclude.EdtLoanNo.getText().toString();

           /* if (name.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Please enter name", Toast.LENGTH_SHORT).show();
            } else if (num.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Please enter loan number", Toast.LENGTH_SHORT).show();
            } else {*/
            searchCust(name, num);
            //}

        });

        binding.Swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callApi();
            }
        });

        binding.tvPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfDown();
            }
        });

        binding.layTransHistory.setOnClickListener(v -> {
            Intent intent = new Intent(activity, TransHistoryActivity.class);
            startActivity(intent);
        });

    }


    AlertYesNoListener alertYesNoListener = new AlertYesNoListener() {
        @Override
        public void onYesClick(String requestCode) {

        }

        @Override
        public void onNoClick(String requestCode) {

        }

        @Override
        public void onNeutralClick(String requestCode) {

        }
    };





    void savePdf() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            // Android version is less than 8
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                new DownloadFile().execute(String.valueOf(Uri.parse(url)), "savings.pdf");
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "savings.pdf");
                DialogUtils.showOKAlert(activity, "invalid", "PDF file Downloaded and saved in " + file, new Bundle(), true, alertYesNoListener);

            }

        } else {

            // Android version is 8 or greater

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setTitle("savings.pdf is Downloaded");
            request.setDescription("Downloading a file");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "savings.pdf");
            //Toast.makeText(MainActivity.this, "okay", Toast.LENGTH_SHORT).show();

            DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = downloadManager.enqueue(request);

            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            Cursor cursor = downloadManager.query(query);

            if (cursor.moveToFirst()) {
                @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                @SuppressLint("Range") int downloadedBytes = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                @SuppressLint("Range") int totalBytes = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                // update UI with download progress
            }

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "savings.pdf");
            DialogUtils.showOKAlert(activity, "invalid", "PDF file Downloaded and saved in " + file, new Bundle(), true, alertYesNoListener);


        }
    }


    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            android.util.Log.v(TAG, "doInBackground() Method invoked ");

            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            File pdfFile = new File(folder, fileName);
            android.util.Log.v(TAG, "doInBackground() pdfFile invoked " + pdfFile.getAbsolutePath());
            android.util.Log.v(TAG, "doInBackground() pdfFile invoked " + pdfFile.getAbsoluteFile());

            try {
                pdfFile.createNewFile();
                android.util.Log.v(TAG, "doInBackground() file created" + pdfFile);

            } catch (IOException e) {
                e.printStackTrace();
                android.util.Log.e(TAG, "doInBackground() error" + e.getMessage());
                android.util.Log.e(TAG, "doInBackground() error" + e.getStackTrace());


            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            android.util.Log.v(TAG, "doInBackground() file download completed");

            return null;
        }
    }

    void pdfDown() {
        Map<String, String> map = new HashMap<>();
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "generate_pdf", map, "pdf", new Bundle(), apiCallbacks);
    }

    void searchCust(String name, String num) {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getContext()).getString(UserData.KEY_USER_ID));
        map.put("customername", name);
        map.put("loanno", num);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "searchcustomers", map, "search", new Bundle(), apiCallbacks);
    }


    void callApi() {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getContext()).getString(UserData.KEY_USER_ID));
        //dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "dashboard", map, "dashboard", new Bundle(), apiCallbacks);
    }

}