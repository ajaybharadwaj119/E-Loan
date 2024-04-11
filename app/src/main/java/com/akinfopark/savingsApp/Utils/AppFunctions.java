package com.akinfopark.savingsApp.Utils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.fragment.app.Fragment;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppFunctions {
    Context context;

    public static void setAlertDialogBg(Dialog dialog, Drawable drawable) {
        dialog.getWindow().setBackgroundDrawable(drawable);
    }

    public static void hideAndShowViews(List<View> views, View view) {
        for (View v : views
        ) {
            v.setVisibility(GONE);
        }
        view.setVisibility(VISIBLE);
    }

    public static String getCurrentTimeChat() {
        return DataFormats.dateFormatTimeStampChat.format(new Date());
    }

    public static long getCurrentTimeStampChat() {
        return System.currentTimeMillis() / 1000L;
    }

    public static boolean validImageFormatChat(String filePath, String fileType) {
        final String extension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
        if (fileType.equalsIgnoreCase("image"))
            return (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png"));
        return true;
    }

    /*public static void hideAndShowViews(List<ExpandableLayout> views, ExpandableLayout view) {
        for (ExpandableLayout v : views
        ) {
            v.setExpanded(false);
        }
        view.setExpanded(true);
    }*/

    public static JSONObject get0object(JSONObject response) throws JSONException {
        JSONArray jsonArray = response.getJSONArray("result");
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        return jsonObject;
    }

    public static String getAPIMsg(JSONObject response) throws JSONException {
        JSONObject jsonObject = response.getJSONObject("message");
        return jsonObject.getString("msg");
    }

    public static void shareText(Activity activity, Fragment fragment, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        activity.startActivity(shareIntent);
    }

    public static String getDrivingMode(String vehicleType) {
        if (vehicleType.equalsIgnoreCase("auto")) {
            return "driving";
        } else if (vehicleType.equalsIgnoreCase("bike")) {
            return "bicycle";
        }
        return "driving";
    }


    public static void vibrate() {

    }

}
