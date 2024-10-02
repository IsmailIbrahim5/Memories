package com.idea.memories.Classes.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.idea.memories.Classes.MemoriesGenerator;
import com.idea.memories.Classes.Memory;
import com.idea.memories.R;

import com.idea.memories.Views.Activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import androidx.core.app.NotificationManagerCompat;

public class DateCheckerService extends BroadcastReceiver {

    private Calendar calendar;
    private MemoriesGenerator memoriesGenerator;
    private Memory memory;
    private ArrayList<Memory> memories;
    private Notification notification = null;
    private static final int NOTIFICATION_ID = 16;
    private static final String CHANNEL_ID = "MEMORIES_CHANNEL";
    @Override
    public void onReceive(Context context, Intent intent) {
        calendar = Calendar.getInstance();
        memoriesGenerator = new MemoriesGenerator(context);
        memories = memoriesGenerator.generateMain();
        memoriesGenerator.putIndexes(memories);
        Random random  = new Random();
        int randomNum = random.nextInt(9) +1;
        String notificationTitle;
        for (int i = 0; i < memories.size(); i++) {
            memory = memories.get(i);

            String[] date = memory.getDate().split("/");
            Log.e(getClass().getName()  , date[0] + " + " + calendar.get(Calendar.YEAR) + " + " + date[1] + " + " + calendar.get(Calendar.MONTH) + " + " + date[2] + " + " + calendar.get(Calendar.DAY_OF_MONTH));

            //Check if it has been one year or more on any memory
            if (Integer.valueOf(date[0]) < calendar.get(Calendar.YEAR) & Integer.valueOf(date[1]) == calendar.get(Calendar.MONTH)+1 && Integer.valueOf(date[2]) == calendar.get(Calendar.DAY_OF_MONTH)) {
                //Send Notification
                int diff = calendar.get(Calendar.YEAR) - Integer.valueOf(date[0]);

                if(diff == 1)
                    notificationTitle = memory.getTitle() + " (1 Year Ago)";
                else
                    notificationTitle = memory.getTitle() + " ("+diff+" Years Ago)";

                Bitmap notificationIcon = null;
                if(memory.getMainPhoto() != null ) {
                    notificationIcon = BitmapFactory.decodeFile(getPath(context, memory.getMainPhoto().getPath()));
                }

                sendNotification(notificationTitle ,
                        getDesc(randomNum , context) ,
                        memory.getPosition(),
                        false,
                        notificationIcon,
                        context);
            }
        }

        if(notification == null) {
            //if notification = null means that there is no year notification to send, so we go to weeks notifications
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date = simpleDateFormat.parse(memories.get(0).getDate());
                Date temp;
                memory = memories.get(0);
                for (int i = 0; i < memories.size(); i++) {
                    temp = simpleDateFormat.parse(memories.get(i).getDate());
                    if (temp.getTime() > date.getTime()) {
                        date = temp;
                        memory = memories.get(i);
                    }
                }

                long diff = calendar.getTime().getTime() - date.getTime();

                if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) % 7 == 0 & TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) != 0) {
                    randomNum =+ 10;

                    long weeks = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) / 7;
                    if (weeks > 1)
                        notificationTitle = "It has been " + weeks + " weeks since you created a memory";
                    else
                        notificationTitle = "It has been one week since you created a memory";

                    sendNotification(notificationTitle ,
                            getDesc(randomNum , context) ,
                            -2,
                            true,
                            null,
                            context);

                }
            } catch (Exception e) {
            }
        }
    }


    private String getPath(Context context , Uri uri)
    {
        if (uri == null)
            return null ;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        }
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }

    private String getDesc(int num , Context context){
        String desc = "";
        switch (num){
            case 1 : desc = context.getResources().getString(R.string.notification_description_1);
                    break;
            case 2 : desc = context.getResources().getString(R.string.notification_description_2);
                break;
            case 3 : desc = context.getResources().getString(R.string.notification_description_3);
                break;
            case 4 : desc = context.getResources().getString(R.string.notification_description_4);
                break;
            case 5 : desc = context.getResources().getString(R.string.notification_description_5);
                break;
            case 6 :  desc = context.getResources().getString(R.string.notification_description_6);
                break;
            case 7 : desc = context.getResources().getString(R.string.notification_description_7);
                break;
            case 8 : desc= context.getResources().getString(R.string.notification_description_8);
                break;
            case 9 : desc= context.getResources().getString(R.string.notification_description_9);
                break;
            case 10 : desc= context.getResources().getString(R.string.notification_description_10);
                break;
            case 11 : desc = context.getResources().getString(R.string.notification_description_11);
                break;
            case 12 : desc = context.getResources().getString(R.string.notification_description_12);
                break;
            case 13 : desc = context.getResources().getString(R.string.notification_description_13);
                break;
            case 14 : desc = context.getResources().getString(R.string.notification_description_14);
                break;
            case 15 :  desc = context.getResources().getString(R.string.notification_description_15);
                break;
            case 16 : desc = context.getResources().getString(R.string.notification_description_16);
                break;
            case 17 : desc= context.getResources().getString(R.string.notification_description_17);
                break;
            case 18 : desc= context.getResources().getString(R.string.notification_description_18);
                break;
            case 19 : desc= context.getResources().getString(R.string.notification_description_19);
                break;
        }
        return desc;
    }

    private void sendNotification(String notificationTitle , String notificationDescription , int memoryPosition , boolean add , Bitmap icon , Context context){
        Intent mainActivity = new Intent(context, MainActivity.class);
        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        mainActivity.putExtra("memoryPosition" , memoryPosition);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainActivity, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentTitle(notificationTitle)
                .setContentText(notificationDescription)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(context.getResources().getColor(R.color.sky))
                .setStyle(new Notification.BigTextStyle());

        if(add)
                notificationBuilder.addAction(R.drawable.ic_add, "Create a new Memory", pendingIntent);
        else
            notificationBuilder.setContentIntent(pendingIntent);

        if(icon != null) {
            notificationBuilder.setLargeIcon(icon);
            notificationBuilder.setStyle(new Notification.BigPictureStyle().bigPicture(icon).bigLargeIcon((Bitmap) null));
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        //this part is important cuz u have to give the notification a channel id for devices API > 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(CHANNEL_ID);
            notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT));
        }

        notification = notificationBuilder.build();

        //the notification Id is to access this specific notification whenever you want
        notificationManager.notify(NOTIFICATION_ID, notification);

    }
}
