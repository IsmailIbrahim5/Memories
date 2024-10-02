package com.idea.memories.Classes.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;

import java.util.Calendar;

import androidx.core.app.JobIntentService;

public class TimerService extends JobIntentService {

    private static final int JOB_ID = 5;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, TimerService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork( Intent intent) {
        //Make an alarm that checks everyday at 12 AM
        Calendar firingCal = Calendar.getInstance();
        firingCal.set(Calendar.HOUR_OF_DAY, 12);
        firingCal.set(Calendar.MINUTE, 0);
        firingCal.set(Calendar.SECOND, 0);

        long intendedTime = firingCal.getTimeInMillis();

        PendingIntent dateCheckerService = PendingIntent.getService(this, 0, new Intent(this, DateCheckerService.class), 0);

        AlarmManager alarmManager = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, intendedTime, AlarmManager.INTERVAL_DAY, dateCheckerService);
    }

}
