package com.anuragmalti.iamroot.khanabot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){

        ////Log.e("Message ",remoteMessage.toString());

        if(remoteMessage.getData().isEmpty()) {
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }else{
            showNotification(remoteMessage.getData());
        }

//        Intent intent = new Intent(this,OrderHistory.class);
//        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
////        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
////                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
//        NotificationCompat.Builder notificationBuilder;
//        notificationBuilder = new NotificationCompat.Builder(this,"12345678910111213");
//        notificationBuilder.setContentTitle("Khanabot");
//        notificationBuilder.setTicker("Khanabot brings you something very delicious...");
//        notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
//        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
//        notificationBuilder.setAutoCancel(true);
//        notificationBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
//        notificationBuilder.setLights(Color.RED, 3000, 3000);
//        notificationBuilder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.margun));
//        notificationBuilder.setContentIntent(pendingIntent);
//        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
//        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0,notificationBuilder.build());
//        ////Log.e("notification","with id 123456789wala");

    }

    private void showNotification(Map<String, String> data) {
        String title = data.get("title").toString();
        String body = data.get("body").toString();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "Khanabot";
        Intent intent = new Intent(this,HomePage.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "KhanabotNotification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Notification");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Khanabot")
                .setContentText(body)
                .setTicker("Khanabot brings you something very delicious...")
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setLights(Color.RED, 3000, 3000)
                .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.margun))
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH);
        notificationManager.notify(0,notificationBuilder.build());
        Log.e("notification","with id 123456789wala");

    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "Khanabot";
        Intent intent = new Intent(this,HomePage.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "KhanabotNotification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Notification");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Khanabot")
                .setContentText(body)
                .setTicker("Khanabot brings you something very delicious...")
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setLights(Color.RED, 3000, 3000)
                .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.margun))
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH);
        notificationManager.notify(0,notificationBuilder.build());
        Log.e("notification","with id 123456789wala");


    }

}
