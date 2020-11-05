package com.raspberry.practicalparent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class TimerNotificationServiceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("Stop Timer")) {
            TimerNotificationService.cancelTimer();

            SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("timerRunning", false);
            editor.apply();
        }
    }
}
