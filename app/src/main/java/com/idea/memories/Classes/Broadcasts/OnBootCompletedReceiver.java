package com.idea.memories.Classes.Broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.idea.memories.Classes.Services.TimerService;

public class OnBootCompletedReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    TimerService timerCheck = new TimerService();
    TimerService.enqueueWork(context, new Intent());
  }
}
