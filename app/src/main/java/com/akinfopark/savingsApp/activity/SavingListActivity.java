package com.akinfopark.savingsApp.activity;

import static com.akinfopark.savingsApp.Utils.AppSignatureHelper.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.akinfopark.savingsApp.API.APICallbacks;
import com.akinfopark.savingsApp.API.APIStatus;
import com.akinfopark.savingsApp.API.APPConstants;
import com.akinfopark.savingsApp.API.UserData;
import com.akinfopark.savingsApp.R;
import com.akinfopark.savingsApp.Utils.AlertYesNoListener;
import com.akinfopark.savingsApp.Utils.CommonFunctions;
import com.akinfopark.savingsApp.Utils.DialogUtils;
import com.akinfopark.savingsApp.Utils.FileDownloader;
import com.akinfopark.savingsApp.Utils.MyPrefs;
import com.akinfopark.savingsApp.Utils.NetworkController;
import com.akinfopark.savingsApp.Utils.OnItemViewClickListener;
import com.akinfopark.savingsApp.adapter.AdapterSavingHome;
import com.akinfopark.savingsApp.adapter.customer.AdapterSavings;
import com.akinfopark.savingsApp.databinding.ActivitySavingListBinding;
import com.akinfopark.savingsApp.databinding.DialogAddSavingsBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavingListActivity extends AppCompatActivity {

    ActivitySavingListBinding binding;
    Activity activity;
    AlertDialog dialogLoading, dialogAdd, dialogPopup;
    DialogAddSavingsBinding addSavingsBinding;
    List<JSONObject> list = new ArrayList<>();
    AdapterSavingHome adapterSavings;
    DialogAddSavingsBinding dialogAddSavingsBinding;
    String url = "", pdfName = "savings", CustomerName = "", CustomerRefID = "", CustomerID = "";
    public static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 10;
    int page = 1;
    int currentLength = 0;
    int perPage = 10;

    private boolean isLoading = false;

    // Add a method to reset the isLoading flag
    private void setLoading(boolean loading) {
        isLoading = loading;
    }

    APICallbacks apiCallbacks = new APICallbacks() {
        @Override
        public void taskProgress(String tag, int progress, Bundle bundle) {

        }

        @Override
        public void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle) {
            DialogUtils.dismissLoading(dialogLoading, null, binding.Swipe);
            try {
                if (apiStatus == APIStatus.SUCCESS) {

                    if (tag.equalsIgnoreCase("addsavingNew")) {
                        if (response.getBoolean("status")) {
                            Toast.makeText(activity, "Savings is Added", Toast.LENGTH_SHORT).show();
                            dialogPopup.dismiss();
                            callApi();
                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (tag.equalsIgnoreCase("pdf")) {
                        url = APPConstants.MAIN_URL + response.getString("output");

                        savePdf();

                      /*  byte[] pdfData = url.getBytes();

// Provide a file name for the PDF file
                        String fileName = "ashikeee.pdf";

// Save the PDF file
                        savePdfFile(pdfData, fileName);*/

                    }

                    if (tag.equalsIgnoreCase("list")) {
                        if (response.getBoolean("status")) {
                            JSONObject object = response.getJSONObject("result");

                            //list.clear();
                            CommonFunctions.setJSONArray(object, "Savings", list, adapterSavings);

                            if (list.size() < 1) {
                                binding.empty.setVisibility(View.VISIBLE);
                            } else {
                                binding.empty.setVisibility(View.GONE);
                            }
                            currentLength =response.getJSONObject("result").getInt("Count");
                        } else {
                            Toast.makeText(activity, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (tag.equalsIgnoreCase("addsaving")) {
                        if (response.getBoolean("status")) {
                            Toast.makeText(activity, "Savings is Added", Toast.LENGTH_SHORT).show();

                            addSavingsBinding.edtCarNum.setText("");
                            addSavingsBinding.edtSavAmt.setText("");

                            dialogAdd.dismiss();
                            clearPage();
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
        binding = ActivitySavingListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        dialogLoading = DialogUtils.createLoading(this);

        dialogAddSavingsBinding = DialogAddSavingsBinding.inflate(getLayoutInflater());
        dialogPopup = DialogUtils.getCustomAlertDialog(activity, dialogAddSavingsBinding.getRoot());

        addSavingsBinding = DialogAddSavingsBinding.inflate(getLayoutInflater());
        dialogAdd = DialogUtils.getCustomAlertDialog(activity, addSavingsBinding.getRoot());

        addSavingsBinding.LayCusName.setVisibility(View.GONE);
        addSavingsBinding.LayCusNUm.setVisibility(View.GONE);
        addSavingsBinding.LayCusNewNum.setVisibility(View.VISIBLE);


        addSavingsBinding.tvPrefix.setText(""+MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_EMP_PREFIX));

        /*addSavingsBinding.edtCarNumNew.setText(MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_EMP_PREFIX));
        Selection.setSelection(addSavingsBinding.edtCarNumNew.getText(), addSavingsBinding.edtCarNumNew.getText().length());

        addSavingsBinding.edtCarNumNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith(MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_EMP_PREFIX))){
                    addSavingsBinding.edtCarNumNew.setText(MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_EMP_PREFIX));
                    Selection.setSelection(addSavingsBinding.edtCarNumNew.getText(), addSavingsBinding.edtCarNumNew.getText().length());

                }
            }
        });*/

        addSavingsBinding.submitBtn.setOnClickListener(v -> {
            String num = addSavingsBinding.edtCarNumNew.getText().toString();
            String amt = addSavingsBinding.edtSavAmt.getText().toString();

            if (num.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Please enter card number", Toast.LENGTH_SHORT).show();
            } else if (amt.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Please enter Amount", Toast.LENGTH_SHORT).show();
            } else {
                AddSavingsApi(num, amt);
            }

        });


        binding.imageViewBack.setOnClickListener(v -> {
            finish();
        });

        binding.RecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!isLoading && CommonFunctions.isLastItemDisplayingLinear(recyclerView) && currentLength >= perPage) {
                    isLoading = true;
                    page++;
                    callNewApi();
                }
            }
        });

        callApi();

        binding.Swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearPage();
            }
        });
        adapterSavings = new AdapterSavingHome(activity, list, new OnItemViewClickListener() {
            @Override
            public void onClick(View v, int i) throws JSONException {
                JSONObject object = list.get(i);

                CustomerName = object.getJSONObject("customer").getString("CustomerName");
                CustomerRefID = object.getJSONObject("customer").getString("CustomerRefID");
                CustomerID = object.getString("SavingCustomerID");

                if (v.getId() == R.id.laySave) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(activity, CustomerProfileActivity.class);
                    bundle.putString("objValSave", object.getJSONObject("customer").toString());
                    bundle.putString("custId", object.getString("SavingCustomerID"));
                    bundle.putString("select", "savings");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                if (v.getId() == R.id.tvCreatedOn) {
                    dialogAddSavingsBinding.edtCusName.setText(CustomerName);
                    dialogAddSavingsBinding.edtCarNum.setText(CustomerRefID);
                    dialogPopup.show();
                }


            }
        });

        binding.RecyclerView.setAdapter(adapterSavings);


        dialogAddSavingsBinding.submitBtn.setOnClickListener(v -> {

            String txtAmt = dialogAddSavingsBinding.edtSavAmt.getText().toString();
            int fnAmt = 0;
            if (!txtAmt.equalsIgnoreCase("")) {
                fnAmt = Integer.parseInt(txtAmt);
            }


           /* if (fnAmt < 100) {
                Toast.makeText(activity, "Minimum amount is Rs.100", Toast.LENGTH_SHORT).show();
            } else*/
            if (fnAmt > 10000) {
                Toast.makeText(activity, "Maximum amount is Rs.10,000", Toast.LENGTH_SHORT).show();
            } else {
                AddSavingsApi(txtAmt);
                //  callApi();

                dialogAddSavingsBinding.edtSavAmt.setText("");

            }


        });


        binding.tvAddSavings.setOnClickListener(v -> {
            dialogAdd.show();

        });


        binding.download.setOnClickListener(v -> {

            pdfDown();

        });


    }

    void savePdf() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            // Android version is less than 8
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                new DownloadFile().execute(String.valueOf(Uri.parse(url)), pdfName + "_snapay.pdf");
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), pdfName + "_snapay.pdf");
                DialogUtils.showOKAlert(activity, "invalid", "PDF file Downloaded and saved in " + file, new Bundle(), true, alertYesNoListener);

            }

        } else {

            // Android version is 8 or greater

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setTitle(pdfName + "_snapay.pdf is Downloaded");
            request.setDescription("Downloading a file");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, pdfName + "_snapay.pdf");
            //Toast.makeText(MainActivity.this, "okay", Toast.LENGTH_SHORT).show();

            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
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

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), pdfName + "_snapay.pdf");
            DialogUtils.showOKAlert(activity, "invalid", "PDF file Downloaded and saved in " + file, new Bundle(), true, alertYesNoListener);


        }
    }

    private void savePdfFile(byte[] pdfData, String fileName) {
        try {
            // Create a file on external storage
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            FileOutputStream fileOutput = new FileOutputStream(file);

            // Write the PDF data to the file
            fileOutput.write(pdfData);

            // Close the stream
            fileOutput.close();

            // Optionally, you can use a file provider to grant access to the file
            // to other apps on Android 7.0 and above.
            // Here's an example of how to generate a content URI using FileProvider:
            // Uri contentUri = FileProvider.getUriForFile(context, "com.example.fileprovider", file);
            // Grant permissions to the content URI if necessary.
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, download the PDF file
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(url);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setTitle(pdfName + "_snapay.pdf is Downloaded");
                request.setDescription("Downloading a file");
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, pdfName + "_snapay.pdf");
                downloadManager.enqueue(request);
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), pdfName + "_snapay.pdf");
                DialogUtils.showOKAlert(activity, "invalid", "PDF file Downloaded and saved in " + file, new Bundle(), true, alertYesNoListener);
            } else {
                // Permission denied, show a message to the user
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            }
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

    void clearPage() {
        page = 1;
        list.clear();
        // dialogLoading.show();
        adapterSavings.notifyDataSetChanged();
        callApi();

    }

    void pdfDown() {
        Map<String, String> map = new HashMap<>();
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "generate_pdf", map, "pdf", new Bundle(), apiCallbacks);
    }

    void AddSavingsApi(String amt) {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("amount", amt);
        map.put("customerid", CustomerID);
        map.put("Customerrefid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_EMP_PREFIX) + CustomerID);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "addsaving", map, "addsavingNew", new Bundle(), apiCallbacks);
    }

    void AddSavingsApi(String custId, String amt) {
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("amount", amt);
        map.put("customerid", custId);
        map.put("Customerrefid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_EMP_PREFIX) + custId);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "addsaving", map, "addsaving", new Bundle(), apiCallbacks);
    }


    void callNewApi() {
        setLoading(false);
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("currentPage", "" + page);

        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "savings", map, "list", new Bundle(), apiCallbacks);
    }

    void callApi() {
        setLoading(false);
        Map<String, String> map = new HashMap<>();
        map.put("employeeid", MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_USER_ID));
        map.put("currentPage", "" + page);
        dialogLoading.show();
        NetworkController.getInstance().callApiPost(activity, APPConstants.MAIN_URL + "savings", map, "list", new Bundle(), apiCallbacks);
    }
}