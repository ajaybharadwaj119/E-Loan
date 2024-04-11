package com.akinfopark.savingsApp.firebase;

import static com.akinfopark.savingsApp.Utils.CommonConstants.SHARED_PREF_NAME;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.akinfopark.savingsApp.API.UserData;
import com.akinfopark.savingsApp.R;
import com.akinfopark.savingsApp.Utils.MyPrefs;
import com.akinfopark.savingsApp.activity.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

;

/**
 * Created by AK INFOPARK on 25-09-2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    int i = 0, not_id = 0;
    int count = 0;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        String fbToken = MyPrefs.getInstance(getApplicationContext()).getString(UserData.KEY_FB_TOKEN);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(fbToken, s);
        editor.apply();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        count++;

        //MyPrefs.getInstance(getApplicationContext()).putInt(UserData.KEY_NOTIF_COUNT,count);

        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(getBaseContext());
        Map<String, String> receivedMap = remoteMessage.getData();

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String icon = "";
        String type = "";


        sendNotification(title, body, icon, type, receivedMap);


    }

    private void sendNotification(String title, String messageBody, String image, String type, Map<String, String> map) {

       /* int counter = MyPrefs.getInstance(getApplicationContext()).getInt("notifycount");
        MyPrefs.getInstance(getApplicationContext()).putInt("notifycount", counter + 1);*/

        Bitmap bitmap = null;
        try {
            URL url = new URL(image);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(MyFirebaseMessagingService.this);
        Intent intent;
        stackBuilder.addParentStack(SplashActivity.class);
        Bundle bundle = new Bundle();
        intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        } else {
            pendingIntent = PendingIntent.getActivity(this,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        }

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_test);
        NotificationCompat.BigPictureStyle s = new NotificationCompat.BigPictureStyle().bigPicture(bitmap);
        s.setSummaryText(messageBody);
        String CHANNEL_ID = "my_channel_01";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.icon_test)
                .setDefaults(Notification.DEFAULT_ALL)
                .setLargeIcon(icon)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messageBody));
        //.setStyle(s);

        NotificationManager notificationManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager = getSystemService(NotificationManager.class);
        } else {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
      /*  if (notificationManager != null) {
            notificationManager.notify(not_id, notificationBuilder.build());
        }
        not_id++;*/
    }

}
