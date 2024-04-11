package com.akinfopark.savingsApp;

import android.content.ComponentCallbacks2;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

public class AppLifecycleHandler implements ComponentCallbacks2 {
    private boolean isInBackground;
    @Override
    public void onTrimMemory(int i) {
        if (i == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            isInBackground = true;
        } else {
            isInBackground = false;
        }
    }

    public boolean isInBackground() {
        return isInBackground;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration configuration) {

    }

    @Override
    public void onLowMemory() {

    }
}
