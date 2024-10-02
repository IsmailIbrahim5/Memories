package com.idea.memories.Classes.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import com.idea.memories.R;

import com.idea.memories.Classes.UserData;
import com.idea.memories.Views.Activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.core.app.NotificationManagerCompat;

public class FirebaseMessagesService extends FirebaseMessagingService {
    private static String CHANNEL_ID = "MEMORIES_CHANNEL";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        UserData userData = new UserData(getApplicationContext());

        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        mainActivity.putExtra("memoryPosition" , -2);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, mainActivity, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteMessage.Notification remoteMessageNotification = remoteMessage.getNotification();

        String title = remoteMessageNotification.getTitle().replace("userName" , userData.getData().getUserName());
        String desc = remoteMessageNotification.getBody().replace("userName" , userData.getData().getUserName());

        Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(desc)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(getApplication().getResources().getColor(R.color.sky))
                .setAutoCancel(true)
                .setStyle(new Notification.BigTextStyle());
        try {
            if(remoteMessage.getData().get("create").equalsIgnoreCase("true")){
                notificationBuilder.addAction(R.drawable.ic_add, "Create a new Memory", pendingIntent);
            }
        }catch (Exception e){
        }
        try {
            URL image= new URL(remoteMessageNotification.getImageUrl().toString());
            HttpURLConnection connection = (HttpURLConnection)image.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Bitmap imagee = BitmapFactory.decodeStream(inputStream);
            if(remoteMessage.getData().get("largeIcon").equalsIgnoreCase("true"))
                notificationBuilder.setLargeIcon(imagee);
            notificationBuilder.setStyle(new Notification.BigPictureStyle().bigPicture(imagee).bigLargeIcon((Bitmap) null));
            connection.disconnect();
            inputStream.close();

        }catch (Exception e){
            Log.e(getClass().getName() , e.getMessage());
        }


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(CHANNEL_ID);
            notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT));
        }

        Notification notification = notificationBuilder.build();

        notificationManager.notify(16, notification);
    }
}
