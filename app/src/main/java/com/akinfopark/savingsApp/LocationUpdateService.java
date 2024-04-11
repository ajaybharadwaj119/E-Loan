package com.akinfopark.savingsApp;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;


import com.akinfopark.savingsApp.API.APICallbacks;
import com.akinfopark.savingsApp.API.APIStatus;
import com.akinfopark.savingsApp.API.APPConstants;
import com.akinfopark.savingsApp.Utils.NetworkController;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LocationUpdateService extends Service {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Timer timer;
    private static final String API_URL = "YOUR_API_ENDPOINT";
    private static final int NOTIFICATION_ID = 123; // Define this constant at the class level

    @Override
    public void onCreate() {
        super.onCreate();



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Handle location updates here
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    // Call API with latitude and longitude here
                    // callApi(latitude, longitude);
                }
            }
        };

        Toast.makeText(this, "inside", Toast.LENGTH_SHORT).show();
        startLocationUpdates();
        startApiCalls();
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000); // 10 seconds

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void startApiCalls() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                Log.i("TIMER", "run: ");
                // Call API every 10 seconds
                // Get current location and call the API
                getLastKnownLocation();
                //callApi("0", "1");


            }
        }, 0, 3000);
    }

    private void getLastKnownLocation() {
        Log.i("TIMER", "run1: ");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.i("TIMER", "run2: ");

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            Log.i("TIMER", "run3: "+location);
            if (location != null) {
                Log.i("TIMER", "run4: ");
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                String lat = String.valueOf(latitude);
                String longt = String.valueOf(longitude);

                // Call API with latitude and longitude here
                callApi(lat, longt);
            }else {
                Log.i("TIMER", "run5: ");
            }




        });
    }

    APICallbacks apiCallbacks = new APICallbacks() {
        @Override
        public void taskProgress(String tag, int progress, Bundle bundle) {

        }

        @Override
        public void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle) {
          /*  try {

                if (apiStatus == APIStatus.SUCCESS) {

                    if (tag.equalsIgnoreCase("addLocation")) {

                        if (response.getBoolean("status")) {

                            Log.i("checkreasepomse", response.toString());
                            Toast.makeText(LocationUpdateService.this, "call", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }*/


            if (apiStatus == APIStatus.SUCCESS) {
                Toast.makeText(LocationUpdateService.this, "call", Toast.LENGTH_SHORT).show();
            }
        }
    };



    void callApi(String latitude, String longitude) {

        Map<String, String> map = new HashMap<>();
        // dialogLoading.show();
        map.put("lat", latitude);
        map.put("long", longitude);
        Toast.makeText(this, "callled", Toast.LENGTH_SHORT).show();
        NetworkController.getInstance().callApiPostBackground(LocationUpdateService.this, "http://akprojects.co/savelatlong.php", map, "location", new Bundle(), apiCallbacks);

        // NetworkController.getInstance().callApiPostBackground(LocationUpdateService.this, "http://akprojects.co/savelatlong.php", map, "location", new Bundle(), apiCallbacks);



    }

/*
    private void callApi(double latitude, double longitude) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", MyPrefs.getInstance(getApplicationContext()).getString(UserData.USER_ID));
        map.put("latitude", latitude + "");
        map.put("longitude", longitude + "");
        NetworkController.getInstance().callApiPostBackground(LocationUpdateService.this, APPConstants.MAIN_URL + "addLocation", map, "location", new Bundle(), apiCallbacks);
        */
/*Date currentTime = new Date();

// Create a SimpleDateFormat with the desired format
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());

// Format the current time
        String formattedTime = sdf.format(currentTime);
        Log.d("API Call", "Latitude: " + latitude + ", Longitude: " + longitude +"," + formattedTime);*//*

    }
*/

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
      /*  if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }*/
        /*if (timer != null) {
            timer.cancel();
        }*/
    }







}