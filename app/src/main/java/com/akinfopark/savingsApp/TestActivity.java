package com.akinfopark.savingsApp;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.akinfopark.savingsApp.API.APICallbacks;
import com.akinfopark.savingsApp.API.APIStatus;
import com.akinfopark.savingsApp.API.UserData;
import com.akinfopark.savingsApp.Background.MyForegroundService;
import com.akinfopark.savingsApp.Utils.DialogUtils;
import com.akinfopark.savingsApp.Utils.GpsTracker;
import com.akinfopark.savingsApp.Utils.MyPrefs;
import com.akinfopark.savingsApp.Utils.NetworkController;
import com.akinfopark.savingsApp.contact.ContactFetcher;
import com.akinfopark.savingsApp.databinding.ActivityTestBinding;
import com.akinfopark.savingsApp.db.DatabaseHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TestActivity extends AppCompatActivity {
    ActivityTestBinding binding;
    private GpsTracker gpsTracker;
    String latitude = "", longitude = "", contact = "";
    public static Activity activity;
    AlertDialog dialogLoading;
    DatabaseHelper databaseHelper;
    private static final int CONTACT_PERMISSION_REQUEST_CODE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private Handler handler;
    private Runnable runnable;
    private static final int DELAY = 10000; // 10 seconds in milliseconds


    APICallbacks apiCallbacks = new APICallbacks() {
        @Override
        public void taskProgress(String tag, int progress, Bundle bundle) {

        }

        @Override
        public void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle) {
            DialogUtils.dismissLoading(dialogLoading, null, null);

            if (apiStatus == APIStatus.SUCCESS) {

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        dialogLoading = DialogUtils.createLoading(this);


       /* Intent serviceIntent = new Intent(this, LocationUpdateService.class);
        startService(serviceIntent);*/

        databaseHelper = new DatabaseHelper(getApplicationContext());
        //  MyPrefs.getInstance(getApplicationContext()).putBoolean("fetch", false);

        // Check if the permission is not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_CONTACTS},
                    CONTACT_PERMISSION_REQUEST_CODE);
        } else {
            askForLocationPermission();
        }

        // Check if the permission is not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }



        binding.button2.setOnClickListener(v -> {
            contact = String.valueOf(databaseHelper.getAllData());
            Log.i("Log Contact", contact);
            callApi();
        });

        binding.button3.setOnClickListener(v -> {
            getLocation();

        });

        handler = new Handler();


        Intent serviceIntent = new Intent(this, MyForegroundService.class);
        serviceIntent.putExtra("activity_package", getPackageName());
        serviceIntent.putExtra("activity_class", MainActivity.class.getName());
        ContextCompat.startForegroundService(this, serviceIntent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        runnable = new Runnable() {
            @Override
            public void run() {
                // Your function to be called every 10 seconds
               // Toast.makeText(TestActivity.this, "Function called!", Toast.LENGTH_SHORT).show();
                getLocation();

                // Schedule the next run with a delay
                handler.postDelayed(runnable, DELAY);
            }
        };

        handler.postDelayed(runnable, DELAY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); // Stop scheduling the task

        /*Intent serviceIntent = new Intent(this,LocationUpdateService.class);
        startService(serviceIntent);*/

        Log.i("APPPPPPP", "RUNNING: ");
  //getLocation();
    }


    public void getLocation() {
        gpsTracker = new GpsTracker(getApplicationContext());
        if (gpsTracker.canGetLocation()) {
            latitude = String.valueOf(gpsTracker.getLatitude());
            longitude = String.valueOf(gpsTracker.getLongitude());

            binding.lat.setText("Latitude is : " + latitude);
            binding.longe.setText("Longitude is : " + longitude);

            MyPrefs.getInstance(activity).putString(UserData.LAT,latitude);
            MyPrefs.getInstance(activity).putString(UserData.LONG,longitude);


        /*  tvLatitude.setText(String.valueOf(latitude));
            tvLongitude.setText(String.valueOf(longitude));*/
//            Toast.makeText(gpsTracker, "Location Fetched", Toast.LENGTH_SHORT).show();
           // callLocationApi();
        } else {
           gpsTracker.showSettingsAlert();
        }
    }

    private void askForLocationPermission() {
        // Check if the permission is not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Location permission is already granted
            // Do something with the location permissions
        }
    }

    void callLocationApi() {
        Map<String, String> map = new HashMap<>();
        map.put("lat", MyPrefs.getInstance(activity).getString(UserData.LAT));
        map.put("long", MyPrefs.getInstance(activity).getString(UserData.LONG));
        NetworkController.getInstance().callApiPost(activity, "http://akprojects.co/savelatlong.php", map, "location", new Bundle(), apiCallbacks);
    }

    void callLocationApi2(String lat,String lon) {

        Map<String, String> map = new HashMap<>();
        // dialogLoading.show();
        map.put("lat", lat);
        map.put("long", lon);
        NetworkController.getInstance().callApiPost(activity, "http://akprojects.co/savelatlong.php", map, "location", new Bundle(), apiCallbacks);
    }
    void callApi() {

        Map<String, String> map = new HashMap<>();
        dialogLoading.show();
        map.put("jsonvalues", contact);
        NetworkController.getInstance().callApiPost(activity, "https://akprojects.co/savecontact.php", map, "add", new Bundle(), apiCallbacks);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CONTACT_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    // Do something with the contact permissions
                    askForLocationPermission();

                    if (!MyPrefs.getInstance(getApplicationContext()).getBoolean("fetch")) {
                        ContactFetcher contactFetcher = new ContactFetcher(getApplicationContext());
                        contactFetcher.fetchContacts();
                        // Toast.makeText(getApplicationContext(), "12", Toast.LENGTH_SHORT).show();
                        MyPrefs.getInstance(getApplicationContext()).putBoolean("fetch", true);

                    } else {
                        //  Toast.makeText(getApplicationContext(), "21", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    // Permission denied
                    // Disable the functionality that depends on this permission.
                }
                return;
            }
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    // Do something with the location permissions
                } else {
                    // Permission denied
                    // Disable the functionality that depends on this permission.
                }
                return;
            }

        }
    }


}