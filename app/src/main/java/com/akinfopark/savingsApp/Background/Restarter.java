package com.akinfopark.savingsApp.Background;

import static android.view.View.GONE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class Restarter extends BroadcastReceiver {
    Handler handler = new Handler();
    Runnable runnable;
    Context context1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Broadcast Listened", "Service tried to stop");
        context1 = context;
        handler.postDelayed(runnable = new Runnable() {
            public void run() {

              /*  if (MyPrefs.getInstance().getString(UserData.KEY_SWITCH_OFF).equalsIgnoreCase("switchOn")) {
                    setenable();
                }

                if (MyPrefs.getInstance().getString(UserData.KEY_NOTIF).equalsIgnoreCase("Lock")) {
                    Log.i("Valuee1", "no");
                    enablePop();
                } else if (MyPrefs.getInstance().getString(UserData.KEY_NOTIF).equalsIgnoreCase("Unlock the device")) {
                    dismissPop();
                }*/

                handler.postDelayed(runnable, 8000);
            }
        }, 30000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, MyBackgroundService.class));
        } else {
            context.startService(new Intent(context, MyBackgroundService.class));
        }
    }

    void enablePop() {
      /*  if (MyPrefs.getInstance().getString(UserData.KEY_LOCK).equalsIgnoreCase("")) {
            setenable();
        }*/
    }

    void setenable() {
       /* if (GlobalActionBarService.enabled) {
            MyPrefs.getInstance().putBoolean("enabled", true);
            Intent intent = new Intent("visible");
            if (true)
                intent.putExtra("visible", VISIBLE);
            else
                intent.putExtra("visible", GONE);
            LocalBroadcastManager.getInstance(context1.getApplicationContext()).sendBroadcast(intent);
        } else {
            MyPrefs.getInstance().putBoolean("enabled", false);
        }
        MyPrefs.getInstance().putString(UserData.KEY_LOCK, "locked");
        MyPrefs.getInstance().putString(UserData.KEY_SWITCH_OFF, "switchOff");*/
    }

    void dismissPop() {
        stopFunctions();
        Intent intent = new Intent("visible");
        intent.putExtra("visible", GONE);
        LocalBroadcastManager.getInstance(context1.getApplicationContext()).sendBroadcast(intent);
    }

    void stopFunctions() {
    /*    MyPrefs.getInstance().putBoolean("enabled", false);
        MyPrefs.getInstance().putString(UserData.KEY_LOCK, "");*/
        handler.removeCallbacks(runnable);

    }

}
