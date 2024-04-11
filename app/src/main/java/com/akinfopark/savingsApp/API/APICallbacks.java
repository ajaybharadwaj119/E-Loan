package com.akinfopark.savingsApp.API;

import android.os.Bundle;

import org.json.JSONObject;

public interface APICallbacks {
    void taskProgress(String tag, int progress, Bundle bundle);
    void taskFinish(APIStatus apiStatus, String tag, JSONObject response, String message, Bundle bundle);
    //void taskFail(String tag, String response, Bundle bundle);
}
