package com.example.plannect.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.plannect.MainActivity;
import com.example.plannect.R;

public class NotificationHelper extends ContextWrapper {


    Uri defaultNotificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    private static final String CHANNEL_ID_1 = "new_post_notification_id";
    private static final String CHANNEL_NAME_1 = "New Post Notification";
    private static final int REQUEST_CODE = 69;
    NotificationManager notificationManager;


    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }



public  NotificationManager getNotificationManager(){

        if(notificationManager==null){
            notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }

        return  notificationManager;

}

    private void createChannel() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID_1,CHANNEL_NAME_1,NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getNotificationManager().createNotificationChannel(notificationChannel);
        }
    }


   public NotificationCompat.Builder getPostNotification(){

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pi = PendingIntent.getActivity(this,REQUEST_CODE,i,PendingIntent.FLAG_UPDATE_CURRENT);

        return  new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID_1)
                .setContentTitle("New post")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pi)
                .setSound(defaultNotificationSound)
                .setAutoCancel(true);

    }


}



